package lab1;

import common.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * Class Individual represents object for Genetic Algorithm
 */
public class Individual {
    private static final String alphabetLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Map<String, Double> englistTrigramsMap = getEnglishTrigrams();

    private final String key;
    private String openText;
    private double fitness;

    Individual (String key, String cipherText) {
        this.key = key;
        decrypt(cipherText);
        calcFitness();
    }

    public String getKey() {
        return key;
    }

    public double getFitness() {
        return fitness;
    }
    public String getOpenText() {
        return openText;
    }

    private void decrypt(String cipherText) {
        Map<Character, Character> decryptMap = new HashMap<Character, Character>();

        for (int i = 0; i < key.length(); i++) {
            decryptMap.put(key.charAt(i), alphabetLetters.charAt(i));
        }

        StringBuilder builder = new StringBuilder();
        for (char ch : cipherText.toCharArray()) {
            builder.append(decryptMap.get(ch));
        }
        openText = builder.toString();
    }

    private void calcFitness() {
        Map<String, Double> trigrams = getTrigrams(openText);
        fitness = 0.0;

        for (String trigram : trigrams.keySet()) {
            double decryptedFreq = trigrams.get(trigram);
            double englishFreq = englistTrigramsMap.getOrDefault(trigram, 0.0);
            fitness += decryptedFreq - englishFreq;
        }
    }

    private Map<String, Double> getTrigrams(String text) {
        Map<String, Double> trigrams = new HashMap<>();
        int trigramsCount = 0;
        for (int i = 0; i < text.length() - 3; i++) {
            trigramsCount++;
            String trigram = text.substring(i, i + 3);
            double count = trigrams.getOrDefault(trigram, 0.0);
            count += 1.0;
            trigrams.put(trigram, count);
        }

        for (String trigram : trigrams.keySet()) {
            double freq = trigrams.get(trigram) / trigramsCount;
            trigrams.put(trigram, freq);
        }
        return trigrams;
    }

    private static Map<String, Double> getEnglishTrigrams() {
        Map<String, Double> trigramsMap = new HashMap<>();
        //System.out.println("Read all english trigrams");
        // http://practicalcryptography.com/cryptanalysis/letter-frequencies-various-languages/english-letter-frequencies/
        ArrayList<String> trigramLines = Utils.readLinesFromFile("src/lab1/english_trigrams.txt");

        long allCount = 0;
        for (String trigramLine : trigramLines) {
            String[] trigramParts = trigramLine.split(" ");
            assert trigramParts.length == 2;
            long count = Long.parseLong(trigramParts[1]);
            trigramsMap.put(trigramParts[0], (double)count);
            allCount += count;
        }

        //System.out.println("English trigram count " + allCount);
        for (String trigram : trigramsMap.keySet()) {
            double freq = trigramsMap.get(trigram) / (double) allCount;
            trigramsMap.put(trigram, freq);
//            System.out.println("English trigram: " + trigram);
//            System.out.println("English trigram freq: " + freq);
        }
//        System.out.println("THE: " +  trigramsMap.get("THE"));
//        System.out.println("AND: " +  trigramsMap.get("AND"));

        return trigramsMap;
    }

    public int compare(Individual a) {
        return Double.compare(fitness, a.fitness);
    }
}
