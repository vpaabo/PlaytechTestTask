import java.util.ArrayList;

public class Player extends Gamer {
    int playerID;


    public Player() {
        super();
    }

    public Player(int playerID) {
        this.playerID = playerID;
    }

    public Player(int playerID, String cards) throws FaultyCardException {
        super(cards);
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }
}
