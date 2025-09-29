package ru.nsu.krasnyanski;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;

public class BlackjackTest {

    @Test
    public void testCard() {
        Card card = new Card("♠", "A", 11);
        assertEquals(11, card.getValue());
        assertEquals("A ♠ (11)", card.toString());
    }

    @Test
    public void testDeckDrawAndRemaining() {
        Deck deck = new Deck();
        int size = deck.remainingCards();
        Card c = deck.draw();
        assertNotNull(c);
        assertEquals(size - 1, deck.remainingCards());
    }

    @Test
    public void testDeckEmpty() {
        Deck deck = new Deck();
        while (deck.remainingCards() > 0) deck.draw();
        assertThrows(NoSuchElementException.class, deck::draw);
    }

    @Test
    public void testPlayerScoreWithAces() {
        Player player = new Player("Test");
        player.addCard(new Card("♠", "A", 11));
        player.addCard(new Card("♥", "K", 10));
        assertEquals(21, player.getScore());
        player.addCard(new Card("♦", "2", 2));
        assertEquals(13, player.getScore()); // Ace becomes 1
    }

    @Test
    public void testPlayerHand() {
        Player player = new Player("Test");
        Card card = new Card("♠", "5", 5);
        player.addCard(card);
        assertTrue(player.getHand().contains(card));
    }
}
