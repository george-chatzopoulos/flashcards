package flashcards;

import java.io.*;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.ArrayList;

public class Deck {
    private ArrayList<String> terms;
    private ArrayList<String> definitions;
    private ArrayList<Integer> mistakes;
    private ArrayList<String> logs;

    // Constructor
    public Deck() {
        terms = new ArrayList<>();
        definitions = new ArrayList<>();
        mistakes = new ArrayList<>();
        logs = new ArrayList<>();
    }

    // Validators
    public boolean doesExist(String item, ArrayList<String> items) {
        return items.contains(item);
    }

    // Helper Functions
    public String getInput(Scanner scan) {
        String input = scan.nextLine();
        logs.add(input);
        return input;
    }


    public void printOutput(String msg) {
        logs.add(msg);
        System.out.println(msg);
    }



    private String readFace(Scanner scan, ArrayList<String> list, String type) {
        String face = getInput(scan);
        // Print the error message
        if (doesExist(face, list)) {
            if (type.equals("t")) {
                String duplicatedTerm = terms.get(terms.indexOf(face));
                String msg = String.format("The card \"%s\" already exists.", duplicatedTerm);
                printOutput(msg);
            } else {
                String duplicatedDefinition = definitions.get(definitions.indexOf(face));
                String msg = String.format("The definition \"%s\" already exists.", duplicatedDefinition);
                printOutput(msg);
            }
        }

        return face;
    }


    public int readFile(File file) {
        int changes = 0;

        // Read the file
        try {
            // Set up the reader
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            // Read the lines
            while((line = reader.readLine()) != null) {
                // Separate the term and definition
                String[] parts = line.split(":");
                String term = parts[0];
                String definition = parts[1];
                String mistake = parts[2];
                int mistakeInt = Integer.parseInt(mistake);

                // Replace the definition if the term exists
                if (doesExist(term, terms)) {
                    int index = terms.indexOf(term);
                    definitions.set(index, definition);
                    mistakes.set(index, mistakeInt);
                } else {
                    terms.add(term);
                    definitions.add(definition);
                    mistakes.add(mistakeInt);
                }
                changes++;
            }
            // Close the reader
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return changes;
    }


    // Menu Functions

    public void addCard(Scanner scan) {
        // Get the term
        printOutput("The card:");
        String term = readFace(scan, terms, "t");
        if (doesExist(term, terms)) {
            return;
        }

        // Get the definition
        printOutput("The definition of the card:");
        String definition = readFace(scan, definitions, "d");
        if (doesExist(definition, definitions)) {
            return;
        }

        // Add term and definition to the list
        terms.add(term);
        definitions.add(definition);
        mistakes.add(0);
        // Print the message
        String msg = String.format("The pair (\"%s\":\"%s\") has been added.", term, definition);
        printOutput(msg);
    }


    public void removeCard(Scanner scan) {
        // Get the card to remove
        printOutput("Which card?");
        String card = getInput(scan);

        // Remove the card
        if (terms.contains(card)) {
            int index = terms.indexOf(card);
            terms.remove(index);
            definitions.remove(index);
            mistakes.remove(index);
            // Print the message
            printOutput("The card has been removed.");
        } else {
            String msg = String.format("Can't remove \"%s\": there is no such card.", card);
            printOutput(msg);
        }
    }


    public void importCards(Scanner scan, File file) {
        // Get the file
        if (file == null) {
            // Get the file name
            printOutput("File name:");
            String fileName = getInput(scan);

            // Import the cards
            file = new File(fileName);
        }

        if (file.exists() && file.isFile()){
            // Read the file
            int changes = readFile(file);

            // Print the message
            String msg = String.format("%d cards have been loaded.", changes);
            printOutput(msg);
        } else {
            printOutput("File not found.");
        }
    }


    public void exportCards(Scanner scan, File file) {
        if (file == null){
            // Get the file name
            printOutput("File name:");
            String fileName = getInput(scan);

            // If the file name does not contain an extension, add .txt
            if (!fileName.contains(".")) {
                fileName += ".txt";
            }

            // Export the cards
            file = new File(fileName);
        }
        try {
            // Set up the writer
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            // Write the cards
            for (int i = 0; i < terms.size(); i++) {
                writer.write(String.format("%s:%s:%d\n", terms.get(i), definitions.get(i), mistakes.get(i)));
            }

            // Close the writer
            writer.close();
            // Print the message
            String msg = String.format("%d cards have been saved.", terms.size());
            printOutput(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void ask(Scanner scan) {
        printOutput("How many times to ask?");
        int times = 0;
        try {
            times = Integer.parseInt(getInput(scan));
        } catch (NumberFormatException e) {
            printOutput("Invalid input. Please enter a number.");
        }

        // Exit if the input is invalid
        if (times <= 0) {
            printOutput("Invalid input. Please enter a number greater than 0.");
            return;
        } else if (times > terms.size()) {
            String msg = String.format("The number of times to ask exceeds the number of cards (%d).", terms.size());
            printOutput(msg);
            return;
        }

        // Quiz the user
        for (int i = 0; i < times; i++) {
            String msg = String.format("Print the definition of \"%s\":", terms.get(i));
            printOutput(msg);
            String answer = getInput(scan);
            evalAnswer(answer, terms.get(i));
        }
    }


    public void log(Scanner scan) {
        // Get the file name
        printOutput("File name:");
        String fileName = getInput(scan);
        File file = new File(fileName);
        printOutput("The log has been saved.");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String log : logs) {
                writer.write(log + "\n");
            }
            LocalDate date = LocalDate.now();
            writer.write(date + "\n");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void hardestCard() {
        ArrayList<Integer> hardest = new ArrayList<>();
        int maxMistakes = 0;

        // Find the hardest card(s)
        for (int i = 0; i < mistakes.size(); i++) {
            int mistake = mistakes.get(i);
            if (mistake > maxMistakes) {
                hardest.clear();
                hardest.add(i);
                maxMistakes = mistake;
            } else if (mistake == maxMistakes) {
                hardest.add(i);
            }
        }

        // Print the message
        if (maxMistakes == 0) {
            printOutput("There are no cards with errors.");
        } else {
            if (hardest.size() == 1) {
                String term = terms.get(hardest.get(0));
                String msg = String.format("The hardest card is \"%s\". You have %d errors answering it.", term, maxMistakes);
                printOutput(msg);
            } else {
                StringBuilder msg = new StringBuilder("The hardest cards are ");
                for (int i = 0; i < hardest.size(); i++) {
                    String term = terms.get(hardest.get(i));
                    msg.append(String.format("\"%s\"", term));
                    if (i < hardest.size() - 1) {
                        msg.append(", ");
                    }
                }
                msg.append(String.format(". You have %d errors answering them.", maxMistakes));
                printOutput(msg.toString());
            }
        }
    }


    public void resetStats() {
        mistakes.replaceAll(ignored -> 0);
        printOutput("Card statistics have been reset.");
    }


    // Quiz Functions

    public void evalAnswer(String answer,String term) {
        int index = terms.indexOf(term);
        // Evaluate the answer
        if (answer.equals(definitions.get(index))) {
            printOutput("Correct!");
        } else {
            String msg = String.format("Wrong. The right answer is \"%s\"", definitions.get(index));
            // Update the print message if the definition exists for another term
            if (doesExist(answer, definitions)) {
                int correctIndex = definitions.indexOf(answer);
                String correctTerm = terms.get(correctIndex);
                msg += String.format(", but your definition is correct for \"%s\".", correctTerm);
            }
            mistakes.set(index, mistakes.get(index) + 1);
            printOutput(msg);
        }
    }
}
