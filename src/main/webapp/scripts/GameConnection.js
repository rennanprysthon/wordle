import GUI from "./GUI.js";

class GameConnection {
    constructor() {
        this.websocket = new WebSocket("http://localhost:8080/wordleGame/wordle")
        this.playerNumber = null;
    }

    sendWord(currentWord) {
        let json = {
            "word": currentWord,
            "type": "SEND_WORD"
        }

        this.websocket.send(JSON.stringify(json));
    }


    startGame() {
        let gui = new GUI(this);
        gui.registerEvents();

        const json = {
           "type": "ENTER_GAME"
        }

        this.websocket.send(JSON.stringify(json))

        this.startStopButton.innerHTML = 'Stop Game';
   }

   registerEvents() {
       this.startStopButton = document.querySelector("#start-stop-button")
       this.startStopButton.onclick = this.startGame.bind(this);
   }
}

let gameConnection = new GameConnection();
gameConnection.registerEvents();

