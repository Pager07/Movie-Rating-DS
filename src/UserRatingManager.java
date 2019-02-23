import java.util.LinkedList;

public class UserRatingManager {
    private int userID;
    private LinkedList<UserRatings> ratings;

    UserRatingManager(int userID) {
        this.userID = userID;
        ratings = new LinkedList<>();
    }


//  Behaviour: The linkedlist is sorted such that those with a smaller movieID comes first
//  To Add To Linked List: Iterate through the LinkedList till you find that the current movieID you are trying to add
//                          is less than the current movie ID. If the movieID is the same then the user must be editting
//                           old ID. Else add it to the back of the linked list.
    public void addRating(int movieID, float rating, int timeStamp) {
        for (int i = 0; i < ratings.size(); i++) {
//            Update Review Condition
            if (ratings.get(i).movieID == movieID) {
                ratings.set(i, new UserRatings(movieID, rating, timeStamp));
                return;
            } else if (ratings.get(i).movieID < movieID) {
                ratings.add(i, new UserRatings(movieID, rating, timeStamp));
                return;
            }
        }
        ratings.addLast(new UserRatings(movieID, rating, timeStamp));
    }

//    Iterate through Linked list till you find the same movieID in the list then return.
    public void removeRating(int movieID) {
        for (int i = 0; i < ratings.size(); i++) {
            if (ratings.get(i).movieID == movieID) {
                ratings.remove(i);
                break;
            }
        }
    }

    public String getUserRating(int movieID) {
        for (int i = 0; i < ratings.size(); i++) {
            if (ratings.get(i).movieID == movieID) {
                return Float.toString(ratings.get(i).rating);
            }
        }
        return "User has no review for " + movieID;
    }

    //    userID, movieID, rating, timeStamp
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ratings.size(); i++) {
            builder.append(ratings.get(i).toString()).append("\n");
        }
        return builder.toString();
    }

    private class UserRatings {
        private int timeStamp, movieID;
        private float rating;

        private UserRatings(int movieID, float rating, int timeStamp) {
            this.timeStamp = timeStamp;
            this.movieID = movieID;
            this.rating = rating;
        }

        @Override
        public String toString() {
            return userID + "," + movieID + "," + rating + "," + timeStamp;
        }
    }
}
