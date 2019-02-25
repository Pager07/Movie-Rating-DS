import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;

public class Server implements ServerInterface {
    //    ValueTS: Contains all updates that have been applied (applied by RM)
//    replicaTS: Contains all updates that have been accepted by RM (keeps track of all the other updates from Gossip)
    private TimeStamp valueTS, replicaTS;
    private ServerStatus status = ServerStatus.ACTIVE;
    private int number, updates = 0;
    private UpdateManager updateManager;
    private MovieDatabase database;

    public Server(int number, int numServers) {
        this.number = number;
        valueTS = new TimeStamp(numServers);
        replicaTS = new TimeStamp(numServers);
        updateManager = new UpdateManager(number);
        database = new MovieDatabase();
    }

    @Override
    public QueryPackage processQuery(TimeStamp qPrev, String[] queryOperations) {
//       if valueTS < q.prev then the replica manager is missing some updates.
        if (qPrev.isLessThan(valueTS)) {
            return new QueryPackage(replicaTS, database.queryDatabase(queryOperations));
        }
        return new QueryPackage(replicaTS, "Replica Manager " + number + " can't process query");
    }

    @Override
    public TimeStamp processUpdate(TimeStamp qPrev, String[] operations, String frontEndIdentifier) {
        if (updateManager.inLog(frontEndIdentifier)) return qPrev;
//        Increment Time Stamps as update is valid
        replicaTS.incrementFrontEnd(number);
        valueTS.incrementFrontEnd(number);
        TimeStamp ts = qPrev.getUniqueID(replicaTS, number);
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
        System.out.println("Processing Gossip at Replica" + number);
        updateManager.processGossip(log, senderTimeStamp, replicaTS, valueTS, senderNumber);
        System.out.println("Updates at Replica" + number + ": " + Arrays.toString(updateManager.updates.toArray()));
        System.out.println("^^^^^^^^^^^^^^^^\n");
        valueTS = updateManager.timeStampTable.get(number);
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8000);
            System.out.println("---------------------------------------------------------------------------");
            for (int i = 0; i < registry.list().length - 1; i++) {
                ServerInterface stub = (ServerInterface) registry.lookup("Server" + i);
                System.out.println("Replica" + i + " ValueTS:   " + stub.getValueTS());
                System.out.println("Replica" + i + " ReplicaTS: " + stub.getReplicaTS());
            }
            System.out.println("---------------------------------------------------------------------------\n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gossip() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8000);
            for (int i = 0; i < registry.list().length - 1; i++) {
                if (i != number) {
                    ServerInterface stub = (ServerInterface) registry.lookup("Server" + i);
                    stub.processGossip(updateManager.updateLog, valueTS, number);
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

    private void processUpdate(String[] operations) {
        if (operations.length == 2) {
//            remove Review
        } else if (operations.length == 3) {
//            adding Movie
            database.addMovie(operations);
        } else {
//            adding Review
        }
    }

    //    Testing Methods
    @Override
    public String getUpdates() throws RemoteException {
        return Arrays.toString(updateManager.updates.toArray());
    }

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

    //    public static void main(String[] args) {
//        try {
//            // Create server object
//            Server obj = new Server(5, PublicInformation.numServers);
//
//            // Create remote object stub from server object
//            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(obj, 0);
//            // Get registry
//            Registry registry = LocateRegistry.getRegistry("localhost", 8000);
//            // Bind the remote object's stub in the registry
//            registry.bind("Server5", stub);
//
//            // Write ready message to console
//            System.out.println("Server5 ready");
//        } catch (Exception e) {
//            System.err.println("Server exception: " + e.toString());
//            e.printStackTrace();
//        }
//    }

}
