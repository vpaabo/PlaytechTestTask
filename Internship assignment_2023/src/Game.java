import java.util.*;

public class Game {

    // all possible actios that can occur in a game
    enum Action {
        P_JOINED,
        P_HIT,
        P_STAND,
        P_WIN,
        P_LOSE,
        P_LEFT,
        D_SHOW,
        D_HIT,
        D_REDEAL,
        UNKNOWN /* for error handling*/
    }


    private int gameSessionID;

    private Player player;
    private Gamer dealer;

    private ArrayList<Turn> turns;


    private final Set<Card> cardsInPlay;

    public Game() {
        this.turns = new ArrayList<>();
        this.cardsInPlay = new HashSet<Card>();
    }

    public Game(int gameSessionID) {
        this();
        this.gameSessionID = gameSessionID;

    }

    public int getGameSessionID() {
        return gameSessionID;
    }

    public void setGameSessionID(int gameSessionID) {
        this.gameSessionID = gameSessionID;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Gamer getDealer() {
        return dealer;
    }

    public void setDealer(Gamer dealer) {
        this.dealer = dealer;
    }

    public ArrayList<Turn> getTurns() {
        return turns;
    }

    public void setTurns(ArrayList<Turn> turns) {
        this.turns = turns;
    }

    public void addTurn(Turn turn) {
        if (gameSessionID == 0) gameSessionID = turn.getGameSessionID();
        turns.add(turn);
    }

    /**
     * Check all Turns in this Game, in order.
     * If a fault is found, a FaultFoundException is
     * thrown and caught and the Turn where it occurred is returned instead
     * @return
     */
    public Turn checkTurns() {
        System.out.println("\n\nChecking game " + this.gameSessionID);

        player = new Player();
        dealer = new Gamer();

        Turn currentTurn = turns.get(0);
        Turn previousTurn = null;

        try {
            while (turns.size() > 0) {
                currentTurn = turns.remove(0);

                System.out.println("\n" + currentTurn);

                // do things that were 'declared' in the previous turn
                if (previousTurn != null) {

                    // in each round, check:
                    // player id is same as last turn
                    // the timestamp is trictly increasing
                    if (currentTurn.getPlayerID() != previousTurn.getPlayerID())
                        throw new FaultFoundException("Player ID does not match with previous' turn player ID");

                    if (currentTurn.getTimestamp() <= previousTurn.getTimestamp())
                        throw new FaultFoundException("Turn timestamp is not strictly increasing");

                    switch (previousTurn.getAction()) {
                        case P_JOINED, D_REDEAL -> {
                            // next possible action(s): P_HIT, P_STAND, P_LOSE
                            checkTurnContinuity(previousTurn, currentTurn,
                                    Action.P_HIT, Action.P_STAND, Action.P_LOSE, Action.P_LEFT);

                            // both hands should be unchanged
                            checkCardHandChange(previousTurn, currentTurn);
                        }
                        case P_HIT -> {
                            // next possible action(s): P_HIT, P_STAND, P_LOSE
                            checkTurnContinuity(previousTurn, currentTurn,
                                    Action.P_HIT, Action.P_STAND, Action.P_LOSE, Action.P_LEFT);

                            // player's hand now should have one more card
                            player.addNewCardToHand(currentTurn.getPlayersHand());
                        }
                        case P_STAND -> {
                            // next possible action(s): D_SHOW
                            checkTurnContinuity(previousTurn, currentTurn,
                                    Action.D_SHOW, Action.P_LEFT);

                            // both hands should be unchanged
                            checkCardHandChange(previousTurn, currentTurn);
                        }
                        case D_SHOW -> {
                            // next possible action(s): D_HIT, P_WIN, P_LOSE, P_LEFT
                            checkTurnContinuity(previousTurn, currentTurn,
                                    Action.D_HIT, Action.P_WIN, Action.P_LOSE, Action.P_LEFT);

                            // dealer's ? card is now seen
                            dealer.setCards(currentTurn.getDealersHand());
                        }
                        case D_HIT -> {
                            // next possible action(s): D_HIT, P_WIN, P_LOSE, (P_LEFT ??)
                            checkTurnContinuity(previousTurn, currentTurn,
                                    Action.D_HIT, Action.P_WIN, Action.P_LOSE, Action.P_LEFT);


                            // dealer's hand should have one more card
                            dealer.addNewCardToHand(currentTurn.getDealersHand());
                        }
                        case P_WIN, P_LOSE ->
                                // next possible action(s): D_REDEAL, P_LEFT
                                checkTurnContinuity(previousTurn, currentTurn,
                                        Action.D_REDEAL, Action.P_LEFT);

                        case P_LEFT ->
                                // next possible action(s): null
                                // can't have a turn after the player has left innit
                                throw new FaultyActionException("After P_LEFT no action can be taken (game session is over)");
                    }
                }

                switch (currentTurn.getAction()) {
                    case P_JOINED -> {
                        // both players get two cards, exactly one of dealer's is hidden
                        player.setPlayerID(currentTurn.getPlayerID());
                        player.setCards(currentTurn.getPlayersHand());
                        dealer.setCards(currentTurn.getDealersHand());


                        // hold a list of all used cards and look for duplicates
                        for (Card card1 : player.getCards()) {
                            for (Card card2 : cardsInPlay) {
                                if (card1.getSuit() == card2.getSuit() && card1.getValue().equals(card2.getValue()))
                                    throw new FaultyCardException("Duplicate card in play");

                            }
                            cardsInPlay.add(card1);
                        }
                        for (Card card1 : dealer.getCards()) {
                            for (Card card2 : cardsInPlay) {
                                if (card1.getSuit() == card2.getSuit() && card1.getValue().equals(card2.getValue()))
                                    throw new FaultyCardException("Duplicate card in play");
                            }
                            cardsInPlay.add(card1);
                        }

                        // didn't work, it checked using Object.equals(Object o)
                        /*for (Card card : player.getCards()) {
                            if (cardsInPlay.contains(card)) throw new FaultyCardException("Duplicate card in play");
                            else cardsInPlay.add(card);
                        }
                        for (Card card : dealer.getCards()) {
                            if (cardsInPlay.contains(card)) throw new FaultyCardException("Duplicate card in play");
                            else cardsInPlay.add(card);
                        }*/

                        // dealer should have exactly one ? card in hand
                        int qSum = 0;
                        for (Card card : dealer.getCards()) {
                            if (card.suit == '?') qSum++;
                        }
                        if (qSum != 1) {
                            //System.out.println(Collections.frequency(dealer.getCards(), new Card("?")));
                            throw new FaultyCardException("Dealer should have exactly one ? card in hand");
                        }
                    }
                    case P_HIT, P_STAND -> {
                        // can only hit if  not bust
                        // can stand only if not bust
                        if (Card.sum(player.getCards()) >= 21)
                            throw new FaultyActionException("Player is bust, should not be able to " +
                                    ((currentTurn.getAction() == Action.P_HIT) ? "hit" : "stand"));

                    }
                    case D_SHOW -> {
                        // nothing should change, during next move dealers hand should be seen
                    }
                    case D_HIT -> {
                        // can only hit if under 17 and not bust
                        int dealerSum = Card.sum(dealer.getCards());
                        if (dealerSum >= 21)
                            throw new FaultyActionException("Dealer is bust, should not be able to hit");
                        if (17 <= dealerSum /*&& dealerSum < 21*/)
                            throw new FaultyActionException("Player is bust, should not be able to hit");
                    }
                    case P_WIN -> {
                        // check if numbers match (dealer doesnt need more cards and points actually give player win)
                        int dealerSum = Card.sum(dealer.getCards());
                        int playerSum = Card.sum(player.getCards());
                        System.out.println("Dealer - " + dealerSum + ", player " + playerSum);

                        if (dealerSum < 17 || playerSum > 21 && playerSum < dealerSum) {
                            throw new FaultyActionException("Player should not be winning (player - " + playerSum + ", dealer - " + dealerSum + ")");
                        }
                    }
                    case P_LOSE -> {
                        int dealerSum = Card.sum(dealer.getCards());
                        int playerSum = Card.sum(player.getCards());
                        System.out.println("Dealer - " + dealerSum + ", player " + playerSum);

                        if (dealerSum < 17 || dealerSum > 21 || playerSum <= 21 && playerSum > dealerSum) {
                            throw new FaultyActionException("Player should not be losing (player - " + playerSum + ", dealer - " + dealerSum + ")");
                        }
                    }
                    case D_REDEAL -> {
                        player.setCards(currentTurn.getPlayersHand());
                        dealer.setCards(currentTurn.getDealersHand());

                        cardsInPlay.clear();
                        for (Card card : player.getCards()) {
                            if (cardsInPlay.contains(card)) throw new FaultyCardException("Duplicate card in play");
                            else cardsInPlay.add(card);
                        }
                        for (Card card : dealer.getCards()) {
                            if (cardsInPlay.contains(card)) throw new FaultyCardException("Duplicate card in play");
                            else cardsInPlay.add(card);
                        }
                    }
                    case P_LEFT -> {
                    }
                    case UNKNOWN -> {
                        throw new FaultyActionException("The action is unknown");
                    }
                    default -> {
                        // if action is not known then the turn is invalid
                        // should not arrive here though
                        return currentTurn;
                    }
                }

                /*System.out.println("Dealer: " + currentTurn.getDealersHand() +
                        ", Player: " + currentTurn.getPlayersHand());

                System.out.println(dealer.getCards() + ", " + player.getCards());*/
                previousTurn = currentTurn;

            }
        } catch (FaultFoundException e) {
            System.out.println("Fault found: " + e.getMessage());
            return currentTurn;
        }
        //System.out.println("all used cards: " + cardsInPlay);
        return null;

    }

    /**
     * Given current and previous Turns and possible actions which
     * can occur after previous' turn's, throw a FaultFoundException
     * if current turns action is not possible
     *
     * @param previousTurn
     * @param currentTurn
     * @param allowedActions
     * @throws FaultyActionException
     */
    private void checkTurnContinuity(Turn previousTurn, Turn currentTurn, Action... allowedActions) throws FaultyActionException {
        for (Action action : allowedActions) {
            if (currentTurn.getAction() == action) return;
        }
        throw new FaultyActionException(
                "Impossible situation (previous action: " +
                        previousTurn.getAction() + ", current action: " +
                        currentTurn.getAction() + ")");
    }

    /**
     * Throw a FaultFoundException if player's or dealer's hand changed since last turn
     *
     * @param previousTurn
     * @param currentTurn
     * @throws FaultyCardException
     */
    private void checkCardHandChange(Turn previousTurn, Turn currentTurn) throws FaultyCardException {

        if (!currentTurn.getPlayersHand().equals(previousTurn.getPlayersHand()))
            throw new FaultyCardException("Player's hand changed during action " + previousTurn.getAction());
        if (!currentTurn.getDealersHand().equals(previousTurn.getDealersHand()))
            throw new FaultyCardException("Dealer's hand changed during action " + previousTurn.getAction());
    }


}
