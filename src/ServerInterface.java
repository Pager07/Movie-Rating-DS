import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerInterface extends Remote {

    public QueryPackage processQuery(TimeStamp qPrev) throws RemoteException;

    public TimeStamp processUpdate(TimeStamp qPrev, String update, String uniqueID) throws RemoteException;

    public ServerStatus getServerStatus() throws RemoteException;

    public void setServerStatus(ServerStatus status) throws RemoteException;

    public void processGossip(ArrayList<UpdateLogRecord> log, TimeStamp senderTimeStamp, int senderNumber) throws RemoteException;

    public String getUpdates() throws RemoteException;
}
