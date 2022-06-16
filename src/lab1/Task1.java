package lab1;

import common.Utils;

public class Task1 {
    public static void main(String[] args) {
        String task1Data = "7958401743454e1756174552475256435e59501a5c524e176f786517545e475f5245191772195019175e4317445f58425b531743565c521756174443455e595017d5b7ab5f525b5b58174058455b53d5b7aa175659531b17505e41525917435f52175c524e175e4417d5b7ab5c524ed5b7aa1b174f584517435f5217515e454443175b524343524517d5b7ab5fd5b7aa17405e435f17d5b7ab5cd5b7aa1b17435f5259174f584517d5b7ab52d5b7aa17405e435f17d5b7ab52d5b7aa1b17435f525917d5b7ab5bd5b7aa17405e435f17d5b7ab4ed5b7aa1b1756595317435f5259174f58451759524f4317545f564517d5b7ab5bd5b7aa17405e435f17d5b7ab5cd5b7aa175650565e591b17435f525917d5b7ab58d5b7aa17405e435f17d5b7ab52d5b7aa1756595317445817585919176e5842175a564e17424452175659175e5953524f1758511754585e59545e53525954521b177f565a5a5e595017535e4443565954521b177c56445e445c5e17524f565a5e5956435e58591b17444356435e44435e54565b17435244434417584517405f564352415245175a52435f5853174e5842175152525b174058425b5317445f584017435f52175552444317455244425b4319";
        byte[] task1Bytes = Utils.hex2Bytes(task1Data);

        // Since size of char is 1 byte, then the key space is only 256 symbols: from 0 to 255.
        // Check symbols from alphabet and numbers first
        String keySpace = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (char key : keySpace.toCharArray()) {
            String openText = caesarCipherXORop(task1Bytes, key);
            System.out.println("key = " + key);
            System.out.println(openText);
        }

        // Found the key!!!
        // key = '7'
        // Decrypted text is
        /*
        Now try a repeating-key XOR cipher. E.g. it should take a string ￢ﾀﾜhello world￢ﾀﾝ and, given the key is ￢ﾀﾜkey￢ﾀﾝ,
        xor the first letter ￢ﾀﾜh￢ﾀﾝ with ￢ﾀﾜk￢ﾀﾝ, then xor ￢ﾀﾜe￢ﾀﾝ with ￢ﾀﾜe￢ﾀﾝ, then ￢ﾀﾜl￢ﾀﾝ with ￢ﾀﾜy￢ﾀﾝ, and then xor
        next char ￢ﾀﾜl￢ﾀﾝ with ￢ﾀﾜk￢ﾀﾝ again, then ￢ﾀﾜo￢ﾀﾝ with ￢ﾀﾜe￢ﾀﾝ and so on. You may use an index of coincidence,
        Hamming distance, Kasiski examination, statistical tests or whatever method you feel would show the best result.
         */
    }

    // encrypt and decrypt of caesar cipher
    public static String caesarCipherXORop(byte[] cipherText, char key) {
        StringBuilder builder = new StringBuilder();
        for (byte ct : cipherText) {
            builder.append((char)(ct ^ (byte)key));
        }
        return builder.toString();
    }
}
