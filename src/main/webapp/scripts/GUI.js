import words from "./words_pt.js";
import Wordle from "./Wordle.js";
import Winner from "./Winner.js";

export default class GUI {
    constructor(gameConnection) {
        this.gameConnection = gameConnection;

        this.registerSocketEvents(gameConnection);

        let tbody = document.querySelector("#player-board");
        let opponentBoard = document.querySelector("#opponent-board");
        this.opponent = {
           opponentBoard,
            row: 0,
            col: 0,
            isOver: false
        }
        this.wordle = {
            game: new Wordle(words, 6),
            row: 0,
            col: 0,
            currentWord: "",
            tbody: tbody,
            opponentBoard,
            isOver: false
        };
    }

    registerSocketEvents(gameConnection) {
        gameConnection.websocket.onmessage = this.onMessage.bind(this);
    }

    sendMessage(type, currentWord) {
       this.gameConnection.sendWord(currentWord);
    }

    onMessage(event) {
        let serverResponse = JSON.parse(event.data);

        switch (serverResponse.type) {
            case "JOIN_GAME": {
                this.playerNumber = serverResponse.playerNumber;

                break;
            }
            case "TYPE_WORD": {
                const { word } = serverResponse;

                console.log('opponent typed', word)

                this.showOpponentWord(word)

                break;
            }
            case "WORD_RESPONSE": {
                const { moveResult } = serverResponse;

                console.log({moveResult})

                if (moveResult.winner === "INVALID_WORD") {
                    let tr = this.wordle.tbody.rows[this.wordle.row];
                    for (let j = 0; j < tr.cells.length; j++) {
                        tr.cells[j].dataset.animation = "shake";
                        tr.cells[j].onanimationend = () => {
                            tr.cells[j].dataset.animation = "";
                        };
                    }

                    return;
                }

                this.showWord(moveResult)
            }
        }
    }

    showWord(mr) {
        let endOfGame = () => {
            this.wordle.col = 0;
            this.wordle.currentWord = "";
            this.wordle.row++;
            if (mr.winner === Winner.WIN) {
                let j = 1, td = this.wordle.tbody.rows[this.wordle.row - 1].cells[0];
                let endanim = cell => {
                    cell.dataset.animation = "bounce";
                    if (cell.nextSibling) {
                        setTimeout(() => endanim(cell.nextSibling), j++ * 100);
                    }
                };
                endanim(td);
                this.wordle.isOver = true;
            }
            if (this.wordle.isOver) {
                window.onkeyup = undefined;
                message.textContent = "Congratulations!";
                message.className = "bg-success text-white";
            } else if (mr.winner === Winner.LOSE) {
                window.onkeyup = undefined;
                message.textContent = `You lose! ${this.wordle.game.secret.toUpperCase()}`;
                message.className = "bg-secondary text-white";
            }
        };
        let styleKeyboard = () => {
            for (let i = 0; i < mr.hint.length; i++) {
                let index = mr.hint[i];
                let letter = this.wordle.currentWord[i];
                let b = document.querySelector(`button[data-value='${letter}']`);
                let bStyles = ["btn-secondary", "btn-warning", "btn-success"];
                if (bStyles.indexOf(b.dataset[`color`]) < bStyles.indexOf(bStyles[index])) {
                    b.classList.remove(b.dataset[`color`]);
                    b.dataset[`color`] = bStyles[index];
                }
                if (b.dataset.color) {
                    b.classList.remove("bg-secondary-subtle");
                    b.classList.add("text-white");
                    b.classList.add(b.dataset[`color`]);
                }
            }
            endOfGame();
        };
        let animation = cell => {
            let styles = ["secondary", "warning", "success"]
            cell.dataset.animation = "flip-in";
            cell.onanimationend = () => {
                cell.dataset.animation = "flip-out";
                cell.classList.add("text-white");
                cell.classList.add(`bg-${styles[mr.hint[cell.cellIndex]]}`);
                cell.classList.add(`border-${styles[mr.hint[cell.cellIndex]]}`);
                cell.onanimationend = () => {
                    cell.onanimationend = undefined;
                    if (cell.nextSibling) {
                        animation(cell.nextSibling);
                    } else {
                        styleKeyboard();
                    }
                };
            };
        };

        let td = this.wordle.tbody.rows[this.wordle.row].cells[0];
        animation(td);
    }

    showOpponentWord(opponentWord) {
        let animation = cell => {
            cell.dataset.animation = "flip-in";

            cell.onanimationend = () => {
                cell.dataset.animation = "flip-out";
                cell.classList.add("text-white");
                cell.classList.add('bg-secondary');
                cell.classList.add('border-secondary');

                cell.onanimationend = () => {
                    cell.onanimationend = undefined;
                    if (cell.nextSibling) {
                        animation(cell.nextSibling);
                    }
                };
            };
        };

        let tds = this.opponent.opponentBoard.rows[this.opponent.row].cells;
        let td = tds[0]

        for (let index = 0; index < opponentWord.length; index++) {
            tds[index].innerHTML = opponentWord[index];
        }

        animation(td);

        this.wordle.row++;
    }

    checkWord() {
        if (this.wordle.isOver) return;

        this.sendMessage("SEND_WORD", this.wordle.currentWord);
    }

    removeLetter() {
        if (this.wordle.col === 0) {
            return;
        }
        this.wordle.currentWord = this.wordle.currentWord.slice(0, -1);
        this.wordle.col--;
        if (!this.wordle.isOver) {
            let td = this.wordle.tbody.rows[this.wordle.row].cells[this.wordle.col];
            td.textContent = "";
            td.dataset.animation = "";
        }
    }
    addLetter(letter) {
        if (this.wordle.currentWord.length >= this.wordle.game.wordLength) {
            return;
        }
        if (!this.wordle.isOver) {
            let td = this.wordle.tbody.rows[this.wordle.row].cells[this.wordle.col];
            td.textContent = letter;
            td.dataset.animation = "pop";
            this.wordle.currentWord += letter;
            this.wordle.col++;
        }
    }
    process(key) {
        switch (key) {
            case "Enter":
                this.checkWord();
                break;
            case "Backspace":
                this.removeLetter();
                break;
            default:
                if (key >= 'a' && key <= 'z')
                    this.addLetter(key);
        }
    }
    keyPressed(evt) {
        this.process(evt.key);
    }
    buttonPressed(evt) {
        this.process(evt.currentTarget.dataset.value);
    }
    fillBoard() {
        let rows = "";
        for (let i = 0; i < this.wordle.game.maxTries; i++) {
            rows += "<tr>";
            for (let j = 0; j < this.wordle.game.wordLength; j++) {
                rows += "<td></td>";
            }
            rows += "</tr>";
        }
        this.wordle.tbody.innerHTML = rows;
    }
    fillOpponentBoard() {
        let rows = "";
        for (let i = 0; i < this.wordle.game.maxTries; i++) {
            rows += "<tr>";
            for (let j = 0; j < this.wordle.game.wordLength; j++) {
                rows += "<td></td>";
            }
            rows += "</tr>";
        }

        this.wordle.opponentBoard.innerHTML = rows;
    }
    registerEvents() {
        this.fillBoard();
        this.fillOpponentBoard();
        window.onkeyup = this.keyPressed.bind(this);
        let buttons = document.querySelectorAll("button");
        buttons.forEach(b => b.onclick = this.buttonPressed.bind(this));
    }
}
