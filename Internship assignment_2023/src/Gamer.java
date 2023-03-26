import java.util.ArrayList;
import java.util.Arrays;

public class Gamer {
    /**
     * This class represents a playing party in a game session
     * aka the dealer or the player
     */
    ArrayList<Card> cards;

    public Gamer() {
        this.cards = new ArrayList<>();
    }

    public Gamer(String cards) throws FaultyCardException {
        this();
        this.setCards(cards);

    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }
    public void setCards(String cardString) throws FaultyCardException {

        this.cards.clear();
        for (String unParsed : cardString.split("-")) {
            this.cards.add(parseCard(unParsed));
        }

    }

    public void addCardToHand(Card card){
        this.cards.add(card);
    }


    /**
     * Given the string of a card hand, add the last one to that Gamer's hand
     * @param cards A string of a card hand (ie "kS-2H-10C")
     */
    public void addNewCardToHand(String cards) throws FaultyCardException {
        String[] allCards = cards.split("-");
        //System.out.println("in addNewCardToHand: " + Arrays.toString(allCards));
        this.cards.add(parseCard(allCards[allCards.length - 1]));
    }

    private Card parseCard(String cardString) throws FaultyCardException {
        return new Card(cardString);
    }
}
