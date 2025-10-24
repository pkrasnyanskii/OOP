package ru.nsu.krasnyanski.blackjack.controller;

import ru.nsu.krasnyanski.blackjack.localization.Messages;
import ru.nsu.krasnyanski.blackjack.model.Card;
import ru.nsu.krasnyanski.blackjack.model.Deck;
import ru.nsu.krasnyanski.blackjack.model.Player;
import ru.nsu.krasnyanski.blackjack.view.InputHandler;
import ru.nsu.krasnyanski.blackjack.view.OutputHandler;

/**
 * The main controller class that manages the Blackjack game flow.
 *
 * It coordinates interactions between the model (cards, players, deck)
 * and the view components (input and output handlers).
 *
 */
public class BlackjackGame {
    private final Deck deck = new Deck();
    private final OutputHandler output;
    private final InputHandler input;
    private final Messages messages;
    private final Player player;
    private final Player dealer;

    /**
     * Creates a new Blackjack game controller.
     *
     * @param output   the output handler for displaying messages
     * @param input    the input handler for reading user input
     * @param messages the localization messages provider
     */
    public BlackjackGame(OutputHandler output, InputHandler input, Messages messages) {
        this.output = output;
        this.input = input;
        this.messages = messages;
        this.player = new Player(messages.get("you"));
        this.dealer = new Player(messages.get("dealer"));
    }

    /**
     * Starts and runs a single round of Blackjack.
     */
    public void playRound() {
        startRound();
        playerTurn();
        dealerTurn();
        showResult();
    }

    /**
     * Initializes the game round by dealing the initial cards.
     */
    private void startRound() {
        player.addCard(deck.draw());
        player.addCard(deck.draw());
        dealer.addCard(deck.draw());
        dealer.addCard(deck.draw());
        output.showPlayerCards(player.getName(), player.getHand().toString(), player.getScore());
        output.showDealerHidden(dealer.getHand().get(0).toString());
    }

    /**
     * Handles the player's turn, allowing the player to draw or stop.
     */
    private void playerTurn() {
        while (player.getScore() < 21) {
            int choice = input.getChoice();
            if (choice == 1) {
                Card card = deck.draw();
                player.addCard(card);
                output.println(messages.get("player.took") + " " + card);
                output.showPlayerCards(player.getName(), player.getHand().toString(), player.getScore());
            } else {
                break;
            }
        }
    }

    /**
     * Handles the dealer's automatic turn based on Blackjack rules.
     */
    private void dealerTurn() {
        output.println(messages.get("dealer.turn"));
        while (dealer.getScore() < 17) {
            Card card = deck.draw();
            dealer.addCard(card);
            output.println(messages.get("dealer.took") + " " + card);
        }
    }

    /**
     * Determines and displays the final game result.
     */
    private void showResult() {
        int ps = player.getScore();
        int ds = dealer.getScore();

        output.println(messages.get("final.scores") + " " + ps + " / " + ds);
        if (ps > 21) {
            output.println(messages.get("lose"));
        } else if (ds > 21 || ps > ds) {
            output.println(messages.get("win"));
        } else if (ps < ds) {
            output.println(messages.get("lose"));
        } else {
            output.println(messages.get("draw"));
        }
    }
}
