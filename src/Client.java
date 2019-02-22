import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
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
                String response = scanner.next().toLowerCase();
                if (response.matches("update")){
                    System.out.println("Update: ");
                    System.out.println("Server: " + stub.processUpdate(scanner.next()));
                } else if (response.matches("updates")) {
                    System.out.println("Update: ");
                    String message = scanner.next();
                    System.out.println("To Which Servers?");
                    String[] userServers = scanner.next().replaceAll("\\s+", "").split(",");
                    int[] servers = new int[userServers.length];
                    for (int i = 0; i < userServers.length; i++) {
                        servers[i] = Integer.parseInt(userServers[i]);
                    }
                    stub.processUpdates(servers, message);
                } else if (response.matches("timestamps")) {
                    System.out.println("Which Server");
                   System.out.println(stub.getTimeStamps(Integer.parseInt(scanner.next())));
                } else if (response.matches("query")) {
                    System.out.println("Server: " + stub.processQuery());
                } else if (response.matches("getstatus")) {
                    System.out.println("Which Server?");
                    int serverNum = Integer.parseInt(scanner.next());
                    System.out.println("Server: " + stub.getServerStatus(serverNum));
                } else if (response.matches("setstatus")) {
                    System.out.println("Which Server?");
                    int serverNum = Integer.parseInt(scanner.next());
                    System.out.println("Select a Status: [Active, Overloaded, Offline]");
                    String stringStatus = scanner.next().toUpperCase();
                    ServerStatus status;
                    if (stringStatus.matches("ACTIVE")) {
                        status = ServerStatus.ACTIVE;
                    } else if (stringStatus.matches("OVERLOADED")) {
                        status = ServerStatus.OVERLOADED;
                    } else {
                        status = ServerStatus.OFFLINE;
                    }
                    System.out.println("Server: " + stub.setServerStatus(serverNum, status));
                } else if (response.matches("switch")) {
                    System.out.println("Which Server?");
                    int serverNum = Integer.parseInt(scanner.next());
                    System.out.println(Arrays.toString(registry.list()));
                    stub.setPrimaryServer(serverNum);
                }   else {
                    isClientConnected = false;
                    int length = registry.list().length - 1;
                    for (int i = 0; i < length; i++) {
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
