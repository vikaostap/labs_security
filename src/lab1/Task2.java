package lab1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class Task2 {
    public static void main(String[] args) {
        String task2Data = "G0IFOFVMLRAPI1QJbEQDbFEYOFEPJxAfI10JbEMFIUAAKRAfOVIfOFkYOUQFI15ML1kcJFUeYhA4IxAeKVQZL1VMOFgJbFMDIUAAKUgFOElMI1ZMOFgFPxADIlVMO1VMO1kAIBAZP1VMI14ANRAZPEAJPlMNP1VMIFUYOFUePxxMP19MOFgJbFsJNUMcLVMJbFkfbF8CIElMfgZNbGQDbFcJOBAYJFkfbF8CKRAeJVcEOBANOUQDIVEYJVMNIFwVbEkDORAbJVwAbEAeI1INLlwVbF4JKVRMOF9MOUMJbEMDIVVMP18eOBADKhALKV4JOFkPbFEAK18eJUQEIRBEO1gFL1hMO18eJ1UIbEQEKRAOKUMYbFwNP0RMNVUNPhlAbEMFIUUALUQJKBANIl4JLVwFIldMI0JMK0INKFkJIkRMKFUfL1UCOB5MH1UeJV8ZP1wVYBAbPlkYKRAFOBAeJVcEOBACI0dAbEkDORAbJVwAbF4JKVRMJURMOF9MKFUPJUAEKUJMOFgJbF4JNERMI14JbFEfbEcJIFxCbHIJLUJMJV5MIVkCKBxMOFgJPlVLPxACIxAfPFEPKUNCbDoEOEQcPwpDY1QDL0NCK18DK1wJYlMDIR8II1MZIVUCOB8IYwEkFQcoIB1ZJUQ1CAMvE1cHOVUuOkYuCkA4eHMJL3c8JWJffHIfDWIAGEA9Y1UIJURTOUMccUMELUIFIlc";
        byte[] decodedBytes = Base64.getDecoder().decode(task2Data);
        // Use index of coincidence to find the key len
        coincidenceMethod(decodedBytes);
        // result
        //key len = 1 : coincidence index = 0.0
        //key len = 2 : coincidence index = 0.0
        //key len = 3 : coincidence index = 0.06088560885608856    - close to 0.0644 - value for English language
        //key len = 4 : coincidence index = 0.0036900369003690036
        //key len = 5 : coincidence index = 0.007380073800738007
        //key len = 6 : coincidence index = 0.04981549815498155
        //key len = 7 : coincidence index = 0.0018450184501845018
        //key len = 8 : coincidence index = 0.0
        //key len = 9 : coincidence index = 0.08118081180811808

        ArrayList<char[]> keys = generateKeyWithLen3();
        for (char[] key : keys) {
            String openText = caesarCipherXORopWithRepeatingKey(decodedBytes, key);
            if (isHumanReadableText(openText, 0.95)) {
                System.out.println("key = " + Arrays.toString(key));
                System.out.println(openText);
            }
        }

        // Result:
        //
        // key = [L, 0, l] (L0l)
        //
        // Open Text:
        // Write a code to attack some simple substitution cipher. To reduce the complexity of this one we will use only
        // uppercase letters, so the keyspace is only 26! To get this one right automatically you will probably need to
        // use some sort of genetic algorithm (which worked the best last year), simulated annealing or gradient descent.
        // Seriously, write it right now, you will need it to decipher the next one as well. Bear in mind, there's no spaces.
        // https://docs.google.com/document/d/1HY7Dl-5itYD3C_gkueBvvBFpT4CecGPiR30BsARlTpQ/edit?usp=sharing

    }

    private static ArrayList<char[]> generateKeyWithLen3() {
        String keySpace = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        char[] keySpaceChars = keySpace.toCharArray();

        ArrayList<char[]> keys = new ArrayList<char[]>();
        for (char i : keySpaceChars) {
            for (char j : keySpaceChars) {
                for (char k : keySpaceChars) {
                    char[] key = new char[3];
                    key[0] = i;
                    key[1] = j;
                    key[2] = k;
                    keys.add(key);
                }
            }
        }
        return keys;
    }

    // encrypt and decrypt of caesar cipher with repeating-key
    public static String caesarCipherXORopWithRepeatingKey(byte[] cipherText, char[] key) {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (byte ct : cipherText) {
            builder.append((char)(ct ^ key[index % key.length]));
            index++;
        }
        return builder.toString();
    }

    private static void coincidenceMethod(byte[] cipherText) {
        byte[] cmpData = new byte[cipherText.length];

        // copy ciphertext
        System.arraycopy(cipherText, 0, cmpData, 0, cipherText.length);

        System.out.println("Indexes of coincidence:");
        for (int i = 1; i < 10; i++) {
            int counter = 0;
            byte lastByte = cmpData[cmpData.length - 1];
            System.arraycopy(cmpData, 0, cmpData, 1, cmpData.length - 1);
            cmpData[0] = lastByte;

            for (int j = 0; j < cipherText.length; j++) {
                if(cipherText[j] == cmpData[j]) {
                    counter++;
                }
            }

            double coincidenceIndex = (double)counter / (double)cipherText.length;
            System.out.println("key len = " + i + " : coincidence index = " + coincidenceIndex);
        }
    }

    public static boolean isHumanReadableText(String openText, double percent) {
        int countLettersAndNumbers = 0;
        for (char c : openText.toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == ' ') {
                countLettersAndNumbers++;
            }
        }
        double realPercent = (double) countLettersAndNumbers / (double) openText.length();
        if (realPercent >= percent) {
            return true;
        }
        return false;
    }
}
