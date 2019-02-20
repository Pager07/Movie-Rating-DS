import java.io.Serializable;

public class QueryPackage implements Serializable {
    public TimeStamp timeStamp;
    public String message;

    public QueryPackage(TimeStamp timeStamp, String message) {
        this.timeStamp = timeStamp;
        this.message = message;
    }
}
