import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MovieDatabase {
    private HashMap<Integer, MovieRecord> movieDatabase;
    private HashMap<Integer, MovieRating> movieRating;
    private HashMap<Integer, UserRatingManager> userRatings;

    public MovieDatabase() {
        this.movieDatabase = new HashMap<>();
        this.movieRating = new HashMap<>();
        this.userRatings = new HashMap<>();
        fillMovieDatabase();
        fillRating();
    }


//    Get Methods For Movie Database
    public String[] getMovieGenre(int movieID) {
        return movieDatabase.get(movieID).genres;
    }

    public String getMovieName(int movieID) {
        return movieDatabase.get(movieID).movieName;
    }


//    Get Method for Overall Movie Rating
    public Float getMovieRating(int movieID) {
        return movieRating.get(movieID).getRating();
    }


//    get method for user specific information.
    public String getUserRatingFor(int userID, int movieID) {
        return userRatings.get(userID).getUserRating(movieID);
    }

    public String getAllUserRatings(int userID) {
        return userRatings.get(userID).toString();
    }

    public void addUserRating(int userID, int movieID, float rating, int timeStamp) {
        userRatings.get(userID).addRating(movieID, rating, timeStamp);
    }

    public void deleteUserRating(int userID, int movieID) {
        userRatings.get(userID).removeRating(movieID);
    }

//    Fill MovieDatabase Methods
    private void fillMovieDatabase(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(getFile("movies")));
            String line;
            String[] elements;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\"")) {
                    elements = processQuotation(line);
                }
                else {
                    elements = line.split(",");
                }
                movieDatabase.put(Integer.parseInt(elements[0]), new MovieRecord(elements[1], elements[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] processQuotation(String line) {
        if (line.split("\\(").length > 2) return processParenthesis(line);
        String[] elements = new String[3];
        String[] splitString = line.split(",");
        elements[0] = splitString[0];

//        Processing Middle Portion of String
        splitString[1] = splitString[1].substring(1);
        elements[1] = "";
        splitString[splitString.length - 2] = splitString[splitString.length - 2].substring(0, splitString.length);
        for (int i = splitString.length - 2; i > 0; i--) {
            elements[1] += splitString[i] + " ";
        }
        elements[1] = elements[1].substring(0, elements[1].length() - 1);
//        Process End of Array
        elements[2] = splitString[splitString.length - 1];
        return elements;
    }

    private String[] processParenthesis(String line){
        String[] elements = new String[3], components = line.split("\"");
        elements[0] = components[0].substring(0, components[0].length() - 1);
        elements[1] = processMiddle(components[1]);
        elements[2] = components[2].substring(1);
        return elements;
    }

    private String processMiddle(String middle) {
        String[] components = middle.split("\\(");
        components[0] = processComma(components[0]);
        for (int i = 1; i < components.length; i++) {
            components[i] = "(" + processComma(components[i]) + ")";
        }
        return String.join(" ", components);
    }

    private String processComma(String line) {
        line = line.replaceAll("\\)", "");
        String[] components = line.split(",");
        StringBuilder processed = new StringBuilder();
        for (int i = components.length - 1; i >= 0; i--){
            processed.append(components[i].trim()).append(" ");
        }
        processed.deleteCharAt(processed.length() - 1);
        return processed.toString();
    }

//    Get Ratings Method
//    userID, movieID, rating, timeStamp
    private void fillRating() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(getFile("ratings")));
            String line;
            while( (line = reader.readLine()) != null) {
                String[] components = line.split(",");
                int userID = Integer.parseInt(components[0]), movieID = Integer.parseInt(components[1]), time = Integer.parseInt(components[3]);
                float rating = Float.parseFloat(components[2]);
                if (userRatings.get(userID) == null) {
                    userRatings.put(userID, new UserRatingManager(userID));
                }
                userRatings.get(userID).addRating(movieID, rating, time);
//                Put Movie Rating if Absent
                movieRating.putIfAbsent(movieID, new MovieRating(0));
//                Increment Movie Rating
                movieRating.get(movieID).addRating(rating);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


//    Universal Methods
    private File getFile(String fileName) {
        return new File(System.getProperty("user.dir") + "/Movie Database/" + fileName + ".csv");
    }

    private class MovieRecord {
        private String movieName;
        private String[] genres;

        MovieRecord(String movieName, String genres) {
            this.movieName = movieName;
            this.genres = genres.split("\\|");
        }
    }

    private class MovieRating {
        private float sum;
        private int ratings;

        private MovieRating(float rating) {
            sum = rating;
            ratings = 1;
        }

        private void addRating(float rating) {
            sum += rating;
            ratings++;
        }

        private float getRating() {
            return sum / ratings;
        }
    }
}
