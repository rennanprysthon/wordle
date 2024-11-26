import Winner from "./Winner.js";
import MoveResult from "./MoveResult.js";
import NotInWordListError from "./NotInWordListError.js";

export default class Wordle {
    constructor(words, tries) {
        this.words = words;
        this.tries = 0;
        this.maxTries = tries;
        this.wordLength = this.secret.length;
    }
}