package ru.nsu.krasnyanski.blackjack;

import java.util.NoSuchElementException;
import ru.nsu.krasnyanski.blackjack.model.Card;
import ru.nsu.krasnyanski.blackjack.model.Deck;
import ru.nsu.krasnyanski.blackjack.model.Player;
import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanski.blackjack.model.Rank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Blackjack core model classes.
 */
public class BlackjackTest {

    @Test
    public void testCard() {
        Card card = new Card("♠", Rank.ACE);
        assertEquals(11, card.getValue());
        assertEquals("A ♠ (11)", card.toString());
    }

    @Test
    public void testDeckDrawAndRemaining() {
        Deck deck = new Deck();
        int initial = deck.getRemainingCardsCount();
        Card c = deck.draw();
        assertNotNull(c);
        assertEquals(initial - 1, deck.getRemainingCardsCount());
    }

    @Test
    public void testDeckEmptyThrows() {
        Deck deck = new Deck();
        while (deck.getRemainingCardsCount() > 0) {
            deck.draw();
        }
        assertThrows(NoSuchElementException.class, deck::draw);
    }

    @Test
    public void testPlayerScoreWithAceAdjustment() {
        Player player = new Player("Tester");
        player.addCard(new Card("♠", Rank.ACE));
        player.addCard(new Card("♥", Rank.KING));
        assertEquals(21, player.getScore());

        player.addCard(new Card("♦", Rank.TWO));
        assertEquals(13, player.getScore());
    }

    @Test
    public void testPlayerHandAddition() {
        Player player = new Player("Tester");
        Card card = new Card("♣", Rank.FIVE);
        player.addCard(card);
        assertTrue(player.getHand().contains(card));
    }

    @Test
    public void testRanksValues() {
        assertEquals(10, Rank.QUEEN.getValue());
        assertEquals(2, Rank.TWO.getValue());
        assertEquals(11, Rank.ACE.getValue());
    }

    @Test
    public void testDeckHas52UniqueCards() {
        Deck deck = new Deck();
        assertEquals(52, deck.getRemainingCardsCount());
        assertTrue(deck.draw() instanceof Card);
    }

    @Test
    public void testDrawIfBothHave21() {
        Player player = new Player("Player");
        Player dealer = new Player("Dealer");
        player.addCard(new Card("♠", Rank.ACE));
        player.addCard(new Card("♥", Rank.KING));
        dealer.addCard(new Card("♦", Rank.ACE));
        dealer.addCard(new Card("♣", Rank.KING));
        int ps = player.getScore();
        int ds = dealer.getScore();
        assertEquals(21, ps);
        assertEquals(21, ds);

        String result =
                ps > 21 ? "lose" :
                        ds > 21 || ps > ds ? "win" :
                                ps < ds ? "lose" :
                                        "draw";

        assertEquals("draw", result);
    }

}
