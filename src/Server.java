import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class Server implements ServerInterface {
    //    ValueTS: Contains all updates that have been applied (applied by RM)
//    replicaTS: Contains all updates that have been accepted by RM (keeps track of all the other updates from Gossip)
    private TimeStamp valueTS, replicaTS;
    private ServerStatus status = ServerStatus.ACTIVE;
    private int serverNumber, updates = 0;
    private UpdateManager updateManager;
    private MovieDatabase database;

    public Server(int serverNumber, int numServers) {
        this.serverNumber = serverNumber;
        valueTS = new TimeStamp(numServers);
        replicaTS = new TimeStamp(numServers);
        database = new MovieDatabase();
        updateManager = new UpdateManager(serverNumber, database);
    }

    @Override
    public QueryPackage processQuery(TimeStamp qPrev, String[] queryOperations) {
//       if valueTS < q.prev then the replica manager is missing some updates.
        if (!qPrev.isLessThan(valueTS)) {
            System.out.println("Current Server" + serverNumber + " Behind Front End");
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", 8000);
                for (int i = 0; i < registry.list().length - 1; i++) {
                    if (i != serverNumber) {
                        ServerInterface server = (ServerInterface) registry.lookup("Server" + i);
                        server.sendGossip(this);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Processed Gossip");
        }
        return new QueryPackage(replicaTS, database.queryDatabase(queryOperations));
    }

    @Override
    public TimeStamp processUpdate(TimeStamp qPrev, String[] operations, String frontEndIdentifier) {
        if (updateManager.inLog(frontEndIdentifier)) return qPrev;
//        Increment Time Stamps as update is valid
        replicaTS.incrementFrontEnd(serverNumber);
        valueTS.incrementFrontEnd(serverNumber);
        TimeStamp ts = qPrev.getUniqueID(replicaTS, serverNumber);
        if (updateManager.addToLog(ts, qPrev, frontEndIdentifier, operations)) {
            replicaTS.combineTimeStamps(ts);
        }
        updates++;
        if (updates == PublicInformation.requiredUpdates) {
            System.out.println("Gossiping\n********************");
            updates = 0;
            gossip();
        }
        return ts;
    }
    

    @Override
    public ServerStatus getServerStatus() {
        return status;
    }

    @Override
    public void setServerStatus(ServerStatus status) {
        this.status = status;
    }

    @Override
    public void processGossip(ArrayList<UpdateLogRecord> log, TimeStamp senderTimeStamp, int senderNumber) {
        updateManager.processGossip(log, senderTimeStamp, replicaTS, valueTS, senderNumber);
        valueTS = updateManager.timeStampTable.get(serverNumber);
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8000);
            for (int i = 0; i < registry.list().length - 1; i++) {
                ServerInterface stub = (ServerInterface) registry.lookup("Server" + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendGossip(ServerInterface requestingServer) throws RemoteException {
        requestingServer.processGossip(updateManager.updateLog, replicaTS, serverNumber);
    }

    private void gossip() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8000);
            for (int i = 0; i < registry.list().length - 1; i++) {
                if (i != serverNumber) {
                    ServerInterface stub = (ServerInterface) registry.lookup("Server" + i);
                    stub.processGossip(updateManager.updateLog, valueTS, serverNumber);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int createNewUser() throws RemoteException {
        return database.createNewUser();
    }

    //    Testing Methods
    @Override
    public String getValueTS() throws RemoteException {
        return valueTS.toString();
    }

    @Override
    public String getReplicaTS() throws RemoteException {
        return replicaTS.toString();
    }

    @Override
    public String getTimeStamps() throws RemoteException {
        return "ValueTS: " + valueTS.toString() + "\nReplicaTS: " + replicaTS.toString();
    }
}
