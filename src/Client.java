import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static void main(String[] args){
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8000);

            // Lookup the remote object "Hello" from registry
            // and create a stub for it
            FrontEndInterface stub = (FrontEndInterface) registry.lookup("FrontEnd");
            // Invoke a remote method
            System.out.println("Front End: " + stub.sayHello() + "\n");
            boolean isClientConnected = true;
            Scanner scanner = new Scanner(System.in);
            while (isClientConnected) {
                System.out.println("What Operations Would You Like To Perform: ");
                int serverNum;
                String response = scanner.next().toLowerCase();
                if (response.matches("hello")) {
                    System.out.println("Which Server?");
                    serverNum = Integer.parseInt(scanner.next());
                    System.out.println("Server: " + stub.connectToServer(serverNum));
                } else if (response.matches("update")){
                    System.out.println("Which Server?");
                    serverNum = Integer.parseInt(scanner.next());
                    System.out.println("Server: " + stub.processUpdate(serverNum));
                } else if (response.matches("query")) {
                    System.out.println("Which Server?");
                    serverNum = Integer.parseInt(scanner.next());
                    System.out.println("Server: " + stub.processQuery(serverNum));
                } else {
                    isClientConnected = false;
                    for (int i = 0; i < PublicInformation.numServers; i++) {
                        registry.unbind("Server" + i);
                    }
                    registry.unbind("FrontEnd");
                }
            }
        } catch (RemoteException e) {
            System.out.println("Remote Exception");
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
