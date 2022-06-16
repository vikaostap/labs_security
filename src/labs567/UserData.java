package labs567;

import common.Utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserData {
    // Pepper
    private static final Path pepperForHashFileLocation = Paths.get("src/labs567/conf/pepper.data");
    private static final byte[] pepperForHash = readOrCreatePepperForHash();

    private static byte[] readOrCreatePepperForHash() {
        File pepperFile = pepperForHashFileLocation.toFile();
        if (pepperFile.exists()) {
            String pepper = Utils.readWholeFile(pepperForHashFileLocation);
            System.out.println("Read pepper from the file. Pepper data: " + pepper);
            return Utils.hex2Bytes(pepper);
        } else {
            // Need to generate random bytes for pepper. Use the same function for salt
            byte[] pepperBytes = Utils.generateSaltForHash();
            String pepper = Utils.bytes2Hex(pepperBytes);
            Utils.writeStringInFile(pepper, pepperFile);
            System.out.println("Generated pepper (" + pepper + ") were written to the file");
            return pepperBytes;
        }
    }

    // KEK
    private static final Path KEKDataFileLocation = Paths.get("src/labs567/conf/kek.data");
    private static final byte[] KEKData = readKEK();

    private static byte[] readKEK() {
        /* KEK was generated using the following code:
        * Utils.writeStringInFile(Utils.bytes2Hex(CryptoFunctions.generateAESKey()), KEKDataFileLocation.toFile());
        */
        File KEKFile = KEKDataFileLocation.toFile();
        if (KEKFile.exists()) {
            String kek = Utils.readWholeFile(KEKDataFileLocation);
            System.out.println("Read KEK from the file. KEK is: " + kek);
            return Utils.hex2Bytes(kek);
        } else {
            throw new RuntimeException("Could not get KEK");
        }
    }

    private String username;
    private String passwordHash;
    private String saltForHash;
    private byte[] dekData;
    private String homeAddress;
    private String phoneNumber;
    private String serializedData;

    private UserData() {} /* default constructor only for internal usage */

    UserData (String user, String password) {
        username = user;
        byte[] salt = Utils.generateSaltForHash();
        saltForHash = Utils.bytes2Hex(salt);

        byte[] hash = CryptoFunctions.getArgon2iHash(password, salt, pepperForHash);
        passwordHash = Utils.bytes2Hex(hash);

        homeAddress = "";
        phoneNumber = "";
        dekData = null;
        serializedData = null;
    }

    public void updatePrivateInfo(String newAddress, String newPhone) {
        homeAddress = newAddress;
        phoneNumber = newPhone;
        // reset DEK and serialisedData because they are not valid anymore
        dekData = null;
        serializedData = null;
    }

    public String getUsername() {
        return username;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isPasswordCorrect(String passwordToCheck) {
        byte[] salt = Utils.hex2Bytes(saltForHash);
        byte[] hash = CryptoFunctions.getArgon2iHash(passwordToCheck, salt, pepperForHash);
        String hashStr = Utils.bytes2Hex(hash);
        return hashStr.equals(passwordHash);
    }

    public String serialize() {
        // This function return representation UserData object in String format, that can be saved in DB
        if (serializedData != null) {
            return serializedData;
        }
        dekData = generateDEK();
        String dekStr = "";
        String addressEnc = "";
        String phoneEnc = "";
        try {
            byte[] tmp = CryptoFunctions.encryptAES(homeAddress.getBytes(StandardCharsets.UTF_8), dekData);
            addressEnc = Utils.bytes2Hex(tmp);
            tmp = CryptoFunctions.encryptAES(phoneNumber.getBytes(StandardCharsets.UTF_8), dekData);
            phoneEnc = Utils.bytes2Hex(tmp);
            tmp = CryptoFunctions.encryptAES(dekData, KEKData);
            dekStr = Utils.bytes2Hex(tmp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not encrypt");
        }
        serializedData = String.format("%s,%s,%s,%s,%s,%s", username, passwordHash, saltForHash,
                                                            dekStr, addressEnc, phoneEnc);
        return serializedData;
    }

    public static UserData deserialize(String userDataStr) {
        if (userDataStr.length() == 0) {
            throw new RuntimeException("UserData string is not valid");
        }

        String[] userDataParts = userDataStr.split(",");
        if (userDataParts.length != 4 && userDataParts.length != 6) {
            throw new RuntimeException("UserData array is not valid");
        }
        System.out.println(userDataStr);
        UserData ud = new UserData();
        ud.username = userDataParts[0];
        //System.out.println("deserialize: username= " + ud.username);
        ud.passwordHash = userDataParts[1];
        ud.saltForHash = userDataParts[2];
        ud.dekData = CryptoFunctions.decryptAES(Utils.hex2Bytes(userDataParts[3]), KEKData);
        if (userDataParts.length == 6) {
            byte[] tmp = CryptoFunctions.decryptAES(Utils.hex2Bytes(userDataParts[4]), ud.dekData);
            ud.homeAddress = new String(tmp);
            //System.out.println("deserialize: homeAddress= " + ud.homeAddress);
            tmp = CryptoFunctions.decryptAES(Utils.hex2Bytes(userDataParts[5]), ud.dekData);
            ud.phoneNumber = new String(tmp);
            //System.out.println("deserialize: phoneNumber= " + ud.phoneNumber);
        } else {
            ud.homeAddress = "";
            ud.phoneNumber = "";
        }
        ud.serializedData = userDataStr;
        return ud;
    }

    // Generating data encryption key (DEK). We should generate new DEK for each user data
    private byte[] generateDEK() {
        return CryptoFunctions.generateAESKey();
    }

    public static String isPasswordReliable(String password) {
        Matcher lowerCaseMatcher = Pattern.compile("[a-z]").matcher(password);
        Matcher upperCaseMatcher = Pattern.compile("[A-Z]").matcher(password);
        Matcher digitMatcher = Pattern.compile("[0-9]").matcher(password);
        Matcher specialCharacterMatcher = Pattern.compile("[@#$%^&*!]").matcher(password);

        if (!lowerCaseMatcher.find()) {
            return "Password doesn't contain lowercase letters";
        } else if (!upperCaseMatcher.find()) {
            return "Password doesn't contain uppercase letters";
        } else if (!digitMatcher.find()) {
            return "Password doesn't contain digits";
        } else if (!specialCharacterMatcher.find()) {
            return "Password doesn't contain special symbols: @#$%^&*!";
        } else if (password.length() < 8) {
            return "Password contains less than 8 symbols";
        }
        return "OK";
    }
}
