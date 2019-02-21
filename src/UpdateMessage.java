import java.io.Serializable;

public class UpdateMessage implements Serializable {
    public TimeStamp updateID, qPrev;
    public String updateOperations;

    public UpdateMessage(TimeStamp updateID, TimeStamp qPrev, String updateOperations) {
        this.updateID = updateID;
        this.qPrev = qPrev;
        this.updateOperations = updateOperations;
    }


}
