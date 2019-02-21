import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;


public class FrontEnd implements FrontEndInterface{
    private TimeStamp qPrev = new TimeStamp(PublicInformation.numServers);
    private int primaryStub = 0;
    // TODO: 21/02/2019 Get them gossiping (update after x updates)
    
    public String sayHello(){
        return "Front End Successfully Connected to Client!";
    }


    @Override
    public String processQuery(){
        ServerInterface stub = locateStub(primaryStub);
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
    public String processUpdate(String updateMessage) {
        ServerInterface stub = locateStub(primaryStub);
        if (stub != null) {
            try {
                TimeStamp valueTS = stub.processUpdate(qPrev, updateMessage, UUID.randomUUID().toString());
                qPrev.combineTimeStamps(valueTS);
                return "Update Processed For Replica Manager " + primaryStub;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return "Couldn't Find Server";
    }

    @Override
    public String getServerStatus(int serverNumber) {
        ServerInterface stub = locateStub(serverNumber);
        if (stub != null) {
            try {
                return "Replica Manager" + serverNumber + " Status: " + stub.getServerStatus();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return "Couldn't Find Server";
    }

    @Override
    public String setServerStatus(int serverNumber, ServerStatus status) {
        ServerInterface stub = locateStub(serverNumber);
        if (stub != null) {
            try {
                stub.setServerStatus(status);
                return "Replica Manager" + serverNumber + " Set Status to: " + stub.getServerStatus();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return "Couldn't Find Server";
    }

    @Override
    public void setPrimaryServer(int serverNumber) throws RemoteException {
        primaryStub = serverNumber;
    }

    //   Finding Stub Methods
    /*
    Check the primary stub to perform actions, if it is not available call reroute method.
     */
    private ServerInterface locateStub(int serverNumber) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8000);
            ServerInterface stub =  (ServerInterface) registry.lookup("Server" + serverNumber);
            if (stub.getServerStatus() != ServerStatus.ACTIVE) {
                rerouteStub(registry, stub);
            }
            return stub;
        } catch (Exception e ) {
            e.printStackTrace();
            return null;
        }
    }


    /*
    Checks all the servers till you find an available server
     */
    private void rerouteStub(Registry registry, ServerInterface stub) {
        try {
            while (stub.getServerStatus() != ServerStatus.ACTIVE) {
                for (int i = 0; i < PublicInformation.numServers; i++) {
                    stub = (ServerInterface) registry.lookup("Server" + i);
                    if (stub.getServerStatus() == ServerStatus.ACTIVE) {
                        primaryStub = i;
                        System.out.println("Rerouted Stub to: " + primaryStub);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
