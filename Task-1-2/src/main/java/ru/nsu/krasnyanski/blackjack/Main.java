package ru.nsu.krasnyanski.blackjack;

import ru.nsu.krasnyanski.blackjack.controller.BlackjackGame;
import ru.nsu.krasnyanski.blackjack.localization.Messages;
import ru.nsu.krasnyanski.blackjack.view.InputHandler;
import ru.nsu.krasnyanski.blackjack.view.OutputHandler;

/**
 * Entry point for the Blackjack game.
 * Allows playing multiple rounds in a loop.
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
            output.println(messages.get("game.welcome"));

            boolean play = true;

            BlackjackGame game = new BlackjackGame(output, input, messages);
            game.playRound();

            while (play) {
                int start = input.getChoice("play.again");
                if (start == 0) {
                    play = false;
                    output.println(messages.get("goodbye"));
                } else {
                    game = new BlackjackGame(output, input, messages);
                    game.playRound();
                }
            }
        }
    }
}
