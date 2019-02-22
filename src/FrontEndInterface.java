import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FrontEndInterface extends Remote{
    public String sayHello() throws RemoteException;

    public String processQuery() throws RemoteException;

    public String processUpdate(String updateMessage) throws RemoteException;

    public void processUpdates(int[] servers, String updateMessage) throws RemoteException;

    public String getServerStatus(int serverNumber) throws RemoteException;

    public String setServerStatus(int serverNumber, ServerStatus status) throws RemoteException;

    public void setPrimaryServer(int serverNumber) throws RemoteException;


}
