import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Main {

    public static ArrayList<Game> allGames = new ArrayList<>();

    public static void main(String[] args) {
        // read inputs from file
        String filename = "game_data.txt";

        ArrayList<Turn> allTurns = readFromFile(filename);

        for (Turn t : allTurns) {
            addToOrCreateGame(t);
        }
        for (Game game : allGames) {
            Collections.sort(game.getTurns());
        }

        // print all games out in order
        printGames();


        // check each game for faulty logic
        ArrayList<Turn> allInvalidTurns = checkAllGames();

        //sort by game ID
        allInvalidTurns.sort(new Comparator<Turn>() {
            @Override
            public int compare(Turn o1, Turn o2) {
                if (o1.getGameSessionID() == o2.getGameSessionID()) return 0;
                return (o1.getGameSessionID() < o2.getGameSessionID()) ? -1 : 1;
            }
        });


        // write output file
        String outputfile = "analyzer_results.txt";
        writeToFile(allInvalidTurns, outputfile);
    }

    public static ArrayList<Turn> checkAllGames() {
        ArrayList<Turn> foundInvalidTurns = new ArrayList<>();

        for (Game game : allGames) {
            Turn invalidTurn = game.checkTurns();
            if (invalidTurn != null)
                foundInvalidTurns.add(invalidTurn);
        }
        return foundInvalidTurns;
    }

    public static void addToOrCreateGame(Turn turn) {
        for (Game game : allGames) {
            if (game.getGameSessionID() == turn.getGameSessionID()) {
                game.addTurn(turn);
                return;
            }
        }
        Game newGame = new Game(turn.getGameSessionID());
        newGame.addTurn(turn);
        allGames.add(newGame);
    }

    public static void printGames() {
        for (Game game : allGames) {
            System.out.println("Game: " + game.getGameSessionID());

            for (Turn turn : game.getTurns()) {
                System.out.println(turn);
            }
            System.out.println("\n");
        }
    }


    public static ArrayList<Turn> readFromFile(String filename) {
        ArrayList<Turn> turns = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    turns.add(new Turn(line));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException | NullPointerException e) {
                    // if line is empty or contains data not in the correct turn format
                    //System.out.println("Invalid Turn constructor call with line: " + line);
                    continue;
                } catch (IllegalArgumentException e) {
                    // when an unknown enum value is passed,
                    // it won't raise a FaultFoundException,
                    // instead the Turn is given an action UNKNOWN,
                    // which will raise a fault later on

                    //System.out.println("Found fault in line: " + line);
                    String[] parts = line.split(",");
                    turns.add(new Turn(Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]),
                            Game.Action.UNKNOWN,
                            parts[3],
                            parts[4],
                            parts[5]));
                }
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        return turns;
    }

    public static void writeToFile(ArrayList<Turn> faultyTurns, String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            for (Turn turn : faultyTurns) {
                writer.write(turn.toString());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
}
