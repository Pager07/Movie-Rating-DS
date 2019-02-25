import java.io.Serializable;
import java.util.*;

public class UpdateManager implements Serializable {
    //    Contains the timestamps of other RMs. Updated whenever you obtain a gossip message.
    public HashMap<Integer, TimeStamp> timeStampTable;
    public ArrayList<UpdateLogRecord> updateLog;
    private MovieDatabase movieDatabase;
    private int replicaNumber;
    //    Contains the unique front end identifier of ID (thus all we need to do is check the uID of an update)
    private HashSet<String> executedOperationTable;

    public UpdateManager(int replicaNumber, MovieDatabase movieDatabase) {
        this.replicaNumber = replicaNumber;
//        Reference to the Server's Database
        this.movieDatabase = movieDatabase;
        timeStampTable = new HashMap<>();
        for (int i = 0; i < PublicInformation.numServers; i++)
            timeStampTable.put(i, new TimeStamp(PublicInformation.numServers));
        executedOperationTable = new HashSet<>();
        updateLog = new ArrayList<>();
    }


    /*
    Returns true if we can merge valueTS with ts (which signifies that update can be applied)
     */
    public boolean addToLog(TimeStamp ts, TimeStamp qPrev, String frontEndIdentifier, String[] operations) {
        updateLog.add(new UpdateLogRecord(replicaNumber, ts, qPrev, frontEndIdentifier, operations));
        if (qPrev.isLessThan(ts)) {
//            apply update
            movieDatabase.processUpdate(operations);
//            add performed operations into the executed Operations Log
            executedOperationTable.add(frontEndIdentifier);
            timeStampTable.put(replicaNumber, ts);
            return true;
        }
        return false;
    }


    //  checks if the String uid is in the executedOperations table.
    public boolean inLog(String uid) {
        return executedOperationTable.contains(uid);
    }

    public void processGossip(ArrayList<UpdateLogRecord> sentLog, TimeStamp senderTS, TimeStamp replicaTS, TimeStamp valueTS, int senderNumber) {
        System.out.println("Going Through Sent Log:");
//        Going through and adding to own log
        for (UpdateLogRecord record : sentLog) {
            System.out.println(replicaTS + ", " + record.ts + " " + replicaTS.isLessThan(record.ts));
            if (replicaTS.isLessThan(record.ts) && !inLog(record.frontEndIdentifier)) {
                updateLog.add(record);
                valueTS.incrementFrontEnd(record.replicaNumber);
                System.out.println("Added Record to Own Log: " + record);
            }
        }
//        Merging The Time Stamps
        replicaTS.combineTimeStamps(senderTS);
        timeStampTable.put(replicaNumber, replicaTS);
        System.out.println("\nApplying Updates");
        applyUpdates();
        System.out.println("\nDiscarding Old Logs");
        discardOldLogs(senderTS, senderNumber);
    }


    private void applyUpdates() {
        LinkedList<UpdateLogRecord> stableUpdates = new LinkedList<>();
        for (UpdateLogRecord record : updateLog) {
            if (record.ts.isLessThan(timeStampTable.get(replicaNumber)) && !inLog(record.frontEndIdentifier)) {
                boolean notAdded = true;
                for (int i = 0; i < stableUpdates.size(); i++) {
                    if (record.qPrev.getSum() < stableUpdates.get(i).qPrev.getSum()) {
                        stableUpdates.add(i, record);
                        notAdded = false;
                    }
                }
                if (notAdded) {
                    stableUpdates.addLast(record);
                }
            }
        }
        for (UpdateLogRecord record : stableUpdates) {
            movieDatabase.processUpdate(record.operations);
            executedOperationTable.add(record.frontEndIdentifier);
            System.out.println("Added To Updates: " + Arrays.toString(record.operations));
        }
    }

    private void discardOldLogs(TimeStamp senderTS, int senderNumber) {
        timeStampTable.put(senderNumber, senderTS);
        ArrayList<UpdateLogRecord> unapprovedUpdates = new ArrayList<>();
        for (UpdateLogRecord record : updateLog) {
            boolean notProcessedEverywhere = true;
            for (int i = 0; i < PublicInformation.numServers; i++) {
                if (timeStampTable.get(i).valueAt(record.replicaNumber) < record.ts.valueAt(record.replicaNumber)) {
                    notProcessedEverywhere = false;
                    break;
                }
            }
            if (notProcessedEverywhere) unapprovedUpdates.add(record);
        }
        updateLog = unapprovedUpdates;
        System.out.println("Final Update Log: " + Arrays.toString(updateLog.toArray()) + "\n");
    }


}
