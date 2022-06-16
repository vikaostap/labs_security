package lab3;

import common.Utils;

public class Account {
    public String mode;
    public int id;
    public long money;

    public long lastRealNumber;
    public String lastMessage;

    public Account(String gameMode) {
        id = Utils.generateRandom(0, 100_000);
        mode = gameMode;
    }


}
