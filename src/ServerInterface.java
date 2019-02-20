import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerInterface extends Remote {

    public String sayHello() throws RemoteException;

    public String processQuery(TimeStamp qPrev) throws RemoteException;

    public String processUpdate(TimeStamp qPrev) throws RemoteException;

    public ServerStatus getServerStatus() throws RemoteException;
}
