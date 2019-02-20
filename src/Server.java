import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server implements ServerInterface{
    private TimeStamp valueTS;
    private ServerStatus status = ServerStatus.ACTIVE;
    private int number;

    public Server(int number, int numServers){
        this.number = number;
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
    public String processQuery(TimeStamp qPrev) {
//       if valueTS < q.prev then the replica manager is missing some updates.
        if (valueTS.isBehindTimeStamp(qPrev)) {
            return "Replica Manager" + number + " is behind";
        }
        return "Replica Manager " + number + " can process query";
    }

    @Override
    public String processUpdate(TimeStamp qPrev) {
        if (valueTS.isBehindTimeStamp(qPrev)) {
            return "Replica Manager " + number + " can't process update";
        }
        valueTS.incrementFrontEnd(number);
        return "Replica Manager " + number + " processed update";
    }

    //   Gets a specific movie and its ratings
    private void getMovie(){}


    //   Updates an already submitted rating
    private void updateRating(){ }


    //  Submit a new movie rating
    private void submitRating(){}

}
