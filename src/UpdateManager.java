import java.io.Serializable;
import java.util.*;

public class UpdateManager implements Serializable {
    private int replicaNumber;
//    Contains the timestamps of other RMs. Updated whenever you obtain a gossip message.
    public HashMap<Integer, TimeStamp> timeStampTable;
//    Contains the unique front end identifier of ID (thus all we need to do is check the uID of an update)
    private HashSet<String> executedOperationTable;
    public ArrayList<UpdateLogRecord> updateLog;
    public ArrayList<String> updates;

    public UpdateManager(int replicaNumber){
        this.replicaNumber = replicaNumber;
        timeStampTable = new HashMap<>();
        for (int i = 0; i < PublicInformation.numServers; i++) timeStampTable.put(i, new TimeStamp(PublicInformation.numServers));
        executedOperationTable = new HashSet<>();
        updateLog = new ArrayList<>();
        updates = new ArrayList<>();
    }


    /*
    Returns true if we can merge valueTS with ts (which signifies that update can be applied)
     */
    public boolean addToLog(TimeStamp ts, TimeStamp qPrev, String frontEndIdentifier, String operations){
        updateLog.add(new UpdateLogRecord(replicaNumber, ts, qPrev, frontEndIdentifier, operations));
        if (qPrev.isLessThan(ts)) {
//            apply update
            updates.add(operations);
//            add performed operations into the executed Operations Log
            executedOperationTable.add(frontEndIdentifier);
            timeStampTable.put(replicaNumber, ts);
            return true;
        }
        return false;
    }


//  checks if the String uid is in the executedOperations table.
    public boolean inLog(String uid){
        return executedOperationTable.contains(uid);
    }

    public void processGossip(ArrayList<UpdateLogRecord> sentLog, TimeStamp senderTS, TimeStamp recipientTS, int senderNumber) {
        System.out.println("Going Through Sent Log:");
        for (UpdateLogRecord record : sentLog) {
            System.out.println(recipientTS + ", " + record.ts  + " " + recipientTS.isLessThan(record.ts));
            if(recipientTS.isLessThan(record.ts)) {
                recipientTS.combineTimeStamps(record.ts);
                timeStampTable.put(replicaNumber, recipientTS);
                updateLog.add(record);
                System.out.println("Added Record to Own Log: " + record);
            }
        }
        System.out.println("\nApplying Updates");
        applyUpdates(recipientTS);
        System.out.println("\nDiscarding Old Logs");
        discardOldLogs(senderTS, senderNumber);
    }


    private void applyUpdates(TimeStamp recipientTS) {
        LinkedList<UpdateLogRecord> stableUpdates = new LinkedList<>();
        for (UpdateLogRecord record : updateLog) {
            if (record.ts.isLessThan(recipientTS)) {
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
            updates.add(record.operations);
            System.out.println("Added To Updates: " + record.operations);
        }
    }

    private void discardOldLogs(TimeStamp senderTS, int senderNumber) {
        timeStampTable.put(senderNumber, senderTS);
        ArrayList<UpdateLogRecord> unapprovedUpdates = new ArrayList<>();
        for (UpdateLogRecord record : updateLog) {
            boolean notUpdated = false;
            for (int i = 0; i < PublicInformation.numServers; i++) {
                if (timeStampTable.get(i).valueAt(replicaNumber) < record.ts.valueAt(i)) notUpdated = true;
            }
            if (!notUpdated) unapprovedUpdates.add(record);
        }
        updateLog = unapprovedUpdates;
        System.out.println("Final Update Log: " + Arrays.toString(updateLog.toArray()) + "\n");
    }


}
