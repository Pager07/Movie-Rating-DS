import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class UpdateManager {
    private int replicaNumber;
//    Contains the timestamps of other RMs. Updated whenever you obtain a gossip message.
    private HashMap<Integer, TimeStamp> timeStampTable;
//    Contains the unique front end identifier of ID (thus all we need to do is check the uID of an update)
    private HashSet<String> executedOperationTable;
    private ArrayList<UpdateLogRecord> updateLog;
    public ArrayList<String> updates;

    public UpdateManager(int replicaNumber){
        this.replicaNumber = replicaNumber;
        timeStampTable = new HashMap<>();
        executedOperationTable = new HashSet<>();
        updateLog = new ArrayList<>();
        updates = new ArrayList<>();
    }


    /*
    Returns true if we can merge valueTS with ts (which signifies that update can be applied)
     */
    public boolean addToLog(TimeStamp ts, TimeStamp qPrev, String frontEndIdentifier, String operations){
        updateLog.add(new UpdateLogRecord(ts, qPrev, frontEndIdentifier, operations));
        if (ts.isLessThan(qPrev)) {
//            apply update
            updates.add(operations);
//            add performed operations into the executed Operations Log
            executedOperationTable.add(frontEndIdentifier);
            for (String update : updates) System.out.println(update);
            return true;
        }
        return false;
    }


//  checks if the String uid is in the executedOperations table.
    public boolean inLog(String uid){
        return executedOperationTable.contains(uid);
    }


    protected class UpdateLogRecord {
        private TimeStamp ts, qPrev;
        private String operations, frontEndIdentifier;
        private int replicaNumber;

        public UpdateLogRecord(TimeStamp ts, TimeStamp qPrev, String frontEndIdentifier, String operations) {
            this.ts = ts;
            this.replicaNumber = UpdateManager.this.replicaNumber;
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
}
