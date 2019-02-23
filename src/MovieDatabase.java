import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class MovieDatabase {
    // TODO: 23/02/2019 Return Appropriate Response when no such movie exists
    private HashMap<Integer, MovieRecord> movieDatabase;
    private HashMap<Integer, MovieRating> movieRating;
//    Keep This in for now, maybe in the future experiment with making the Keys the name of the Movie.
    private HashMap<String, Integer> movieIDs;
    private HashMap<Integer, UserRatingManager> userRatings;

    public MovieDatabase() {
        this.movieDatabase = new HashMap<>();
        this.movieRating = new HashMap<>();
        this.userRatings = new HashMap<>();
        this.movieIDs = new HashMap<>();
        fillMovieDatabase();
        fillRating();
    }

//    Returns a String In The Form: Movie (Rating) Genres
    public String queryDatabase(String movieName) {
        int movieID = movieIDs.get(movieName);
        StringBuilder builder = new StringBuilder();
        builder.append(movieName);
        builder.append(" (").append(movieDatabase.get(movieID).year).append(")");
        builder.append("\nRating: ").append(movieRating.get(movieID).getRating()).append("\nGenres: ");
        for (String genre : movieDatabase.get(movieID).genres) {
            builder.append(genre).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
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
                if (line.length() < 5) break;
                System.out.println(line);
                if (line.contains("\"")) {
                    elements = processQuotation(line);
                }
                else {
                    String[] components = line.split(",");
                    elements = new String[4];
                    elements[0] = components[0];
                    removeYear(elements, components[1], false);
                    elements[3] = components[2];
                }
                movieDatabase.put(Integer.parseInt(elements[0]), new MovieRecord(elements[1], elements[2], elements[3]));
                movieIDs.put(elements[1], Integer.parseInt(elements[0]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    Assumption: String is already processed, just need to remove year
    private void removeYear(String[] elements, String line, boolean isParenthesis) {
        String[] components = line.split("\\(");
        elements[2] = components[components.length - 1].replaceAll("\\)", "");
        elements[1] = "";
        for (int i = 0; i < components.length - 1; i++) {
            if (i < 1) {
                elements[1] += components[i].trim();
            } else {
                elements[1] += (isParenthesis ? "(" : "") + components[i].trim();
            }
        }
    }

    private String[] processQuotation(String line) {
        if (line.split("\\(").length > 2) return processParenthesis(line);
        String[] elements = new String[4];
        String[] splitString = line.split(",");
        elements[0] = splitString[0];
//        Processing Middle Portion of String
        elements[1] = "";
        for (int i = 1; i< splitString.length - 1; i++) {
            elements[1] += splitString[i];
        }
//        removes "
        elements[1] = elements[1].replaceAll("\"", "");
//        Process End of Array
        removeYear(elements, elements[1], false);
        elements[3] = splitString[splitString.length - 1];
        return elements;
    }

    private String[] processParenthesis(String line){
        String[] elements = new String[4], components = line.split("\"");
        elements[0] = components[0].substring(0, components[0].length() - 1);
        elements[1] = processMiddle(components[1]);
        elements[3] = components[2].substring(1);
        removeYear(elements, elements[1], true);
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
        private String movieName, year;
        private String[] genres;

        MovieRecord(String movieName, String year, String genres) {
            this.movieName = movieName;
            this.genres = genres.split("\\|");
            this.year = year;
        }

        @Override
        public String toString() {
            return movieName + " (" + year + ") " + "\nGenres: " + Arrays.toString(genres);
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

    public static void main(String[] args) {
        MovieDatabase database = new MovieDatabase();
        for (String name : database.movieIDs.keySet()) {
            if(name.length() == 0) {
                System.out.println(name + ", " + database.movieIDs.get(name));
            }
        }
    }
}
