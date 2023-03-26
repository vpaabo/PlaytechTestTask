import java.util.Locale;

public class Turn implements Comparable<Turn>{

    private int timestamp;
    private int gameSessionID;
    private int playerID;
    private Game.Action action;
    private String actionString;
    private String dealersHand;
    private String playersHand;


    public Turn(int timestamp, int gameSessionID, int playerID, Game.Action action, String actionString, String dealersHand, String playersHand) {
        this.timestamp = timestamp;
        this.gameSessionID = gameSessionID;
        this.playerID = playerID;
        this.action = action;
        this.actionString = actionString;
        this.dealersHand = dealersHand;
        this.playersHand = playersHand;
        //this.isFaulty = false;
    }

    public Turn(String toSplit) {
        String[] split = toSplit.split(",");
        this.timestamp = Integer.parseInt(split[0]);
        this.gameSessionID = Integer.parseInt(split[1]);
        this.playerID = Integer.parseInt(split[2]);
        this.action = Game.Action.valueOf(split[3].replace(" ", "_").toUpperCase(Locale.ROOT));
        this.actionString = split[3];
        this.dealersHand = split[4];
        this.playersHand = split[5];
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getGameSessionID() {
        return gameSessionID;
    }

    public void setGameSessionID(int gameSessionID) {
        this.gameSessionID = gameSessionID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public Game.Action getAction() {
        return action;
    }

    public void setAction(Game.Action action) {
        this.action = action;
    }

    public String getDealersHand() {
        return dealersHand;
    }

    public void setDealersHand(String dealersHand) {
        this.dealersHand = dealersHand;
    }

    public String getPlayersHand() {
        return playersHand;
    }

    public void setPlayersHand(String playersHand) {
        this.playersHand = playersHand;
    }

    @Override
    public String toString() {
        //System.out.println();
        return timestamp + "," +
                gameSessionID + "," +
                playerID + "," +
                actionString + "," +
                dealersHand + "," +
                playersHand;
    }

    @Override
    public int compareTo(Turn t) {
        int ts1 = this.getTimestamp();
        int ts2 = t.getTimestamp();
        if (ts1 == ts2) return 0;
        return (ts1 < ts2) ? -1 : 1;
    }
}
