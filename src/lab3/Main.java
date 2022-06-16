package lab3;

import java.io.IOException;
import org.apache.commons.math3.random.MersenneTwister;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        // play in Lcg mode
        //lcgMode();

        // play in Mt mode
        mtMode();
    }

    private static void lcgMode() throws IOException, InterruptedException {
        System.out.println("Playing in Lcg mode");
        // Need create account with free ID
        Account account = new Account("Lcg");
        while (!CasinoConnector.createAccount(account)) {
            account = new Account("Lcg");
        }

        System.out.println("Losing 3 times");
        // need to lose 3 times
        long x0, x1, x2;
        CasinoConnector.makeBetAndPlay(account, 1, 12);
        x0 = account.lastRealNumber;
        System.out.println("1 casino message = " + account.lastMessage);
        System.out.println("1 casino number = " + account.lastRealNumber);
        System.out.println("1 my money = " + account.money);

        CasinoConnector.makeBetAndPlay(account, 1, 13);
        x1 = account.lastRealNumber;
        System.out.println("2 casino message = " + account.lastMessage);
        System.out.println("2 casino number = " + account.lastRealNumber);
        System.out.println("2 my money = " + account.money);

        CasinoConnector.makeBetAndPlay(account, 1, 14);
        x2 = account.lastRealNumber;
        System.out.println("3 casino message = " + account.lastMessage);
        System.out.println("3 casino number = " + account.lastRealNumber);
        System.out.println("3 my money = " + account.money);

        System.out.println("Find a and c");
        // crack casino
        LcgGen lcg = new LcgGen();
        lcg.crack(x0, x1, x2);
        System.out.println("Lcg: a=" + lcg.a + "  c=" + lcg.c);

        // win!!!
        while (account.money < 1_000_000) {
            CasinoConnector.makeBetAndPlay(account, account.money, lcg.nextNumber());
            System.out.println("win: casino message = " + account.lastMessage);
            System.out.println("win: casino number = " + account.lastRealNumber);
            System.out.println("win: my money = " + account.money);
        }
        System.out.println("done");
    }


    private static void mtMode() throws IOException, InterruptedException {
        System.out.println("Playing in Mt mode");
        // Need create account with free ID
        Account account = new Account("Mt");
        while (!CasinoConnector.createAccount(account)) {
            account = new Account("Mt");
        }

        System.out.println("Losing 1 time");
        CasinoConnector.makeBetAndPlay(account, 1, 12);
        System.out.println("1 casino message = " + account.lastMessage);
        System.out.println("1 casino number = " + account.lastRealNumber);
        System.out.println("1 my money = " + account.money);

        System.out.println("Try to guess the seed");
        MersenneTwister mt = null;
        long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
        for (long i = currentTimeInSeconds - 15; i <= currentTimeInSeconds; i++) {
            System.out.println("seed is " + i);
            mt = new MersenneTwister();
            mt.setSeed((int)i);
            long myNumber = ((long)mt.nextInt()) & 0xFFFFFFFFL;

            System.out.println("casino number = " + account.lastRealNumber);
            System.out.println("my     number = " + myNumber);

            if (myNumber == account.lastRealNumber) {
                System.out.println("seed is good");
                break;
            } else {
                System.out.println("seed is NOT good");
                mt = null;
            }
        }

        if (mt == null) {
            System.out.println("Cannot find seed for Mt. :(");
            return;
        }

        // win!!!
        while (account.money < 1_000_000) {
            long myNumber = ((long)mt.nextInt()) & 0xFFFFFFFFL;
            CasinoConnector.makeBetAndPlay(account, account.money, myNumber);
            System.out.println("win: casino message = " + account.lastMessage);
            System.out.println("win: casino number = " + account.lastRealNumber);
            System.out.println("win: my money = " + account.money);
        }
        System.out.println("done");
    }
}
