import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements ServerInterface{
    private TimeStamp valueTS;
    private ServerStatus status = ServerStatus.ACTIVE;
    private int number;

    public Server(int number, int numServers){
        this.number = number - 1;
        valueTS = new TimeStamp(numServers);

    }

    public String sayHello(){
        return "Replica Manager " + number +  " Successfully Connected to Client!";
    }

    @Override
    public ServerStatus getServerStatus() {
        return status;
    }

    @Override
    public QueryPackage processQuery(TimeStamp qPrev) {
//       if valueTS < q.prev then the replica manager is missing some updates.
        if (valueTS.isBehindTimeStamp(qPrev)) {
            return new QueryPackage(valueTS, "Replica Manager" + (number + 1) + " is missing updates");
        }
        return new QueryPackage(valueTS, "Replica Manager " + (number + 1) + " can process query");
    }

    @Override
    public TimeStamp processUpdate(TimeStamp qPrev) {
        if (valueTS.isBehindTimeStamp(qPrev)) {
            return qPrev;
        }
        valueTS.incrementFrontEnd(number);
        System.out.println(valueTS);
        return valueTS;
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
