package ru.nsu.krasnyanski;

import java.util.Scanner;

/**
 * Represents a Blackjack game round with a player and a dealer.
 */
public class BlackjackGame {
    private final Deck deck = new Deck();
    private final Player player = new Player("Вы");
    private final Player dealer = new Player("Дилер");
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Plays a single round of Blackjack.
     */
    public void playRound() {
        player.addCard(deck.draw());
        player.addCard(deck.draw());
        dealer.addCard(deck.draw());
        dealer.addCard(deck.draw());

        System.out.println("Ваши карты: " + player);
        System.out.println("Карты дилера: [" + dealer.getHand().get(0) + ", <закрытая карта>]");

        if (player.getScore() == 21) {
            System.out.println("У вас блэкджек! Вы выиграли раунд!");
            return;
        }

        while (player.getScore() < 21) {
            System.out.println("Введите 1 (взять карту) или 0 (стоп):");
            int choice = scanner.nextInt();
            if (choice == 1) {
                Card c = deck.draw();
                player.addCard(c);
                System.out.println("Вы взяли: " + c);
                System.out.println("Ваши карты: " + player);
            } else {
                break;
            }
        }

        if (player.getScore() > 21) {
            System.out.println("Перебор! Вы проиграли.");
            return;
        }

        System.out.println("Ход дилера:");
        System.out.println("Карты дилера: " + dealer);

        while (dealer.getScore() < 17) {
            Card c = deck.draw();
            dealer.addCard(c);
            System.out.println("Дилер взял: " + c + " > " + dealer.getScore());
        }

        int ps = player.getScore();
        int ds = dealer.getScore();

        System.out.println("Итог: Ваш счёт = " + ps + ", Дилер = " + ds);
        if (ds > 21 || ps > ds) {
            System.out.println("Вы выиграли!");
        } else if (ps < ds) {
            System.out.println("Вы проиграли!");
        } else {
            System.out.println("Ничья!");
        }
    }
}
