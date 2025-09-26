package ru.nsu.krasnyanski.blackjack;

import ru.nsu.krasnyanski.blackjack.controller.BlackjackGame;
import ru.nsu.krasnyanski.blackjack.localization.Messages;
import ru.nsu.krasnyanski.blackjack.view.InputHandler;
import ru.nsu.krasnyanski.blackjack.view.OutputHandler;

/**
 * Entry point for the Blackjack game.
 * Initializes messages, input, and output handlers, and runs a game round.
 */
public class Main {

    /**
     * Starts the Blackjack game.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        Messages messages = new Messages("ru");
        OutputHandler output = new OutputHandler(messages);

        try (InputHandler input = new InputHandler(messages, output)) {
            while (true){
                BlackjackGame game = new BlackjackGame(output, input, messages);
                game.playRound();

                output.println(messages.get("play.again"));

                int choice = input.getChoice();
                if (choice == 2) {
                    output.println(messages.get("goodbye"));
                    break;
                }
            }
        }
    }
}
