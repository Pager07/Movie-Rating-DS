import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerInterface extends Remote {

    public QueryPackage processQuery(TimeStamp qPrev, String[] queryOperations) throws RemoteException;

    public TimeStamp processUpdate(TimeStamp qPrev, String[] updateOperations, String uniqueID) throws RemoteException;

    public ServerStatus getServerStatus() throws RemoteException;

    public void setServerStatus(ServerStatus status) throws RemoteException;

    public void processGossip(ArrayList<UpdateLogRecord> log, TimeStamp senderTimeStamp, int senderNumber) throws RemoteException;

    public String getValueTS() throws RemoteException;

    public String getReplicaTS() throws RemoteException;

    public String getTimeStamps() throws RemoteException;

    public int createNewUser() throws RemoteException;
}
