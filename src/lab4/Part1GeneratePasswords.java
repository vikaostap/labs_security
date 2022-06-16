package lab4;

import common.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Part1GeneratePasswords {
    // top 25 passwords
    // https://www.safetydetectives.com/blog/the-most-hacked-passwords-in-the-world/

    // top 100k passwords
    // https://github.com/danielmiessler/SecLists/blob/master/Passwords/Common-Credentials/10-million-password-list-top-100000.txt

    // words: USA ENGLISH - 61,000 words
    // http://www.gwicks.net/dictionaries.htm


    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

        // init all characters
        String alphabetLowerCase = "abcdefghijklmnopqrstuvwxyz";
        String alphabetUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        String specialChars = "$%^&)><?_.,@";

        // read files with passwords and words
        ArrayList<String> listTop25Passwords = Utils.readLinesFromFile("src/lab4/top25passwords.txt");
        ArrayList<String> listTop100kPasswords = Utils.readLinesFromFile("src/lab4/top100kPasswords.txt");
        ArrayList<String> allWords = Utils.readLinesFromFile("src/lab4/words.txt");

        // use only words with len 4 and more
        ArrayList<String> words = new ArrayList<>();
        for (String word : allWords) {
            if (word.length() > 3) {
                words.add(word);
            }
        }

        // init files for storing hashes
        BufferedWriter weakHashesWriter = new BufferedWriter(new FileWriter("src/lab4/hashes-weak.csv"));
        BufferedWriter strongHashesWriter = new BufferedWriter(new FileWriter("src/lab4/hashes-strong.csv"));


        StringBuilder passwordBuilder = new StringBuilder();

        for (int i = 0; i < 100_000; i++) {
            String password = "";
            int typeOfPassword = i % 100;
            if (typeOfPassword < 10) { // 10% cases
                // 1.a generate password from the top 25/100 passwords
                password = listTop25Passwords.get(Utils.generateRandom(0, listTop25Passwords.size()));
            } else if (typeOfPassword < 80) { // 70% cases
                // 1.b generate password from 100k-1M most common passwords list
                password = listTop100kPasswords.get(Utils.generateRandom(0, listTop100kPasswords.size()));
            } else if (typeOfPassword < 85) { // 5% cases
                // 1.c generate really random password
                password = getReallyRandomPassword(passwordBuilder, alphabetLowerCase, alphabetUpperCase, numbers, specialChars);
                // clear passwordBuilder
                passwordBuilder.setLength(0);
            } else { // 15% cases
                // 1.d generate passwords that humans would create
                password = getHumanPassword(passwordBuilder, alphabetUpperCase, numbers, words);
                // clear passwordBuilder
                passwordBuilder.setLength(0);
            }
            System.out.println("password = " + password);
            String md5 = getMD5hash(password);
            System.out.println("md5 hash = " + md5);
            weakHashesWriter.write(md5);
            weakHashesWriter.write(System.lineSeparator());

            byte[] sha1Salt = Utils.generateSaltForHash();
            String saltStr = Utils.bytes2Hex(sha1Salt);
            String sha1 = getSHA1hash(password, sha1Salt);
            System.out.println("sha1 hash = " + sha1);
            System.out.println("sha1 salt = " + saltStr);

            strongHashesWriter.write(sha1);
            strongHashesWriter.write(',');
            strongHashesWriter.write(saltStr);
            strongHashesWriter.write(System.lineSeparator());
            System.out.println();
        }

        weakHashesWriter.close();
        strongHashesWriter.close();
        System.out.println("done");
    }

    private static String getReallyRandomPassword(StringBuilder passwordBuilder, String lowerChars, String upperChars,
                                                  String numbersChars, String specialChars) {
        // 4-6 upper case letters
        for (int k = Utils.generateRandom(4, 7); k > 0; k--) {
            char c = lowerChars.charAt(Utils.generateRandom(0, lowerChars.length()));
            passwordBuilder.append(c);
        }
        // 2-4 upper case letters
        for (int k = Utils.generateRandom(2, 5); k > 0; k--) {
            char c = upperChars.charAt(Utils.generateRandom(0, upperChars.length()));
            passwordBuilder.append(c);
        }
        // 1-2 numbers
        for (int k = Utils.generateRandom(1, 3); k > 0; k--) {
            char c = numbersChars.charAt(Utils.generateRandom(0, numbersChars.length()));
            passwordBuilder.append(c);
        }
        // 1 special character
        char c = specialChars.charAt(Utils.generateRandom(0, specialChars.length()));
        passwordBuilder.append(c);

        // Shuffle chars
        ArrayList<Character> list = new ArrayList<Character>();
        for (char ch : passwordBuilder.toString().toCharArray()) {
            list.add(ch);
        }
        Collections.shuffle(list);
        passwordBuilder.setLength(0);
        for (char ch : list) {
            passwordBuilder.append(ch);
        }

        return passwordBuilder.toString();
    }

    private static String getHumanPassword(StringBuilder passwordBuilder, String upperChars,
                                           String numbersChars, ArrayList<String> words) {
        String word = words.get(Utils.generateRandom(0, words.size()));
        // Replace any letter to UpperCase
        int index = Utils.generateRandom(0, word.length());
        word = word.replace(word.charAt(index), Character.toUpperCase(word.charAt(index)));
        passwordBuilder.append(word);

        // add 0-2 upper case letter
        for (int k = Utils.generateRandom(0, 3); k > 0; k--) {
            char c = upperChars.charAt(Utils.generateRandom(0, upperChars.length()));
            passwordBuilder.append(c);
        }
        // add 1-2 numbers
        for (int k = Utils.generateRandom(1, 3); k > 0; k--) {
            char c = numbersChars.charAt(Utils.generateRandom(0, numbersChars.length()));
            passwordBuilder.append(c);
        }
        return passwordBuilder.toString();
    }

    private static String getMD5hash(String password) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(password.getBytes());
        return Utils.bytes2Hex(digest);
    }

    private static String getSHA1hash(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        sha1.update(salt);
        byte[] digest = sha1.digest(password.getBytes());
        return Utils.bytes2Hex(digest);
    }

}
