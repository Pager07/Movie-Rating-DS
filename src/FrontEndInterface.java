import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FrontEndInterface extends Remote {
    public String sayHello() throws RemoteException;

    public String processQuery(String[] operations, int primaryServer) throws RemoteException;

    public String processUpdate(String[] updateOperations, int primaryServer) throws RemoteException;

    public void processUpdates(int[] servers, String[] updateOperations) throws RemoteException;

    public ServerStatus getServerStatus(int serverNumber) throws RemoteException;

    public String setServerStatus(int serverNumber, ServerStatus status) throws RemoteException;

    public String getTimeStamps(int serverNumber) throws RemoteException;

    public int createNewUser(int primaryServer) throws RemoteException;
}
