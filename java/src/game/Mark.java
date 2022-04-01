package game;

/**
 * Represents the mark in the Pentago game.
 * There are 3 possibilities, Mark.XX, Mark.OO and Mark.EMPTY
 *
 * @author bart.fischer
 */

public enum Mark {

    EMPTY, XX, OO;


    public Mark other() {
        if (this.equals(XX)) {
            return OO;

        } else if (this.equals(OO)) {
            return XX;
        }
        return EMPTY;
    }

}