import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FrontEndInterface extends Remote{
    public String sayHello() throws RemoteException;

    public String processQuery(String[] operations) throws RemoteException;

    public String processUpdate(String[] updateOperations) throws RemoteException;

    public void processUpdates(int[] servers, String[] updateOperations) throws RemoteException;

    public String getServerStatus(int serverNumber) throws RemoteException;

    public String setServerStatus(int serverNumber, ServerStatus status) throws RemoteException;

    public void setPrimaryServer(int serverNumber) throws RemoteException;

    public String getTimeStamps(int serverNumber) throws RemoteException;

    public int createNewUser() throws RemoteException;
}
