package com.djavid.game.ChessLogic;


import java.io.Serializable;

public class Coord implements Serializable {
    public byte X;
    public byte Y;
    public boolean empty;
    private boolean isNull;

    public Coord(byte x, byte y, boolean empty) {
        this.empty = empty;

        if (!empty) {
            X = x;
            Y = y;
        }
    }

    public Coord() {
        isNull = true;
    }

    public boolean isNull() {
        return isNull;
    }

    public Coord invert() {
        return new Coord((byte)(7 - X), (byte)(7 - Y), false);
    }

    @Override
    public String toString() {
        return "[" + X + ";" + Y + "]";
    }
}
