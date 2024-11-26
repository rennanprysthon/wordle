package br.edu.ifpe.web3.wordle.model;

public class WordleRequest {
    private String word;
    private WordleRequestType type;

    public WordleRequest() {}

    public WordleRequest(String word, WordleRequestType type) {
        this.word = word;
        this.type = type;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public WordleRequestType getType() {
        return type;
    }

    public void setType(WordleRequestType type) {
        this.type = type;
    }
}
