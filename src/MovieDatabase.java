import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class MovieDatabase {
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
        if (!movieIDs.containsKey(movieName)) return "Movie Not Found";
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
/*
    Put MovieRecord(movieName, year, genres) in movieDatabase as key movieID
    Put movieID as key movieName and value movieID
    Goal: split the line into an String[4] = [movieID, movieName, movieYear, movieGenres]
 */
    private void fillMovieDatabase(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(getFile("movies")));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] components = line.split(","), elements = new String[4];
                if (components.length == 3) {
                    elements[0] = components[0];
                    removeYear(components[1], elements);
                    elements[3] = components[components.length - 1];
                    movieDatabase.put(Integer.parseInt(elements[0]), new MovieRecord(elements[1], elements[2], elements[3]));
                    movieIDs.put(elements[1], Integer.parseInt(elements[0]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //    Assumption: Given an already processed string
    private void removeYear(String processedString, String[] elements) {
        String[] components = processedString.split("\\(");
        for (int i = 0; i < components.length - 1; i++) {
            if (i == 0 ) elements[1] = components[i];
            else elements[1] += '(' + components[i];
        }
        elements[1] = elements[1].substring(0, elements[1].length() - 1);
        elements[2] = components[components.length - 1].substring(0, components[components.length - 1].length() - 1);
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
            getGenres(genres);
            this.year = year;
        }

        private void getGenres(String genres) {
            if (genres.contains("(")) {
                this.genres = new String[] {genres.substring(1, genres.length() - 1)};
            }
            else {
                this.genres = genres.split(",");
            }
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
}
