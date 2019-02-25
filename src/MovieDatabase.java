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

    public int createNewUser() {
        return userRatings.size() + 1;
    }

//    Purpose: main method that Server calls to query database
//    Movie Rating: [movieName]
//    All user ratings: [userID, -]
//    Specific Rating: [userID, MovieName, -]
    public String queryDatabase(String[] queryOperations) {
        if (queryOperations.length == 2) {
            return getUserRatings(Integer.parseInt(queryOperations[0]));
        } else if (queryOperations.length == 3) {
//            Get Specific Review For Client
            if (!movieIDs.containsKey(queryOperations[1])) return "No Review Exists";
            int movieID = movieIDs.get(queryOperations[1]), userID = Integer.parseInt(queryOperations[0]);
            return userRatings.get(userID).getUserRatingFor(movieID);
        }
        return findMovie(queryOperations[0]);

    }

    //    Purpose: Find All Information of Specific Movie
    private String findMovie(String movieName) {
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

    //    Purpose: get all ratings that was sent in by the user
    private String getUserRatings(int userID) {
        if (!userRatings.containsKey(userID)) return "User Contains No Reviews";
        return userRatings.get(userID).toString();
    }

//    Purpose: main method that Server calls to Process an Update
//    Review will be sent as a [userID, movieName, review]
//    addMovie Will Be Sent as: [movieName, year, Genres, review, userID]
    public void processUpdate(String[] updateOperations) {
        if (updateOperations.length == 3) {
//            Adding Movie Review
            if (movieIDs.containsKey(updateOperations[1])) {
                int userID = Integer.parseInt(updateOperations[0]), movieID = movieIDs.get(updateOperations[1]);
                movieRating.get(movieID).addRating(Float.parseFloat(updateOperations[2]));
                userRatings.get(userID).addRating(updateOperations[1], movieID, Float.parseFloat(updateOperations[2]));
            } else {
                System.out.println("No Movie in Database");
            }
        } else {
//            Adding Movie To Database
            if (!movieIDs.containsKey(updateOperations[0])) {
                int id = movieIDs.size() + 1;
                movieIDs.put(updateOperations[0], id);
                movieDatabase.put(id, new MovieRecord(updateOperations[0], updateOperations[1], updateOperations[2]));
                movieRating.put(id, new MovieRating(Float.parseFloat(updateOperations[3])));
                userRatings.get(Integer.parseInt(updateOperations[4])).addRating(updateOperations[0], id, Float.parseFloat(updateOperations[3]));
                System.out.println("Processed Update");
            }
        }
    }



    // TODO: 24/02/2019 On Shutdown, get servers to gossip
    public void shutDownServer() {
        saveDatabase();
        saveRatings();
    }

    //    When Servers Close Down and To Save State of Machine
    private void saveDatabase() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(getFile("movies")));
            for (int key : movieDatabase.keySet()) {
                MovieRecord record = movieDatabase.get(key);
                writer.write(key + "," + record.movieName + " (" + record.year + "),");
                StringBuilder genre = new StringBuilder();
                if (record.genres[0].matches("no genres listed"))
                    genre = new StringBuilder("(" + record.genres[0] + ")");
                else {
                    for (String recordedGenre : record.genres) {
                        genre.append(recordedGenre).append("|");
                    }
                    genre.deleteCharAt(genre.length() - 1);
                }
                writer.write(genre.toString() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveRatings() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(getFile("ratings")));
            StringBuilder builder = new StringBuilder();
            for (int user : userRatings.keySet()) {
                builder.append(userRatings.get(user).toString());
            }
            builder.deleteCharAt(builder.length() - 1);
            writer.write(builder.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
        - Check if movie name exists in the ID
        - Generate Unique ID for Movie
        - Add Movie To Database
        - Add MovieName, MovieID to movieIDs
     */
    public String addMovie(String movieName, String year, String genres) {
        if (movieIDs.containsKey(movieName)) return "Movie Already Exists in Database";
////        Generate Unique ID
//        int uniqueID =
        return "1";
    }

    public void addUserRating(int userID, int movieID, float rating) {
        userRatings.get(userID).addRating(movieDatabase.get(movieID).movieName, movieID, rating);
    }


    //    Fill MovieDatabase Methods
/*
    Put MovieRecord(movieName, year, genres) in movieDatabase as key movieID
    Put movieID as key movieName and value movieID
    Goal: split the line into an String[4] = [movieID, movieName, movieYear, movieGenres]
 */
    private void fillMovieDatabase() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(getFile("movies")));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] components = line.split(","), elements = new String[4];
                elements[0] = components[0];
                removeYear(components[1], elements);
                elements[3] = components[components.length - 1];
                movieDatabase.put(Integer.parseInt(elements[0]), new MovieRecord(elements[1], elements[2], elements[3]));
                movieIDs.put(elements[1], Integer.parseInt(elements[0]));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //    Assumption: Given an already processed string
    private void removeYear(String processedString, String[] elements) {
        String[] components = processedString.split("\\(");
        for (int i = 0; i < components.length - 1; i++) {
            if (i == 0) elements[1] = components[i];
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
            while ((line = reader.readLine()) != null) {
                String[] components = line.split(",");
                int userID = Integer.parseInt(components[0]), movieID = Integer.parseInt(components[1]);
                float rating = Float.parseFloat(components[2]);
                if (userRatings.get(userID) == null) {
                    userRatings.put(userID, new UserRatingManager());
                }
                userRatings.get(userID).addRating(movieDatabase.get(movieID).movieName, movieID, rating);
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
        File file = new File(System.getProperty("user.dir") + "/Movie Database/" + fileName + ".csv");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            ;
        }
        return file;
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
                this.genres = new String[]{genres.substring(1, genres.length() - 1)};
            } else {
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
