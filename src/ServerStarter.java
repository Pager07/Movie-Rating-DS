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
            }
            System.out.println("Servers 0 - " + (PublicInformation.numServers - 1) + " ready");
        } catch (Exception e) {
            System.out.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
        System.out.println("---------------------\n\n");
    }
}
