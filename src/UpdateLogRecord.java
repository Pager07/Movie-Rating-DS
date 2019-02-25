import java.io.Serializable;

public class UpdateLogRecord implements Serializable {
    TimeStamp ts, qPrev;
    String[] operations;
    String frontEndIdentifier;
    int replicaNumber;

    UpdateLogRecord(int replicaNumber, TimeStamp ts, TimeStamp qPrev, String frontEndIdentifier, String[] operations) {
        this.ts = ts;
        this.replicaNumber = replicaNumber;
        this.qPrev = qPrev;
        this.frontEndIdentifier = frontEndIdentifier;
        this.operations = operations;
    }

    @Override
    public String toString() {
        return "{" + ts + ", " + qPrev + ", "+ operations +
                ", " + frontEndIdentifier + ", " + replicaNumber + '}';
    }
}
