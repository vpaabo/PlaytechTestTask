import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Card {

    private static final HashMap<String, Integer> cardValueConversions = new HashMap<>();

    static {
        cardValueConversions.put("2", 2);
        cardValueConversions.put("3", 3);
        cardValueConversions.put("4", 4);
        cardValueConversions.put("5", 5);
        cardValueConversions.put("6", 6);
        cardValueConversions.put("7", 7);
        cardValueConversions.put("8", 8);
        cardValueConversions.put("9", 9);
        cardValueConversions.put("10", 10);
        cardValueConversions.put("j", 10);
        cardValueConversions.put("q", 10);
        cardValueConversions.put("k", 10);
        cardValueConversions.put("a", 11);

        cardValueConversions.put("?", 0);
    }

    private static final ArrayList<Character> allPossibleSuits = new ArrayList<>();

    static {
        allPossibleSuits.add('s');
        allPossibleSuits.add('h');
        allPossibleSuits.add('c');
        allPossibleSuits.add('d');
    }

    private static final ArrayList<Card> allPossibleCards = new ArrayList<>();

    static {

        for (String value : cardValueConversions.keySet()) {
            if (value.equals("?")) continue;
            for (char suit : allPossibleSuits) {
                allPossibleCards.add(new Card(suit, value));
            }
        }
        /*System.out.println("All possible cards : " + allPossibleCards.size());
        for (Card c : allPossibleCards) {
            System.out.println(c);
        }*/
    }

    String value;
    char suit;


    public Card(String cardString) throws FaultyCardException {
        if (cardString.equals("?")) {
            suit = '?';
            value = "";
            return;
        }
        cardString = cardString.toLowerCase(Locale.ROOT);
        value = cardString.substring(0, cardString.length() - 1);
        suit = cardString.charAt(cardString.length() - 1);

        //assert allPossibleCards.contains(this);
        if (!(allPossibleSuits.contains(suit) && cardValueConversions.containsKey(value))) {
            throw new FaultyCardException("This is not a real card: " + this);
        }

    }

    public Card(char suit, String value) {
        this.suit = suit;
        this.value = value;
    }

    public char getSuit() {
        return suit;
    }

    public String getValue() {
        return value;
    }


    @Override
    public String toString() {
        if (this.suit == '?') return "Card: ?";
        return "Card: " + value + suit;
    }

    /*@Override
    public boolean equals(Object o){
        return this.value.equals(((Card)o).value) && this.suit == ((Card)o).suit;
    }*/

    public static int sum(ArrayList<Card> cards) {
        int sum = 0;
        for (Card card : cards) {
            if (card.suit == '?') continue;
            //System.out.println("summing card: " + card);
            sum += cardValueConversions.get(card.value);
        }
        return sum;
    }
}
