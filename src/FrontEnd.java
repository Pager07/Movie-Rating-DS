import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;


public class FrontEnd implements FrontEndInterface {
    private TimeStamp qPrev = new TimeStamp(PublicInformation.numServers);

    public String sayHello() {
        return "Front End Successfully Connected to Client!";
    }

    @Override
    public String processQuery(String[] operations, int primaryServer) {
        ServerInterface stub = locateServer(primaryServer);
        if (stub != null) {
            try {
                QueryPackage queryResponse = stub.processQuery(qPrev, operations);
                qPrev = queryResponse.timeStamp;
                return queryResponse.message;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return "Couldn't Find Server";
    }

    @Override
    public int createNewUser(int primaryServer) throws RemoteException {
        ServerInterface stub = locateServer(primaryServer);
        if (stub != null) {
            try {
                return stub.createNewUser();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    @Override
    public String processUpdate(String[] updateMessage, int primaryServer) throws RemoteException {
        ServerInterface stub = locateServer(primaryServer);
        if (stub != null) {
            qPrev.combineTimeStamps(stub.processUpdate(qPrev, updateMessage, UUID.randomUUID().toString()));
            return "Update Processed For Replica Manager " + primaryServer;

        }
        return "Couldn't Find Server";
    }

    @Override
    public void processUpdates(int[] servers, String[] updateOperations) throws RemoteException {
        String uniqueID = UUID.randomUUID().toString();
        for (int server : servers) {
            ServerInterface stub = locateServer(server);
            if (stub != null) {
                qPrev.combineTimeStamps(stub.processUpdate(qPrev, updateOperations, uniqueID));
                System.out.println("Update Processed At Replica" + server + "");
            }
        }
    }

    @Override
    public ServerStatus getServerStatus(int serverNumber) {
        ServerInterface stub = locateServer(serverNumber);
        if (stub != null) {
            try {
                return stub.getServerStatus();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String setServerStatus(int serverNumber, ServerStatus status) {
        ServerInterface stub = locateServer(serverNumber);
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

    //   Finding Stub Methods
    /*
    Check the primary stub to perform actions, if it is not available call reroute method.
     */
    private ServerInterface locateServer(int serverNumber) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8000);
            return (ServerInterface) registry.lookup("Server" + serverNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    //    Testing Methods
    @Override
    public String getTimeStamps(int serverNumber) throws RemoteException {
        ServerInterface stub = locateServer(serverNumber);
        if (stub != null) {
            return stub.getTimeStamps();
        }
        return "Couldn't Find Replica" + serverNumber;
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
