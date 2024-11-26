package br.edu.ifpe.web3.wordle.model;

import java.util.Arrays;
import java.util.List;

public class Wordle {
    private final List<String> words;
    private final String secret;
    private Integer tries;
    private final Integer maxTries;
    private final Integer wordLength;

    public Wordle(List<String> words, Integer tries, String secret) {
        this.words = words;
        this.secret = secret;
        this.tries = 0;
        this.maxTries = tries;
        this.wordLength = this.secret.length();
    }

    public MoveResult check(String value) {
        if (value.length() != this.secret.length() || this.tries > this.maxTries) {
            throw new RuntimeException("Invalid word");
        }

        if (!this.words.contains(value)) {
            return new MoveResult(Winner.INVALID_WORD, null, null);
        }

        int[] result = new int[this.wordLength];
        Arrays.fill(result, -1);

        for (int stringIndex = 0; stringIndex < this.secret.length(); stringIndex++) {
            String number = String.valueOf(value.charAt(stringIndex));

            if (!this.secret.contains(number)) {
                result[stringIndex] = 0;
            } else if (number.equals(String.valueOf(this.secret.charAt(stringIndex)))) {
                result[stringIndex] = 2;
            }
        }

        for (int i = 0; i < result.length; i++) {
            if (result[i] == -1) {
                int finalI = i;
                int v1 = (int) this.secret.chars().filter(v -> v == value.charAt(finalI)).count();
                int v2 = (int) value.chars().filter(v -> v == value.charAt(finalI)).count();

                if (v1 >= v2) {
                    result[i] = 1;
                } else {
                    int index = value.indexOf(value.charAt(i), i + 1);
                    if (index != -1 && result[index] == -1) {
                        result[i] = 1;
                    } else {
                        result[i] = 0;
                    }
                }
            }
        }
        this.tries++;

        if (Arrays.stream(result).allMatch(e -> e == 2)) {
            return new MoveResult(Winner.WIN, this.secret, result);
        }

        if (this.tries.equals(this.maxTries)) {
            return new MoveResult(Winner.LOSE, this.secret, result);
        }

        return new MoveResult(Winner.NONE, null, result);
    }
}
