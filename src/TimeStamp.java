import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Objects;

public class TimeStamp implements Serializable {
    private ArrayList<Integer> vector;

    public TimeStamp(int size){
        vector = new ArrayList<>();
        for (int i = 0; i < size; i++){
            vector.add(0);
        }
    }


//    Perform Component Wise Maximum (<2, 1, 1> and <1, 3, 1> should become <2,3,1>)
    public void combineTimeStamps(TimeStamp otherTimeStamp) {
        for (int i = 0; i < vector.size(); i++) {
            vector.set(i, otherTimeStamp.vector.get(i) > vector.get(i) ? otherTimeStamp.vector.get(i) : vector.get(i));
        }
    }

    public TimeStamp getUniqueID(TimeStamp valueTS, int replicaNumber) {
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
    public boolean isBehindTimeStamp(TimeStamp otherTimeStamp) {
        for (int i = 0; i < otherTimeStamp.vector.size(); i++) {
            if (otherTimeStamp.vector.get(i) > vector.get(i)) return true;
        }
        return false;
    }


    public void incrementFrontEnd(int index) {
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
        for (int i = 0; i < vector.size(); i++) builder.append(vector.get(i) + ",");
        builder.append(">");
        return builder.toString();
    }
}
