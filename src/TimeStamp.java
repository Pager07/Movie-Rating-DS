import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class TimeStamp implements Serializable {
    private ArrayList<Integer> vector;

    TimeStamp(int size) {
        vector = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            vector.add(0);
        }
    }


    //    Perform Component Wise Maximum (<2, 1, 1> and <1, 3, 1> should become <2,3,1>)
    void combineTimeStamps(TimeStamp otherTimeStamp) {
        for (int i = 0; i < vector.size(); i++) {
            vector.set(i, otherTimeStamp.vector.get(i) > vector.get(i) ? otherTimeStamp.vector.get(i) : vector.get(i));
        }
    }


    //    Get the unique ID of an update from both timestamps (it is the current timestamp with the ith element from valueTS)
    TimeStamp getUniqueID(TimeStamp valueTS, int replicaNumber) {
        TimeStamp uniqueID = new TimeStamp(PublicInformation.numServers);
        for (int i = 0; i < valueTS.vector.size(); i++) {
            uniqueID.vector.set(i, vector.get(i));
        }
        uniqueID.vector.set(replicaNumber, valueTS.vector.get(replicaNumber));
        return uniqueID;
    }


    /*
    Should be used when Replica Manager (Back End Server) receives operation request from Front End server. Checks if
    any component in the Replica Manager's is less than q.prev (the timestamp of front end). All components must satisfy
    condition: q.prev <= valueTimeStamp
     */
    boolean isLessThan(TimeStamp otherTimeStamp) {
        for (int i = 0; i < otherTimeStamp.vector.size(); i++) {
            if (otherTimeStamp.vector.get(i) < vector.get(i)) return false;
        }
        return true;
    }


    void incrementFrontEnd(int index) {
        vector.set(index, vector.get(index) + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeStamp timeStamp = (TimeStamp) o;
        return Objects.equals(vector, timeStamp.vector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vector);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<");
        for (Integer aVector : vector) builder.append(aVector).append(",");
        builder.deleteCharAt(builder.length() - 1);
        builder.append(">");
        return builder.toString();
    }

    public int valueAt(int index) {
        return vector.get(index);
    }

    public int getSum() {
        int total = 0;
        for (Integer aVector : vector) {
            total += aVector;
        }
        return total;
    }
}
