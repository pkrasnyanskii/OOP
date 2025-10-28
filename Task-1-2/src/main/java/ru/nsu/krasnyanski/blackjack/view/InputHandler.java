package ru.nsu.krasnyanski.blackjack.view;

import java.util.Scanner;

import ru.nsu.krasnyanski.blackjack.localization.Messages;


/**
 * Handles user input for the Blackjack game.
 * Uses a Scanner and supports localized prompts via Messages.
 * Implements AutoCloseable to properly release resources.
 */
public class InputHandler implements AutoCloseable {
    private final Scanner scanner = new Scanner(System.in);
    private final Messages messages;
    private final OutputHandler output;

    /**
     * Constructs an InputHandler with Messages and OutputHandler.
     *
     * @param messages the Messages instance for localized strings
     * @param output   the OutputHandler instance to show prompts and messages
     */
    public InputHandler(Messages messages, OutputHandler output) {
        this.messages = messages;
        this.output = output;
    }

    /**
     * Prompts the user to enter a choice (0 or 1) and validates input.
     *
     * @return the user's choice, either 0 or 1
     */
    public int getChoice() {
        while (true) {
            output.println(messages.get("prompt.choice"));
            if (!scanner.hasNextInt()) {
                output.println(messages.get("invalid.input"));
                scanner.next();
                continue;
            }
            int choice = scanner.nextInt();
            if (choice == 0 || choice == 1) {
                return choice;
            }
            output.println(messages.get("invalid.input"));
        }
    }

    /**
     * Closes the underlying Scanner to release resources.
     */
    @Override
    public void close() {
        scanner.close();
    }
}
