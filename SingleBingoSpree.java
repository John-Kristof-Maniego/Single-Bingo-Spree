import javax.smartcardio.TerminalFactory;
import java.io.IOException;
import java.util.*;

//Disclaimer: Run this on terminals with ANSI supported! (Recommended: Windows 11 Terminal or Windows 10 CMD)

public class SingleBingoSpree {
    public static void main(String[] args) throws InterruptedException, IOException {

        //Enables ANSI
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            new ProcessBuilder("cmd", "/c", "").inheritIO().start().waitFor();
        }

        //Defining Commands (for easier use)
        Scanner userIn = new Scanner(System.in);
        Random rand = new Random();

        //Variables
        boolean[] exit = new boolean[3];
        boolean autoShuffle = false, markMeter;
        String mode, setting;

        int[] bingoNumbers = new int[75];
        int[] cardNumbers = new int[25];
        String[] card = new String[25];

        String mark = "><";
        boolean[] marked = new boolean[25];

        int cycle, currentScore = 0, lowestScore = 0;

        //Start of Code
        System.out.println("\n+----+----+--Single-Bingo Spree--+----+----+");

        while (!exit[0]) {
            exit[1] = false;
            exit[2] = false;

            System.out.println();

            //Generating the numbers
            for (int number = 0; number < bingoNumbers.length; number++) {
                bingoNumbers[number] = number + 1;
            }

            //Shuffling the Numbers (using the Knuth Shuffle + Splitting the shuffling into 5 groups (B, I, N, G, and O)
            for (int column = 0; column < 5; column++) {
                int startingRange = column * 15;
                int endingRange = startingRange + 14;

                for (int currentNumber = endingRange; currentNumber > startingRange; currentNumber--) {
                    int shuffledNumber = startingRange + rand.nextInt((currentNumber - startingRange) + 1);

                    int currentNumHolder = bingoNumbers[currentNumber];
                    bingoNumbers[currentNumber] = bingoNumbers[shuffledNumber];
                    bingoNumbers[shuffledNumber] = currentNumHolder;
                }
            }

            //Assigning the Numbers to the Card (int)
            for (int number = 0; number < 5; number++) {
                cardNumbers[number] = bingoNumbers[number];
                cardNumbers[number + 5] = bingoNumbers[number + 15];
                cardNumbers[number + 10] = bingoNumbers[number + 30];
                cardNumbers[number + 15] = bingoNumbers[number + 45];
                cardNumbers[number + 20] = bingoNumbers[number + 60];
            }

            //Duplicating the Card (int) to the Printed Card (String)
            for (int number = 0; number < cardNumbers.length; number++) {
                card[number] = String.valueOf(cardNumbers[number]);
            }

            //Printing the Card
            for (int cardRow = 0; cardRow < 5; cardRow++) {
                String grid = " | ", endGrid = " |", space = "         ";

                if (cardRow == 0) {
                    System.out.println(space + "| B  | I  | N  | G  | O  |");
                }

                System.out.println(space + "+----+----+----+----+----+");
                System.out.print(space + "| ");

                if (cardNumbers[cardRow] < 10) {
                    System.out.print(" " + card[cardRow]);
                } else {
                    System.out.print(card[cardRow]);
                }

                System.out.print(grid + card[cardRow + 5] + grid);

                if (cardRow == 2) {
                    System.out.print("SB");
                } else {
                    System.out.print(card[cardRow + 10]);
                }

                System.out.println(grid + card[cardRow + 15] + grid + card[cardRow + 20] + endGrid);
                if (cardRow == 4) {
                    System.out.println(space + "+----+----+----+----+----+");
                }
            }


            System.out.println("\n+----+----+----+-------+----+----+----+----+" +
                               "\n\033[2KCurrent Number of tries: " + currentScore +
                               "\n\033[2KHigh Score (Lowest Tries): " + lowestScore +
                               "\n+----+----+----+-------+----+----+----+----+");

            //Mode Selection
            System.out.print("""
                    \033[2K
                    \033[2K[S] - Start Game
                    \033[2K[G] - Generate new Card
                    \033[2K[O] - Options
                    \033[2K[E] - Exit Program
                    \033[2K
                    \033[2KInput Mode:\s""");

            while (!exit[1]) {
                exit[2] = false;
                mode = userIn.nextLine();

                switch (mode) {

                    //Exit Program
                    case "E", "e":
                        System.out.print("\033[2A\033[2K\n\033[2KExiting Program...\n");
                        exit[0] = true;
                        exit[1] = true;
                        break;

                    //Settings
                    case "O", "o":
                        System.out.print("\033[1A\033[2K\033[1A\033[2K\033[1A\033[2K\033[1A\033[2K\033[1A\033[2K\033[1A\033[2K");

                        System.out.println("""
                                \r[1] - Auto-Shuffle Mode --- Automatically shuffles (Disclaimer: Pressing [Enter] may break the game)
                                \r[0] - Manual-Shuffle Mode --- Press [Enter] to shuffle
                                """);

                        while (!exit[2]) {
                            System.out.print("\r\033[2KInput Option: ");

                            setting = userIn.nextLine();

                            switch (setting) {
                                case "1":
                                    autoShuffle = true;
                                    exit[2] = true;
                                    break;

                                case "0":
                                    autoShuffle = false;
                                    exit[2] = true;
                                    break;

                                default:
                                    System.out.print("\033[2K\033[2A\033[2KInvalid Input\n\r\033[2KInput Option: ");
                                    break;
                            }
                        }
                        System.out.print("\033[1A\033[2K\033[1A\033[2K\033[1A\033[2K\033[1A\033[2K");

                        System.out.print("""
                              \033[2K[S] - Start Game
                              \033[2K[G] - Generate new Card
                              \033[2K[O] - Options
                              \033[2K[E] - Exit Program
                              \033[2K
                              \033[2KInput Mode:\s""");
                        break;

                    //Generate a New Card
                    case "G", "g":
                        System.out.print("\033[25A");
                        exit[1] = true;
                        break;

                    //Start Game
                    case "S", "s":
                        cycle = 0;
                        currentScore = 0;

                        //Shakes the numbers again (without organizations --truly mixed)
                        Arrays.fill(marked, false);

                        for (int number = bingoNumbers.length - 1; number > 0; number--) {

                            int shuffledNumber = rand.nextInt(number + 1);

                            int currentNumHolder = bingoNumbers[number];
                            bingoNumbers[number] = bingoNumbers[shuffledNumber];
                            bingoNumbers[shuffledNumber] = currentNumHolder;
                        }

                        //Count-Down
                        System.out.print("\033[2K\033[1A\033[2K\033[1A\033[2K\033[1A\033[2K\033[1A\033[2K\033[1A\033[2K3...\033[1A\r\033[2KPicked Number: Shuffling...");
                        Thread.sleep(600);
                        System.out.print("\n\r\033[2K2..");
                        Thread.sleep(600);
                        System.out.print("\r\033[2K1...");
                        Thread.sleep(600);
                        System.out.print("\r\033[2KGO!!!");
                        Thread.sleep(600);

                        System.out.print("\r\033[2K\033[17A");
                        while (!exit[2]) {
                            currentScore = cycle + 1;
                            markMeter = false;

                            //Checking if the numbers on the card is equal to the shuffled number picked per cycle
                            for (int number = 0; number < cardNumbers.length; number++) {

                                if (cardNumbers[number] == bingoNumbers[cycle]) {
                                    card[number] = mark;
                                    marked[number] = true;
                                    markMeter = true;
                                }
                            }

                            //Prints card with "><" if a number is equal to the cycled shuffled number
                            for (int cardRow = 0; cardRow < 5; cardRow++) {
                                String grid = " | ", endGrid = " |", space = "         ";

                                System.out.print("\r\033[2K" + space);
                                System.out.print("| ");

                                if (cardNumbers[cardRow] < 10 && !card[cardRow].equals(mark)) {
                                    System.out.print(" " + card[cardRow]);
                                } else {
                                    System.out.print(card[cardRow]);
                                }

                                System.out.print(grid + card[cardRow + 5] + grid);

                                if (cardRow == 2) {
                                    System.out.print("SB");
                                } else {
                                    System.out.print(card[cardRow + 10]);
                                }

                                System.out.println(grid + card[cardRow + 15] + grid + card[cardRow + 20] + endGrid + "\n");
                            }

                            System.out.print("\n\n\033[2KCurrent Number of tries: " + currentScore +
                                    "\n\n\n\n\033[2KPicked Number: " + bingoNumbers[cycle]);

                            if (markMeter) {
                                System.out.print(" (Marked)");
                            }

                            cycle++;

                            //Checking if the card has a 'Bingo Line' (holy moly-)
                            if (    (marked[0] && marked[1] && marked[2] && marked[3] && marked[4]) ||      // Horizontal
                                    (marked[5] && marked[6] && marked[7] && marked[8] && marked[9]) ||
                                    (marked[15] && marked[16] && marked[17] && marked[18] && marked[19]) ||
                                    (marked[20] && marked[21] && marked[22] && marked[23] && marked[24]) ||
                                    (marked[0] && marked[5] && marked[10] && marked[15] && marked[20]) ||   // Vertical
                                    (marked[1] && marked[6] && marked[11] && marked[16] && marked[21]) ||
                                    (marked[3] && marked[8] && marked[13] && marked[18] && marked[23]) ||
                                    (marked[4] && marked[9] && marked[14] && marked[19] && marked[24]) ||
                                    (marked[5] && marked[11] && marked[17] && marked[23]) ||                // Diagonal
                                    (marked[1] && marked[7] && marked[13] && marked[19]) ||
                                    (marked[3] && marked[7] && marked[11] && marked[15]) ||
                                    (marked[9] && marked[13] && marked[17] && marked[21])
                            )

                            {
                                //Sets the lowest score (if lower than the previous score or if the score is 0)
                                if (lowestScore > currentScore || lowestScore == 0) {
                                    lowestScore = currentScore;
                                }

                                System.out.print("\n\033[2KYou Won!!!!!");
                                userIn.nextLine();

                                exit[2] = true;
                            }

                            else {
                                //Auto - Shuffle Mode
                                if (autoShuffle) {
                                    Thread.sleep(750);
                                    System.out.print("\033[16A");
                                }

                                //Manual - Shuffle Mode
                                else {
                                    System.out.print("\n[Press Enter]");
                                    userIn.nextLine();
                                    System.out.print("\033[18A");
                                }
                            }
                        }
                        System.out.print("\033[21A");
                        exit[1] = true;
                        break;

                    default:
                        System.out.print("\033[2A\033[2KInvalid Input\n\033[2KInput Mode: ");
                        break;
                }
            }
        }
    }
}
