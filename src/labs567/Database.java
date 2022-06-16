package labs567;

import common.Utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * For educational purpose I will not use popular DBs such as MySQL, MariaDB, PostgreSQL, MongoDB, ...
 * I will try to implement my own DB using file.
 */
public class Database {
    private final Path dbFileLocation = Paths.get("src/labs567/database/db.data");
    // store "username (String) <=> data (UserData)" pairs
    private final HashMap<String, UserData> dbData = new HashMap<>();

    Database() {
        File dbFile = dbFileLocation.toFile();
        try {
            boolean result = dbFile.createNewFile();
            System.out.println(result ? "DB file was created" : "DB file exists");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Can not create DB file: " + dbFileLocation);
        }
        readAllData();
    }

    private void readAllData() {
        ArrayList<String> lines = Utils.readLinesFromFile(dbFileLocation);
        for (String line : lines) {
            UserData ud = UserData.deserialize(line);
            dbData.put(ud.getUsername(), ud);
        }
        System.out.println("Read " + dbData.size() + " rows in DB");
    }

    private void writeAllData() {
        StringBuilder sb = new StringBuilder();
        for (UserData ud : dbData.values()) {
            sb.append(ud.serialize());
            sb.append(System.lineSeparator());
        }
        String data = sb.toString();
        Utils.writeStringInFile(data, dbFileLocation.toFile());
    }

    public boolean userExists(String username) {
        return dbData.containsKey(username);
    }

    public UserData getUserData(String username) {
        UserData ud = dbData.get(username);
        if (ud == null) {
            throw new RuntimeException("User '" + username + "' not found");
        }
        return ud;
    }

    public void createNewUser(String username, String password) {
        UserData ud = new UserData(username, password);
        dbData.put(username, ud);
        writeAllData();
    }

    public void updateUserInfo(String username, String newHomeAddress, String newPhoneNumber) {
        UserData ud = getUserData(username);
        ud.updatePrivateInfo(newHomeAddress, newPhoneNumber);
        writeAllData();
    }
}
