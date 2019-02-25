import java.util.LinkedList;

public class UserRatingManager {
    private LinkedList<UserRatings> ratings = new LinkedList<>();

    //  Behaviour: The linkedlist is sorted such that those with a smaller movieID comes first
//  To Add To Linked List: Iterate through the LinkedList till you find that the current movieID you are trying to add
//                          is less than the current movie ID. If the movieID is the same then the user must be editting
//                           old ID. Else add it to the back of the linked list.
    public void addRating(String movieName, int movieID, float rating) {
        for (int i = 0; i < ratings.size(); i++) {
//            Update Review Condition
            if (ratings.get(i).movieID == movieID) {
                ratings.set(i, new UserRatings(movieName, movieID, rating));
                return;
            } else if (ratings.get(i).movieID < movieID) {
                ratings.add(i, new UserRatings(movieName, movieID, rating));
                return;
            }
        }
        ratings.addLast(new UserRatings(movieName, movieID, rating));
    }

    public String getUserRatingFor(int movieID) {
        for (int i = 0; i < ratings.size(); i++) {
            if (ratings.get(i).movieID == movieID) {
                return Float.toString(ratings.get(i).rating);
            }
        }
        return "User has no review for " + movieID;
    }

    //    userID, movieID, rating, timeStamp
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ratings.size(); i++) {
            builder.append(ratings.get(i).toString()).append("\n");
        }
        return builder.toString();
    }

    private class UserRatings {
        private String movieName;
        private int movieID;
        private float rating;

        private UserRatings(String movieName, int movieID, float rating) {
            this.movieName = movieName;
            this.movieID = movieID;
            this.rating = rating;
        }

        @Override
        public String toString() {
            return movieName + ": " + rating;
        }
    }
}
