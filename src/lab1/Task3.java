package lab1;

import common.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Task3 {
    public static void main(String[] args) {
        String task3Data = "EFFPQLEKVTVPCPYFLMVHQLUEWCNVWFYGHYTCETHQEKLPVMSAKSPVPAPVYWMVHQLUSPQLYWLASLFVWPQLMVHQLUPLRPSQLULQESPBLWPCSVRVWFLHLWFLWPUEWFYOTCMQYSLWOYWYETHQEKLPVMSAKSPVPAPVYWHEPPLUWSGYULEMQTLPPLUGUYOLWDTVSQETHQEKLPVPVSMTLEUPQEPCYAMEWWYTYWDLUULTCYWPQLSEOLSVOHTLUYAPVWLYGDALSSVWDPQLNLCKCLRQEASPVILSLEUMQBQVMQCYAHUYKEKTCASLFPYFLMVHQLUPQLHULIVYASHEUEDUEHQBVTTPQLVWFLRYGMYVWMVFLWMLSPVTTBYUNESESADDLSPVYWCYAMEWPUCPYFVIVFLPQLOLSSEDLVWHEUPSKCPQLWAOKLUYGMQEUEMPLUSVWENLCEWFEHHTCGULXALWMCEWETCSVSPYLEMQYGPQLOMEWCYAGVWFEBECPYASLQVDQLUYUFLUGULXALWMCSPEPVSPVMSBVPQPQVSPCHLYGMVHQLUPQLWLRPOEDVMETBYUFBVTTPENLPYPQLWLRPTEKLWZYCKVPTCSTESQPBYMEHVPETCMEHVPETZMEHVPETKTMEHVPETCMEHVPETT";

        // Begin Genetic Algorithm
        int numberOfGenerations = 500;

        // Create population
        int numberOfPopulation = 100;
        String initKey = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        List<Individual> individuals = new ArrayList<Individual>();
        for (int i = 0; i < numberOfPopulation; i++) {
            String key = generateRandomKey(initKey, 4, 20);
            Individual ind = new Individual(key, task3Data);
            individuals.add(ind);
        }

        for (int i = 0; i < numberOfGenerations; i++) {
            // selection stage
            individuals.sort(Individual::compare);
            individuals = individuals.stream().limit(80).collect(Collectors.toList());

            // crossover stage
            int currentSize = individuals.size();
            for (int j = 0; j < currentSize; j++) {
                // Generate 2 new keys from old one with different number of letters permutations
                // from 1 to 2 permutations
                String key1 = generateRandomKey(individuals.get(j).getKey(), 1, 3);
                Individual child1 = new Individual(key1, task3Data);
                individuals.add(child1);
                // from 3 to 4 permutations
                String key2 = generateRandomKey(individuals.get(j).getKey(), 3, 5);
                Individual child2 = new Individual(key2, task3Data);
                individuals.add(child2);
            }
        }
        // End Genetic Algorithm

        // print results
        for (int i = 0; i < 3; i++) {
            Individual individual = individuals.get(i);
            System.out.println("key = " + individual.getKey());
            System.out.println("fitness = " + individual.getFitness());
            System.out.println("open text:");
            System.out.println(individual.getOpenText());
            System.out.println();
        }

        /*
         * Result
         * key = EKMFLGDQVZNTOWYHXUSPAIBRCJ
         * fitness = 0.689722249489188
         * open text:
         * ADDTHEABILITYTODECIPHERANYKINDOFPOLYALPHABETICSUBSTITUTIONCIPHERSTHEONEUSEDINTHECIPHERTEXTSHEREHASTWENTYSIXINDEPENDENTRANDOMLYCHOSENMONOALPHABETICSUBSTITUTIONPATTERNSFOREACHLETTERFROMENGLISHALPHABETITISCLEARTHATYOUCANNOLONGERRELYONTHESAMESIMPLEROUTINEOFGUESSINGTHEKEYBYEXHAUSTIVESEARCHWHICHYOUPROBABLYUSEDTODECIPHERTHEPREVIOUSPARAGRAPHWILLTHEINDEXOFCOINCIDENCESTILLWORKASASUGGESTIONYOUCANTRYTODIVIDETHEMESSAGEINPARTSBYTHENUMBEROFCHARACTERSINAKEYANDAPPLYFREQUENCYANALYSISTOEACHOFTHEMCANYOUFINDAWAYTOUSEHIGHERORDERFREQUENCYSTATISTICSWITHTHISTYPEOFCIPHERTHENEXTMAGICALWORDWILLTAKETOTHENEXTLABENJOYBITLYSLASHTWOCAPITALYCAPITALJCAPITALBLCAPITALYCAPITALL
         *
         * open text with spaces:
         * ADD THE ABILITY TO DECIPHER ANY KIND OF POLYALPHABETIC SUBSTITUTION CIPHERS THE ONE USED IN THE CIPHER TEXTS
         * HERE HAS TWENTY SIX INDEPENDENT RANDOMLY CHOSEN MONOALPHABETIC SUBSTITUTION PATTERNS FOR EACH LETTER FROM
         * ENGLISH ALPHABET. IT IS CLEAR THAT YOU CAN NO LONGER RELY ON THE SAME SIMPLE ROUTINE OF GUESSING THE KEY BY
         * EXHAUSTIVE SEARCH WHICH YOU PROBABLY USED TO DECIPHER THE PREVIOUS PARAGRAPH WILL THE INDEX OF COINCIDENCE
         * STILL WORK AS A SUGGESTION. YOU CAN TRY TO DIVIDE THE MESSAGE IN PARTS BY THE NUMBER OF CHARACTERS IN A KEY
         * AND APPLY FREQUENCY ANALYSIS TO EACH OF THEM CAN YOU FIND AWAY TO USE HIGHER ORDER FREQUENCY STATISTICS WITH
         * THIS TYPE OF CIPHER THE NEXT MAGICAL WORD WILL TAKE TO THE NEXT LABEN JOY BITLY SLASH TWO CAPITAL Y CAPITAL J
         * CAPITAL BL CAPITAL Y CAPITAL L
         */
    }

    private static String generateRandomKey(String initKey, int minPermutations, int maxPermutations) {
        int numberPermutations = Utils.generateRandom(minPermutations, maxPermutations);
        char[] chars = initKey.toCharArray();

        for (int j = 0; j < numberPermutations; j++) {
            int index1 = Utils.generateRandom(0, chars.length);
            int index2 = Utils.generateRandom(0, chars.length);

            char tmp = chars[index1];
            chars[index1] = chars[index2];
            chars[index2] = tmp;
        }
        return new String(chars);
    }
}
