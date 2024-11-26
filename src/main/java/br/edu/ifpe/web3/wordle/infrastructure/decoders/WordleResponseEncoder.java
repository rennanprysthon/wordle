package br.edu.ifpe.web3.wordle.infrastructure.decoders;

import br.edu.ifpe.web3.wordle.model.WordleResponse;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;

public class WordleResponseEncoder implements Encoder.Text<WordleResponse> {

    @Override
    public void init(final EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public String encode(final WordleResponse message) throws EncodeException {
        return JsonbBuilder.create().toJson(message);
    }
}
