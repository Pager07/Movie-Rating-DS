import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

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
        System.out.println("QPrev: " + qPrev + "\nValueTs: " + valueTS);
        if (valueTS.isLessThan(qPrev)) {
            return new QueryPackage(replicaTS, "Replica Manager " + number  + " can process query");
        }
        return new QueryPackage(replicaTS, "Replica Manager" + number + " is missing updates");
    }

    @Override
    public TimeStamp processUpdate(TimeStamp qPrev, String operations, String frontEndIdentifier) {
        if (updateManager.inLog(frontEndIdentifier)) return qPrev;
        replicaTS.incrementFrontEnd(number);
        TimeStamp ts = qPrev.getUniqueID(replicaTS, number);
        if (updateManager.addToLog(ts, qPrev, frontEndIdentifier, operations)) {
            valueTS.combineTimeStamps(ts);
            System.out.println(valueTS + "\n" + ts);
        }
        updates++;
        if (updates == PublicInformation.requiredUpdates) {
            updates = 0;
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", 8000);
                for (int i = 0; i < registry.list().length - 1; i++) {
                    if (i != number) {
                        ServerInterface stub = (ServerInterface) registry.lookup("Server" + i);
                        stub.gossip();
                    }
                }
            } catch (Exception e ) {
                e.printStackTrace();
            }
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
    public void gossip() throws RemoteException {

    }

    //   Gets a specific movie and its ratings
    private void getMovie(){}


    //   Updates an already submitted rating
    private void updateRating(){ }


    //  Submit a new movie rating
    private void submitRating(){}

    public static void main(String[] args) {
        try {
            // Create server object
            Server obj = new Server(5, PublicInformation.numServers);

            // Create remote object stub from server object
            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(obj, 0);
            // Get registry
            Registry registry = LocateRegistry.getRegistry("localhost", 8000);
            // Bind the remote object's stub in the registry
            registry.bind("Server5", stub);

            // Write ready message to console
            System.out.println("Server5 ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

}
