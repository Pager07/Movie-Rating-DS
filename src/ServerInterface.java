import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerInterface extends Remote {

    public String sayHello() throws RemoteException;

    public QueryPackage processQuery(TimeStamp qPrev) throws RemoteException;

    public TimeStamp processUpdate(TimeStamp qPrev) throws RemoteException;

    public ServerStatus getServerStatus() throws RemoteException;
}
