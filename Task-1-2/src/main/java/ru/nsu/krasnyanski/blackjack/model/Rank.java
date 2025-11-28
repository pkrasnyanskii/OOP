package ru.nsu.krasnyanski.blackjack.model;

/**
 * Enum representing the rank of a playing card in Blackjack.
 * Each rank has a label and a point value.
 */
public enum Rank {
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    JACK("J", 10),
    QUEEN("Q", 10),
    KING("K", 10),
    ACE("A", 11);

    private final String label;
    private final int value;

    /**
     * Constructs a Rank with a label and value.
     *
     * @param label the label of the rank
     * @param value the point value of the rank
     */
    Rank(String label, int value) {
        this.label = label;
        this.value = value;
    }

    /**
     * Returns the label of the rank.
     *
     * @return string label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the point value of the rank.
     *
     * @return integer value
     */
    public int getValue() {
        return value;
    }
}
