package com.company;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Random;
import java.util.Comparator;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    static Path boardPath = Paths.get("leaderboard.txt");   // ne ukazana direktorija, poetomu file zapiwetsja
    // v direktorii s programmoj
    private static final List<GameResult> leaderBoard = loadLeaderBoard();  //loadLeaderBoard iz konca programi


    public static void main(String[] args) {

        Random random = new Random();

        do {

            GameResult reg = new GameResult();
            System.out.println();
            reg.name = askName("Enter your name (3-10 characters)", 10, 3);

            long start = System.currentTimeMillis();             // vremja

            int myNum = random.nextInt(100) + 1;
            System.out.println("I'm thinking a number from 1 to 100. Try to guess it!");
            boolean userWin = false;
            for (int attempt = 1; attempt <= 10; attempt++) {
                String msg = String.format("Attempt № #%d. Enter your guess: ", attempt);

                int userNum = askNumber(msg, 100, 1);
                if (myNum > userNum) {
                    System.out.println("Your number is too low");
                } else if (myNum < userNum) {
                    System.out.println("Your number is too high");
                } else if (myNum == userNum) {
                    System.out.printf("You won! %d attempts were used.\n", attempt);
                    long finish = System.currentTimeMillis();               //vremja
                    long timeConsumedMillis = finish - start;               //vremja
                    reg.attempt = attempt;
                    reg.time = timeConsumedMillis;                          //vremja
                    reg.time2 = timeConsumedMillis * 0.001;                 //vremja v sekundah

                    leaderBoard.add(reg);
                    leaderBoard.sort(Comparator.<GameResult>comparingInt(r -> r.attempt)
                            .thenComparingLong(r -> r.time));

                    System.out.printf("%s won in %s attempt(s) ( %s seconds). \n", reg.name, reg.attempt, reg.time2);
                    userWin = true;
                    break;

                }
            }
            if (!userWin) {
                System.out.printf("You lost! My number was %d\n", myNum);
            }

            printLeaderBoard();            // Vivesti leaderboard

            System.out.print("Do you want to play again? (Y/n) ");
        } while (!scanner.nextLine().equals("n"));
        System.out.println("Goodbye!");
        storeLeaderBoard();   //vizivaem metod iz konca programmi, dlja zapisi rezultatov v fajl
    }

    private static void printLeaderBoard() {
        System.out.println("Leader Board:");
        System.out.println("\t Name \t\t Attempts \t\t Time");
        // t - otstup - "tab", nazivaetsja moduljaciej
        int maxDisplay = Math.min(4, leaderBoard.size());
        // esli kol-vo igr menwe 4, to berjotsja leaderBoard.size,
        // esli bolwe, to berutsja tolko 4 igri
        List<GameResult> top = leaderBoard.subList(0, maxDisplay);
        for (GameResult r : top) {
            System.out.printf("\t %s \t\t %8d \t\t %5.1f sec\n", r.name, r.attempt, r.time / 1000.0);
            // %8d - 8 otstupov - wtobi cifra vsegda bila prizhata (esli chislo "7" to 8 otstupov, esli "7" to 7 i t.d.
            // d - celoe chislo
            // %5.1f - 5 otstupov i .1 chislo posle zapjatoj. f - drobnoe chislo
        }
    }

    public static int askNumber(String message, int max, int min) {
        while (true) {
            System.out.println(message);

            try {   //try - catch (exception)
                int result = scanner.nextInt();
                scanner.nextLine();    // wtobi ubrat bag s proverkoj pustoj stroki, posle togo kak soglasilsja igrat
                // echjo raz (clear everything what user entered after the number)

                if (result > max) {
                    System.out.printf("Number should not be greater than %d\n", max);
                    continue;
                }

                if (result < min) {
                    System.out.printf("Number should not be lower than %d\n", min);
                    continue;
                }
                return result;
            } catch (InputMismatchException e) {
                String input = scanner.nextLine();
                System.err.println(input + " is not a number");
            }
        }
    }

    public static String askName(String message, int max, int min) {
        while (true) {
            System.out.println(message);


            String nameResult = scanner.nextLine();


            if (nameResult.length() > max) {
                System.out.printf("Name should not consist more than %d characters\n", max);
                continue;
            }

            if (nameResult.length() < min) {
                System.out.printf("Name should not consist less than %d characters\n", min);
                continue;
            }
            return nameResult;


        }
    }

    static void storeLeaderBoard() {
        try (Writer out = Files.newBufferedWriter(boardPath)) {
            // writer - wto-to vrode printf
            for (GameResult r : leaderBoard) {
                String line = String.format("%s \n", r.name);
                out.write(line);
                String line2 = String.format("%d %d \n ", r.attempt, r.time);
                out.write(line2);
            }
        } catch (IOException e) {
            System.out.println("Error writing to file");
        }
    }

    static List<GameResult> loadLeaderBoard() {
        List<GameResult> result = new ArrayList<>();
        try (Scanner in = new Scanner(boardPath)) {
            while (in.hasNext()) {          // poka v sledujuchej stroke wto nibud est
                GameResult r = new GameResult();
                r.name = in.next();
                r.attempt = in.nextInt();
                r.time = in.nextLong();
                result.add(r);
            }
        } catch (IOException e) {
            System.out.println("Cannot read file");
        }
        return result;
    }

}