import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MovieDatabase {
    private HashMap<Integer, MovieRecord> movieDatabase;
    private HashMap<Integer, Float> movieRating;

    public MovieDatabase() {
        this.movieDatabase = new HashMap<>();
        this.movieRating = new HashMap<>();
        fillMovieDatabase();
    }

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
        System.out.println("Middle: " + middle);
        ArrayList<String> components = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        boolean seenOpening = false;
        for (char c : middle.toCharArray()) {
            if (c == ',') {
                if (seenOpening) {
                    seenOpening = false;
                    builder.append(')');
                }
                else {
                    builder.append(' ');
                }
                components.add(builder.toString());
                builder = new StringBuilder();
            } else if( c == '(') {
                seenOpening = true;
                components.add(builder.toString());
                builder = new StringBuilder();
            } else if (c == ')') {
                if (builder.charAt(0) == ' ') builder.replace(0, 1,"(");
                else builder.insert(0, "(");
                builder.append(' ');
                components.add(builder.toString());
                builder = new StringBuilder();
            }
            else {
                builder.append(c);
            }
        }
        StringBuilder processed = joinMiddle(components);
        while (processed.charAt(processed.length() - 1) == ' ') {
            processed.deleteCharAt(processed.length() - 1);
        }
        processed.append(")");
        System.out.println("Processed: " + processed.toString() + "\n");
        return processed.toString();
    }

    private File getFile(String fileName) {
        return new File(System.getProperty("user.dir") + "/Movie Database/" + fileName + ".csv");
    }

    private StringBuilder joinMiddle(ArrayList<String> components) {
        StringBuilder processed = new StringBuilder();
        if (components.size() % 2 == 1) {
            for (int i = 0; i < components.size(); i += 2) {
                if (i + 1 < components.size()) {
                    processed.append(components.get(i + 1));
                    processed.append(components.get(i));
                } else {
                    processed.append(components.get(i));
                }
            }
        } else {

        }
        return processed;
    }

    public static void main(String[] args) {
        new MovieDatabase();
    }


    private class MovieRecord {
        private String movieName;
        private String[] genres;

        public MovieRecord(String movieName, String genres) {
            this.movieName = movieName;
            this.genres = genres.split("|");
        }


    }
}
