import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Client {
    // TODO: 23/02/2019 Adding an update to client
    // TODO: 23/02/2019 Assign Client A Random ID When They Start The Program (simulates they are already on)
    // TODO: 23/02/2019 Creates a completely new ID if they want to

    // TODO: 24/02/2019 Change User Ratings To Be: "Movie Name: Rating"
    private static String userID;

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8000);

            // Lookup the remote object "Hello" from registry
            // and create a frontEnd for it
            FrontEndInterface frontEnd = (FrontEndInterface) registry.lookup("FrontEnd");
            // Invoke a remote method
            System.out.println("Front End: " + frontEnd.sayHello() + "\n");
            boolean isClientConnected = true;
            Scanner scanner = new Scanner(System.in);
            getUserID(scanner, frontEnd);
            while (isClientConnected) {
                System.out.println("What Operations Would You Like To Perform: ");
                String response = scanner.nextLine().toLowerCase();
                if (response.matches("update")) {
                    System.out.println("Server: " + frontEnd.processUpdate(processUpdate(scanner)));
                } else if (response.matches("updates")) {
                    System.out.println("To Which Servers?");
                    String[] userServers = scanner.nextLine().replaceAll("\\s+", "").split(",");
                    int[] servers = new int[userServers.length];
                    for (int i = 0; i < userServers.length; i++) {
                        servers[i] = Integer.parseInt(userServers[i]);
                    }
                    frontEnd.processUpdates(servers, processUpdate(scanner));
                } else if (response.matches("timestamps")) {
                    System.out.println("Which Server");
                    System.out.println(frontEnd.getTimeStamps(Integer.parseInt(scanner.nextLine())));
                } else if (response.matches("query")) {
                    System.out.println(frontEnd.processQuery(processQuery(scanner)));
                } else if (response.matches("getstatus")) {
                    System.out.println("Which Server?");
                    int serverNum = Integer.parseInt(scanner.nextLine());
                    System.out.println("Server: " + frontEnd.getServerStatus(serverNum));
                } else if (response.matches("setstatus")) {
                    System.out.println("Which Server?");
                    int serverNum = Integer.parseInt(scanner.nextLine());
                    ServerStatus status = setServerStatus(scanner);
                    System.out.println("Server: " + frontEnd.setServerStatus(serverNum, status));
                } else if (response.matches("switch")) {
                    System.out.println("Which Server?");
                    int serverNum = Integer.parseInt(scanner.nextLine());
                    System.out.println(Arrays.toString(registry.list()));
                    frontEnd.setPrimaryServer(serverNum);
                } else {
                    isClientConnected = false;
                    int length = registry.list().length - 1;
                    for (int i = 0; i < length; i++) {
                        registry.unbind("Server" + i);
                    }
                    registry.unbind("FrontEnd");
                }
            }
        } catch (RemoteException e) {
            System.out.println("Remote Exception");
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    private static ServerStatus setServerStatus(Scanner scanner) {
        System.out.println("Select a Status: [Active, Overloaded, Offline]");
        String stringStatus = scanner.nextLine().toUpperCase();
        ServerStatus status;
        if (stringStatus.matches("ACTIVE")) {
            status = ServerStatus.ACTIVE;
        } else if (stringStatus.matches("OVERLOADED")) {
            status = ServerStatus.OVERLOADED;
        } else {
            status = ServerStatus.OFFLINE;
        }
        return status;
    }

    //    Checks if the user wants to be assigned an existing user ID or to have an entire userID created for them
    private static void getUserID(Scanner scanner, FrontEndInterface frontEnd) {
        System.out.println("Do You Want To Be An Existing User? (yes/no)");
        String response = scanner.nextLine().toLowerCase().trim();
        if (response.matches("no")) {
            try {
                userID = Integer.toString(frontEnd.createNewUser());
            } catch (RemoteException e) {
                e.printStackTrace();
                userID = Integer.toString(new Random().nextInt(249));
            }
        } else {
            userID = Integer.toString(new Random().nextInt(249));
        }
        System.out.println("Assigned User ID: " + userID);
    }

    //    Update Methods
    // TODO: 24/02/2019 remove movie function
    private static String[] processUpdate(Scanner scanner) {
        System.out.println("What operation would you like to perform: [addMovie, addReview, removeReview]");
        System.out.println("addMovie: Adds movie to the database");
        System.out.println("addReview: Adds review of a movie to database");
        System.out.println("removeReview: Removes a specified user review");
        String response = scanner.nextLine().toLowerCase();
        if (response.matches("addmovie")) return addMovie(scanner);
        else return createRating(scanner);
    }

    //    Review will be sent as a [movieName, review, timeStamp, -]
//    length 4 because it makes it easier to add movie (by distinguishing what operations to do)
    private static String[] createRating(Scanner scanner) {
        String[] review = new String[4];
        System.out.println("What Movie Would You Like To Review");
        review[0] = scanner.nextLine();
        System.out.println("What Rating Out Of Five Would You Give It?");
        review[1] = scanner.next();
        //        Append Random Time Stamp
        review[2] = "892332";
        return review;
    }


    //    Movie Will Be Sent as: movieName, year, Genres
    private static String[] addMovie(Scanner scanner) {
        String[] movie = new String[2];
        System.out.println("What is the Movie's Name:");
        movie[0] = scanner.nextLine();
        System.out.println("When Did The Movie Come Out");
        movie[1] = scanner.nextLine();
        StringBuilder genres = new StringBuilder();
        boolean addingGenres = true;
        while (addingGenres) {
            System.out.println("Genre of The Movie");
            genres.append(scanner.nextLine());
            System.out.println("Continue adding genre? (yes/no)");
            if (scanner.nextLine().toLowerCase().matches("no")) addingGenres = false;
        }
        genres.deleteCharAt(genres.length() - 1);
        movie[1] = genres.toString();
        return movie;
    }


    //    Query Methods
//    Movie Rating: [movieName]
//    All user ratings: [userID, -] (makes it easier to determine what operation to perform later on)
    private static String[] processQuery(Scanner scanner) {
        System.out.println("What Would Query Operation Would You To Perform:");
        System.out.println("movieRating: Finds General Information About a Specific Movie");
        System.out.println("userRatings: List all ratings by the user");
        String response = scanner.nextLine().toLowerCase();
        if (response.matches("movierating")) {
            System.out.println("What Movie Would You Like to Find?: [Sabrina, Toy Story]");
            return new String[]{scanner.nextLine()};
        } else {
            String[] answer = new String[2];
            answer[0] = userID;
            return answer;
        }
    }

}
