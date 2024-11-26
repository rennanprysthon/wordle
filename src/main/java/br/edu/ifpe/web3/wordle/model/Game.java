package br.edu.ifpe.web3.wordle.model;

import br.edu.ifpe.web3.wordle.infrastructure.database.WordsMock;
import jakarta.websocket.Session;

import java.util.List;

public class Game {
    private boolean isGameStarted;
    private Session player1;
    private Session player2;
    private Wordle myWordle;
    private Wordle opWordle;

    public PlayerNumber addPlayer(Session session) {
        if (this.player1 == null) {
            this.player1 = session;
            return PlayerNumber.PLAYER_1;
        }

        if (this.player2 == null) {
            this.player2 = session;
            return PlayerNumber.PLAYER_1;
        }

        List<String> words = WordsMock.returnWords();

        String secret = words.get((int) Math.floor(Math.random() * words.size()));

        this.myWordle = new Wordle(words, 6, secret);
        this.opWordle = new Wordle(words, 6, secret);

        this.isGameStarted = true;

        System.out.println("Game is started");
        return PlayerNumber.FULL;
    }

    public boolean isStarted() {
        return this.isGameStarted;
    }

    public Wordle getWordle(Session session) {
        return session == player1 ? myWordle : opWordle;
    }

    public PlayerNumber getPlayer(Session session) {
        return session == player1 ? PlayerNumber.PLAYER_1 : PlayerNumber.PLAYER_2;
    }

    public Session getPlayer1() {
        return player1;
    }

    public Session getPlayer2() {
        return player2;
    }
}
