import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerStarter {
    public static void main(String[] args) {
        try {
            for (int i = 0; i < PublicInformation.numServers; i++) {
                // Create server object
                Server obj = new Server(i, PublicInformation.numServers);

                // Create remote object stub from server object
                ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(obj, 0);
                // Get registry
                Registry registry = LocateRegistry.getRegistry("localhost", 8000);
                // Bind the remote object's stub in the registry
                registry.bind("Server" + i, stub);

                // Write ready message to console
                System.out.println("Server" + i + " ready");
            }
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}