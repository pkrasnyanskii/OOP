package ru.nsu.krasnyanski;

/**
 * Entry point for the Blackjack game.
 */
public class Main {
    public static void main(String[] args) {
        BlackjackGame game = new BlackjackGame();
        System.out.println("Добро пожаловать в Блэкджек!");
        game.playRound();
    }
}
