import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FrontEndInterface extends Remote{
    public String sayHello() throws RemoteException;

    public String connectToServer(int serverNumber) throws RemoteException;

    public String processQuery(int serverNumber) throws RemoteException;

    public String processUpdate(int serverNumber) throws RemoteException;
}
