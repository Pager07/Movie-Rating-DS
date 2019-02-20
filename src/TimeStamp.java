import java.util.ArrayList;

public class TimeStamp {
    private ArrayList<Integer> vector;

    public TimeStamp(int size){
        vector = new ArrayList<>();
        for (int i = 0; i < size; i++){
            vector.add(0);
        }
    }


//    Perform Component Wise Maximum (<2, 1, 1> and <1, 3, 1> should become <2,3,1>)
    public void combineTimeStamps(TimeStamp otherTimeStamp) {
        for (int i = 0; i < otherTimeStamp.vector.size(); i++) {
            vector.set(i, otherTimeStamp.vector.get(i) > otherTimeStamp.vector.get(i) ? otherTimeStamp.vector.get(i) : otherTimeStamp.vector.get(i));
        }
    }


    /*
    Should be used when Replica Manager (Back End Server) receives operation request from Front End server. Checks if
    any component in the Replica Manager's is less than q.prev (the timestamp of front end). All components must satisfy
    condition: q.prev <= valueTimeStamp
     */
    public boolean isBehindTimeStamp(TimeStamp otherTimeStamp) {
        for (int i = 0; i < otherTimeStamp.vector.size(); i++) {
            if (otherTimeStamp.vector.get(i) > otherTimeStamp.vector.get(i)) return true;
        }
        return false;
    }


    public void incrementFrontEnd(int index) {
        vector.set(index, vector.get(index) + 1);
    }
}
