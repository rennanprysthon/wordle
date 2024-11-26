package br.edu.ifpe.web3.wordle.model;

public class WordleResponse {
    private WordleResponseType type;
    private PlayerNumber playerNumber;
    private MoveResult moveResult;
    private String word;

    public WordleResponse() {
    }

    public WordleResponse(WordleResponseType type) {
        this.type = type;
    }

    public WordleResponse(WordleResponseType type, PlayerNumber playerNumber) {
        this.type = type;
        this.playerNumber = playerNumber;
    }

    public WordleResponse(WordleResponseType type, MoveResult moveResult, PlayerNumber playerNumber) {
        this.type = type;
        this.moveResult = moveResult;
        this.playerNumber = playerNumber;
    }

    public WordleResponse(WordleResponseType type, String word) {
        this.type = type;
        this.word = word;
    }

    public WordleResponseType getType() {
        return type;
    }

    public void setType(WordleResponseType type) {
        this.type = type;
    }

    public PlayerNumber getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(PlayerNumber playerNumber) {
        this.playerNumber = playerNumber;
    }

    public MoveResult getMoveResult() {
        return moveResult;
    }

    public void setMoveResult(MoveResult moveResult) {
        this.moveResult = moveResult;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
