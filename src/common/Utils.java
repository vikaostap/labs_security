package common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    public static String bytes2Hex(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        for (byte i : bytes) {
            int decimal = (int)i & 0XFF;
            String hex = Integer.toHexString(decimal);

            if (hex.length() % 2 == 1) {
                hex = "0" + hex;
            }

            result.append(hex);
        }
        return result.toString();
    }

    public static byte[] hex2Bytes(String hex) {
//        int indexBytes = 0;
//        byte[] bytes = new byte[hex.length()/2];
//
//        int index = 0;
//        while (index + 2 <= hex.length()) {
//            String part = hex.substring(index, index + 2);
//            int partInt = Integer.parseInt(part, 16);
//            bytes[indexBytes++] = (byte)partInt;
//            index += 2;
//        }
//        return bytes;
        byte[] result = new byte[hex.length() / 2];

        for (int i = 0; i < result.length; i++) {
            int index = i * 2;

            // Using parseInt() method of Integer class
            int val = Integer.parseInt(hex.substring(index, index + 2), 16);
            result[i] = (byte)val;
        }
        return result;
    }

    // min - included
    // max - not included
    public static int generateRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static ArrayList<String> readLinesFromFile(Path path) {
        try {
            String text = Files.readString(path, StandardCharsets.UTF_8);
            String[] lines = text.split(System.lineSeparator());
            ArrayList<String> list = new ArrayList<>();
            for (String line : lines) {
                line = line.strip(); // remove spaces
                if (!line.isEmpty()) { // do not include empty lines
                    list.add(line);
                }
            }
            return list;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot read file " + path);
        }
    }

    public static ArrayList<String> readLinesFromFile(String filepath) {
        Path path = Paths.get(filepath);
        return readLinesFromFile(path);
    }

    public static String readWholeFile(Path filepath) {
        try {
            return Files.readString(filepath, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot read file " + filepath);
        }
    }

    public static String readWholeFile(String filepath) {
        Path path = Paths.get(filepath);
        return readWholeFile(path);
    }

    public static void writeStringInFile(String data,  File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(data);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot write data in file " + file);
        }
    }

    public static byte[] generateSaltForHash() {
        // Size of salt should be at least 8 Byte, 16 bytes is sufficient
        byte[] salt = new byte[16];
        try {
            SecureRandom sr = SecureRandom.getInstanceStrong();
            sr.nextBytes(salt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot generate salt for hash");
        }
        return salt;
    }

    public static String generateRandomBytes() {
        byte[] data = generateSaltForHash();
        return bytes2Hex(data);
    }
}
