package lab2;

import common.Utils;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        var lines = Utils.readLinesFromFile("src/lab2/salsa20-ciphertext.txt");
        lines.sort((a, b) -> b.length() - a.length());
        ArrayList<byte[]> cipherTexts = new ArrayList<>();
        for (String line: lines) {
            //System.out.println(line);
            byte[] decoded = Utils.hex2Bytes(line);
            cipherTexts.add(decoded);
            //System.out.println(Arrays.toString(decoded));
        }

        // do XOR every ciphertext with other ciphertext
        ArrayList<byte[]> cipherTextsXORed = new ArrayList<>();
        for (int i = 0; i < cipherTexts.size(); i++) {
            for (int j = i + 1; j < cipherTexts.size(); j++) {
                byte[] ciphertextI = cipherTexts.get(i);
                byte[] ciphertextJ = cipherTexts.get(j);
                byte[] xored = new byte[ciphertextI.length];
                for (int k = 0; k < ciphertextI.length; k++) {
                    xored[k] = (byte)(ciphertextI[k] ^ ciphertextJ[k % ciphertextJ.length]);
                }
                cipherTextsXORed.add(xored);
            }
        }

        // Try different keys
        String key = "the lose the name of action ";
        ArrayList<String> opentexts = applyKey(cipherTextsXORed, key);
        for (int i = 0; i < opentexts.size(); i++) {
            System.out.print(i);
            System.out.print(" : ");
            System.out.println(opentexts.get(i));
        }
    }

    private static  ArrayList<String> applyKey(ArrayList<byte[]> cipherTextsXORed, String key) {
        ArrayList<String> opentexts = new ArrayList<>();
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        for (byte[] ctBytes : cipherTextsXORed) {
            char[] opBytes = new char[ctBytes.length];
            for (int i = 0; i < opBytes.length; i++) {
                char c = (char)(ctBytes[i] ^ keyBytes[i % keyBytes.length]);
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == ' ') {
                    opBytes[i] = c;
                } else {
                    opBytes[i] = '*';
                }
            }
            opentexts.add(new String(opBytes));
        }
        return opentexts;
    }
}
