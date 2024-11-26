package br.edu.ifpe.web3.wordle.infrastructure.decoders;

import br.edu.ifpe.web3.wordle.model.WordleRequest;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

public class WordleRequestDecoder implements Decoder.Text<WordleRequest> {
    @Override
    public void init(final EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public WordleRequest decode(final String textMessage) throws DecodeException {
        return JsonbBuilder.create().fromJson(textMessage, WordleRequest.class);
    }

    @Override
    public boolean willDecode(final String s) {
        try {
            JsonbBuilder.create().fromJson(s, WordleRequest.class);
            return true;
        } catch (JsonbException ex) {
            return false;
        }
    }
}

