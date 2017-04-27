package com.djavid.game.ChessLogic;


public enum Tyle {
    nothing,
    empty,
    pawn_w_1, pawn_w_2, pawn_w_3, pawn_w_4, pawn_w_5, pawn_w_6, pawn_w_7, pawn_w_8,
    pawn_b_1, pawn_b_2, pawn_b_3, pawn_b_4, pawn_b_5, pawn_b_6, pawn_b_7, pawn_b_8,
    rook_w_1, rook_w_2,
    rook_b_1, rook_b_2,
    knight_w_1, knight_w_2,
    knight_b_1, knight_b_2,
    bishop_w_1, bishop_w_2,
    bishop_b_1, bishop_b_2,
    queen_w, queen_b,
    king_w, king_b;

    private Figure fig;

    public boolean isFigure() {
        return fig != null;
    }

    public Figure getFig() {
        return fig;
    }

    public void setFig(Figure fig) {
        this.fig = fig;
    }

    @Override
    public String toString() {
        return "Tyle{" +
                this.name() +
                '}';
    }
}
