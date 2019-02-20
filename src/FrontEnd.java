import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class FrontEnd implements FrontEndInterface{
    private TimeStamp qPrev = new TimeStamp(PublicInformation.numServers);
//    private int primaryStub = 0;
    // TODO: 20/02/2019 add primary stub

    public String sayHello(){
        return "Front End Successfully Connected to Client!";
    }

    public String connectToServer(int serverNumber){
        ServerInterface stub = locateStub(serverNumber);
        if (stub != null) {
            try {
                return stub.sayHello();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return "Couldn't Find Server.";
    }

    @Override
    public String processQuery(int serverNumber){
        ServerInterface stub = locateStub(serverNumber);
        if (stub != null) {
            try {
                QueryPackage queryResponse = stub.processQuery(qPrev);
                qPrev = queryResponse.timeStamp;
                return queryResponse.message;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return "Couldn't Find Server";
    }

    @Override
    public String processUpdate(int serverNumber) {
        ServerInterface stub = locateStub(serverNumber);
        if (stub != null) {
            try {
                TimeStamp valueTS = stub.processUpdate(qPrev);
                if (valueTS.equals(qPrev)) return "No Update Processed";
                qPrev.combineTimeStamps(valueTS);
                System.out.println(qPrev);
                return "Update Processed For Replica Manager " + serverNumber;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        System.out.println(qPrev);
        return "Couldn't Find Server";
    }


    private ServerInterface locateStub(int serverNumber) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8000);
            return (ServerInterface) registry.lookup("Server" + serverNumber);
        } catch (Exception e ) {
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {
        try {
            // Create server object
            FrontEnd obj = new FrontEnd();

            // Create remote object stub from server object
            FrontEndInterface stub = (FrontEndInterface) UnicastRemoteObject.exportObject(obj, 0);
            // Get registry
            Registry registry = LocateRegistry.getRegistry("localhost", 8000);
            // Bind the remote object's stub in the registry
            registry.bind("FrontEnd", stub);

            // Write ready message to console
            System.out.println("Front End ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
