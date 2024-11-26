package br.edu.ifpe.web3.wordle.infrastructure.controller;

import br.edu.ifpe.web3.wordle.infrastructure.database.WordsMock;
import br.edu.ifpe.web3.wordle.infrastructure.decoders.WordleRequestDecoder;
import br.edu.ifpe.web3.wordle.infrastructure.decoders.WordleResponseEncoder;
import br.edu.ifpe.web3.wordle.model.*;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ServerEndpoint(
    value = "/wordle",
    decoders = WordleRequestDecoder.class,
    encoders = WordleResponseEncoder.class
)
public class WordleController {
    private final static List<Session> onRooms = new ArrayList<>();
    private static Session session1;
    private static Session session2;

    private static Wordle wordle1;
    private static Wordle wordle2;

    @OnOpen
    public void onOpen(Session session) throws IOException, EncodeException {
        onRooms.add(session);
    }

    @OnMessage
    public void onMessage(Session session, WordleRequest wordleRequest) throws EncodeException, IOException {
        try {
            switch (wordleRequest.getType()) {
                case ENTER_GAME -> {
                    if (session1 == null) {
                        session1 = session;
                        session1.getBasicRemote().sendObject(new WordleResponse(WordleResponseType.JOIN_GAME, PlayerNumber.PLAYER_1));
                        return;
                    }

                    if (session2 == null) {
                        session2 = session;
                        session2.getBasicRemote().sendObject(new WordleResponse(WordleResponseType.JOIN_GAME, PlayerNumber.PLAYER_2));
                    }

                    if (session1 != null && session2 != null) {
                        if (wordle1 == null && wordle2 == null) {
                            List<String> words = WordsMock.returnWords();

                            String secret = words.get((int) Math.floor(Math.random() * words.size()));

                            wordle1 = new Wordle(words, 6, secret);
                            wordle2 = new Wordle(words, 6, secret);
                        }
                    }
                }
                case SEND_WORD -> {
                    if (session == session1 && session1.isOpen()) {
                        MoveResult moveResult = wordle1.check(wordleRequest.getWord());
                        session1.getBasicRemote().sendObject(new WordleResponse(WordleResponseType.WORD_RESPONSE, moveResult, PlayerNumber.PLAYER_1));

                        if (List.of(Winner.NONE, Winner.WIN, Winner.LOSE).contains(moveResult.winner())) {
                            session2.getBasicRemote().sendObject(new WordleResponse(WordleResponseType.TYPE_WORD, wordleRequest.getWord()));
                        }

                        return;
                    }

                    if (session == session2 && session2.isOpen()) {
                        MoveResult moveResult = wordle2.check(wordleRequest.getWord());
                        session2.getBasicRemote().sendObject(new WordleResponse(WordleResponseType.WORD_RESPONSE, moveResult, PlayerNumber.PLAYER_2));

                        if (List.of(Winner.NONE, Winner.WIN, Winner.LOSE).contains(moveResult.winner())) {
                            session1.getBasicRemote().sendObject(new WordleResponse(WordleResponseType.TYPE_WORD, wordleRequest.getWord()));
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        session1 = null;
        session2 = null;

        System.out.println("Closed session " + closeReason.getReasonPhrase());
    }
}
