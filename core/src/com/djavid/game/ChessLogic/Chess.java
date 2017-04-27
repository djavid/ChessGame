package com.djavid.game.ChessLogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Chess {
    private List<Figure> pieces;
    private Tyle[][] board;
    boolean mainColor; //the color of a current player; 0 - black, 1 - white


    public Chess(boolean mainColor) {
        pieces = new ArrayList<Figure>();
        this.mainColor = mainColor;
        board = new Tyle[8][8];

        if (mainColor) { //white color arrangement
            board[0] = new Tyle[] {Tyle.rook_w_1, Tyle.knight_w_1, Tyle.bishop_w_1, Tyle.queen_w,
                    Tyle.king_w, Tyle.bishop_w_2, Tyle.knight_w_2, Tyle.rook_w_2};
            board[1] = new Tyle[] {Tyle.pawn_w_1, Tyle.pawn_w_2, Tyle.pawn_w_3, Tyle.pawn_w_4,
                    Tyle.pawn_w_5, Tyle.pawn_w_6, Tyle.pawn_w_7, Tyle.pawn_w_8};

            board[7] = new Tyle[] {Tyle.rook_b_2, Tyle.knight_b_2, Tyle.bishop_b_2, Tyle.queen_b,
                    Tyle.king_b, Tyle.bishop_b_1, Tyle.knight_b_1, Tyle.rook_b_1};
            board[6] = new Tyle[] {Tyle.pawn_b_1, Tyle.pawn_b_2, Tyle.pawn_b_3, Tyle.pawn_b_4,
                    Tyle.pawn_b_5, Tyle.pawn_b_6, Tyle.pawn_b_7, Tyle.pawn_b_8};
        } else { //black color arrangement
            board[0] = new Tyle[] {Tyle.rook_b_1, Tyle.knight_b_1, Tyle.bishop_b_1, Tyle.king_b,
                    Tyle.queen_b, Tyle.bishop_b_2, Tyle.knight_b_2, Tyle.rook_b_2};
            board[1] = new Tyle[] {Tyle.pawn_b_1, Tyle.pawn_b_2, Tyle.pawn_b_3, Tyle.pawn_b_4,
                    Tyle.pawn_b_5, Tyle.pawn_b_6, Tyle.pawn_b_7, Tyle.pawn_b_8};

            board[7] = new Tyle[] {Tyle.rook_w_2, Tyle.knight_w_2, Tyle.bishop_w_2, Tyle.king_w,
                    Tyle.queen_w, Tyle.bishop_w_1, Tyle.knight_w_1, Tyle.rook_w_1};
            board[6] = new Tyle[] {Tyle.pawn_w_1, Tyle.pawn_w_2, Tyle.pawn_w_3, Tyle.pawn_w_4,
                    Tyle.pawn_w_5, Tyle.pawn_w_6, Tyle.pawn_w_7, Tyle.pawn_w_8};
        }

        for (byte y = 2; y < 6; y++) {
            board[y] = new Tyle[8];
            for (byte x = 0; x < 8; x++) {
                board[y][x] = Tyle.empty;
            }
        }

        for (byte y = 0; y < 8; y++) {
            for (byte x = 0; x < 8; x++) {
                if (board[y][x] != Tyle.empty) {
                    char c = board[y][x].name().split("_")[1].charAt(0);
                    boolean color = c == 'w';

                    Coord coord = new Coord(x, y, false);

                    Figure fig = new Figure(board[y][x].name(), coord, color);
                    pieces.add(fig);
                    board[y][x].setFig(fig);
                }
            }
        }
    }

    public Figure getFigureByName(String name) {
        for (Figure fig : pieces) {
            if (fig.Name.equals(name)) {
                return fig;
            }
        }

        return new Figure();
    }

    public Tyle[][] getBoard() {
        return board;
    }

    public void setBoard(Tyle[][] board) {
        this.board = board;
    }

    public List<Figure> getPieces() {
        return pieces;
    }

    public void print() {
        for (byte y = 0; y < 8; y++) {
            for (byte x = 0; x < 8; x++) {
                System.out.print(board[y][x].name() + "\t");
            }
            System.out.println();
        }
    }

    public void updateCoords() {
        for (byte y = 0; y < 8; y++) {
            for (byte x = 0; x < 8; x++) {
                if (board[y][x].isFigure()) {
                    board[y][x].getFig().coord = new Coord(x, y, false);
                }
            }
        }
    }

    public Figure getPieceByCoord(Coord c) {
        if (board[c.Y][c.X].isFigure()) {
            return board[c.Y][c.X].getFig();
        }

        return new Figure();
    }

    public Tyle getTyleByCoord(Coord c) {
        return board[c.Y][c.X];
    }

    public boolean Check(boolean color) {
        for (Figure fig : pieces) {
            if (fig.color != color) {
                if (!fig.eated) {
                    if (fig.threatensKing(this)) return true;
                }
            }
        }

        return false;
    }

    public boolean Check(Coord from, Coord where, boolean color) {
        boolean res = false;
        byte whereX = where.X;
        byte whereY = where.Y;
        byte fromX = from.X;
        byte fromY = from.Y;

        Tyle whereTyle = getBoard()[where.Y][where.X];
        Tyle fromTyle = getBoard()[from.Y][from.X];

        //make a move
        if (getBoard()[where.Y][where.X].isFigure())
            getBoard()[where.Y][where.X].getFig().setEated();
        getBoard()[where.Y][where.X] = fromTyle;
        getBoard()[from.Y][from.X] = Tyle.empty;
        updateCoords();

        //check
        if (Check(color)) res = true;

        //undo move
        getBoard()[whereY][whereX] = whereTyle;
        getBoard()[fromY][fromX] = fromTyle;
        if (getBoard()[whereY][whereX].isFigure())
            getBoard()[whereY][whereX].getFig().eated = false;
        updateCoords();

        return res;
    }

    public boolean Checkmate(boolean color) {
        for (Figure fig : pieces) {
            if (fig.color == color) {
                if (fig.getAllowedMoves(this, true).size() != 0) return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        String s = "";
        for (Tyle[] arr : board) {
            for (Tyle tyle : arr) {
                s += tyle + " ";
            }
            s += "\n";
        }

        return s + "\n";
    }
}
