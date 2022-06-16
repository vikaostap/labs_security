package lab1;

import common.Utils;

import java.util.Base64;

public class Task0 {
    public static void main(String[] args) {
        String task0 = Utils.readWholeFile("src/lab1/task0-data.txt");

        System.out.println("Len of the data: " + task0.length());
        // split data on chunks of 8 bits
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < task0.length(); i+=8 ) {
            String chunk = task0.substring(i, i + 8); // 8 bits == 1 byte
            byte b = Byte.parseByte(chunk, 2);
            builder.append((char)b);
        }

        String convertedTask = builder.toString();
        System.out.println("Decoded text:");
        System.out.println(convertedTask);

        // since it ends with  "..T0hFCg==", it looks like it is base64
        byte[] decodedBytes = Base64.getDecoder().decode(convertedTask);
        convertedTask = new String(decodedBytes);
        System.out.println("Decoded text from base64:");
        System.out.println(convertedTask);
    }
}
