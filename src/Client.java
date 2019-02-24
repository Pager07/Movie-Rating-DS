import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    // TODO: 23/02/2019 Adding an update to client
    // TODO: 23/02/2019 Assign Client A Random ID When They Start The Program (simulates they are already on)
    // TODO: 23/02/2019 Creates a completely new ID if they want to

    // TODO: 24/02/2019 Finish Adding Review
    // TODO: 24/02/2019 Finish
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
                String response = scanner.nextLine().toLowerCase();
                if (response.matches("update")){
                    System.out.println("Update: ");
                    System.out.println("Server: " + stub.processUpdate(scanner.nextLine()));
                } else if (response.matches("updates")) {
                    System.out.println("Update: ");
                    String message = scanner.nextLine();
                    System.out.println("To Which Servers?");
                    String[] userServers = scanner.nextLine().replaceAll("\\s+", "").split(",");
                    int[] servers = new int[userServers.length];
                    for (int i = 0; i < userServers.length; i++) {
                        servers[i] = Integer.parseInt(userServers[i]);
                    }
                    stub.processUpdates(servers, message);
                } else if (response.matches("timestamps")) {
                    System.out.println("Which Server");
                    System.out.println(stub.getTimeStamps(Integer.parseInt(scanner.nextLine())));
                } else if (response.matches("query")) {
                    System.out.println("Which Movie Would You Like To Find Out?: [Sabrina, Toy Story]");
                    System.out.println("Server: " + stub.processQuery(scanner.nextLine()));
                } else if (response.matches("getstatus")) {
                    System.out.println("Which Server?");
                    int serverNum = Integer.parseInt(scanner.nextLine());
                    System.out.println("Server: " + stub.getServerStatus(serverNum));
                } else if (response.matches("setstatus")) {
                    System.out.println("Which Server?");
                    int serverNum = Integer.parseInt(scanner.nextLine());
                    ServerStatus status = setServerStatus(scanner);
                    System.out.println("Server: " + stub.setServerStatus(serverNum, status));
                } else if (response.matches("switch")) {
                    System.out.println("Which Server?");
                    int serverNum = Integer.parseInt(scanner.nextLine());
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

    private static ServerStatus setServerStatus(Scanner scanner) {
        System.out.println("Select a Status: [Active, Overloaded, Offline]");
        String stringStatus = scanner.nextLine().toUpperCase();
        ServerStatus status;
        if (stringStatus.matches("ACTIVE")) {
            status = ServerStatus.ACTIVE;
        } else if (stringStatus.matches("OVERLOADED")) {
            status = ServerStatus.OVERLOADED;
        } else {
            status = ServerStatus.OFFLINE;
        }
        return status;
    }


}
