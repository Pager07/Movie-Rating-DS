import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;

public class Server implements ServerInterface{
//    ValueTS: Contains all updates that have been applied (though not necessarily processed by RM)
//    replicaTS: Contains all updates that have been accepted by RM (updated whenever an update has been applied to RM)
    private TimeStamp valueTS, replicaTS;
    private ServerStatus status = ServerStatus.ACTIVE;
    private int number, updates = 0;
    private UpdateManager updateManager;

    public Server(int number, int numServers){
        this.number = number;
        valueTS = new TimeStamp(numServers);
        replicaTS = new TimeStamp(numServers);
        updateManager = new UpdateManager(number);
    }

    @Override
    public QueryPackage processQuery(TimeStamp qPrev) {
//       if valueTS < q.prev then the replica manager is missing some updates.
        if (qPrev.isLessThan(valueTS)) {
            return new QueryPackage(replicaTS, Arrays.toString(updateManager.updates.toArray()));
        }
        return new QueryPackage(replicaTS, "Replica Manager " + number + " can't process query");
    }

    @Override
    public TimeStamp processUpdate(TimeStamp qPrev, String operations, String frontEndIdentifier) {
        if (updateManager.inLog(frontEndIdentifier)) return qPrev;
        replicaTS.incrementFrontEnd(number);
        TimeStamp ts = qPrev.getUniqueID(replicaTS, number);
        if (updateManager.addToLog(ts, qPrev, frontEndIdentifier, operations)) {
            valueTS.combineTimeStamps(ts);
        }
        updates++;
        if (updates == PublicInformation.requiredUpdates) {
            System.out.println("Gossiping");
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

    private void gossip() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8000);
            for (int i = 0; i < registry.list().length - 1; i++) {
                if (i != number) {
                    ServerInterface stub = (ServerInterface) registry.lookup("Server" + i);
                    stub.processGossip(updateManager.updateLog, replicaTS, number);
                }
            }
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void processGossip(ArrayList<UpdateLogRecord> log, TimeStamp senderTimeStamp, int senderNumber){
        updateManager.processGossip(log, senderTimeStamp, replicaTS, senderNumber);
        System.out.println(updateManager.updates);
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
