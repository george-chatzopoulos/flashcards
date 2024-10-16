import java.io.File;
import java.util.Scanner;

public class Main {
    // Main
    @SuppressWarnings("ConvertToTryWithResources")
    public static void main(String[] args) {
        final String[] FLAGS = {"-import", "-export"};
        boolean importFlag = false;
        boolean exportFlag = false;
        File importFile = null;
        File exportFile = null;

        // Set up the scanner and the deck
        Scanner scan = new Scanner(System.in);
        Deck flashcards = new Deck();

        // Get the import and export files
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(FLAGS[0])) {
                importFlag = true;
                importFile = new File(args[i + 1]);
            } else if (args[i].equals(FLAGS[1])) {
                exportFlag = true;
                exportFile = new File(args[i + 1]);
            }
        }
        // Run the import command
        if (importFlag) {
            flashcards.importCards(scan, importFile);
        }

        // Menu
        boolean exit = false;
        do {
            flashcards.printOutput("Input the action (add, remove, import, " +
                    "export, ask, exit, log, hardest card, reset stats):");
            String action = flashcards.getInput(scan);

            // Perform the action
            switch (action) {
                case "add" -> flashcards.addCard(scan);
                case "remove" -> flashcards.removeCard(scan);
                case "import" -> flashcards.importCards(scan, importFile);
                case "export" -> flashcards.exportCards(scan, exportFile);
                case "ask" -> flashcards.ask(scan);
                case "exit" -> {
                    flashcards.printOutput("Bye bye!");
                    exit = true;
                }
                case "log" -> flashcards.log(scan);
                case "hardest card" -> flashcards.hardestCard();
                case "reset stats" -> flashcards.resetStats();
                default -> flashcards.printOutput("Invalid action");
            }
        } while (!exit);

        // Run the export command
        if (exportFlag) {
            flashcards.exportCards(scan, exportFile);
        }

        // Close the scanner
        scan.close();
    }
}
