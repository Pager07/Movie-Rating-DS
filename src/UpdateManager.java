import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class UpdateManager {
    private int replicaNumber;
//    Contains the timestamps of other RMs. Updated whenever you obtain a gossip message.
    private HashMap<Integer, TimeStamp> timeStampTable;
//    Contains the unique front end identifier of ID (thus all we need to do is check the uID of an update)
    private HashSet<TimeStamp> executedOperationTable;
    private ArrayList<UpdateLogRecord> updateLog;

    public UpdateManager(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public void addToLog(){

    }

    private boolean inLog(){
        return true;
    }


    protected class UpdateLogRecord {
        private TimeStamp timeStamp, qPrev, frontEndIdentifier;
        private String operations;
        private int replicaNumber;

        public UpdateLogRecord(TimeStamp valueTS, TimeStamp qPrev, TimeStamp frontEndIdentifier, String operations) {
            this.replicaNumber = UpdateManager.this.replicaNumber;
            this.timeStamp = qPrev.getUniqueID(valueTS, replicaNumber);
            this.qPrev = qPrev;
            this.frontEndIdentifier = frontEndIdentifier;
            this.operations = operations;
        }
    }
}
