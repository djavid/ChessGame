package com.djavid.game.ChessLogic;


import java.io.Serializable;

public class Move implements Serializable {
    public static Coord get(String moveName, Figure fig, int dist, Chess chess) {
        if (moveName.equals("forward")) {
            return forward(fig, dist, chess);
        }
        else if (moveName.equals("backward")) {
            return backward(fig, dist, chess);
        }
        else if (moveName.equals("left")) {
            return left(fig, dist, chess);
        }
        else if (moveName.equals("right")) {
            return right(fig, dist, chess);
        }
        else if (moveName.equals("forward_left")) {
            return forward_left(fig, dist, chess);
        }
        else if (moveName.equals("forward_right")) {
            return forward_right(fig, dist, chess);
        }
        else if (moveName.equals("backward_left")) {
            return backward_left(fig, dist, chess);
        }
        else if (moveName.equals("backward_right")) {
            return backward_right(fig, dist, chess);
        }
        else if (moveName.equals("knight_fl")) {
            return knight_fl(fig, chess);
        }
        else if (moveName.equals("knight_fr")) {
            return knight_fr(fig, chess);
        }
        else if (moveName.equals("knight_bl")) {
            return knight_bl(fig, chess);
        }
        else if (moveName.equals("knight_br")) {
            return knight_br(fig, chess);
        }
        else if (moveName.equals("knight_lf")) {
            return knight_lf(fig, chess);
        }
        else if (moveName.equals("knight_rf")) {
            return knight_rf(fig, chess);
        }
        else if (moveName.equals("knight_lb")) {
            return knight_lb(fig, chess);
        }
        else if (moveName.equals("knight_rb")) {
            return knight_rb(fig, chess);
        }
        else {
            return new Coord((byte)0, (byte)0, true);
        }
    }

    private static Coord forward(Figure fig, int dist, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = fig.coord.X;
            Y_new = (byte)(fig.coord.Y - dist);

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = fig.coord.X;
            Y_new = (byte)(fig.coord.Y + dist);

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static Coord backward(Figure fig, int dist, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = fig.coord.X;
            Y_new = (byte)(fig.coord.Y + dist);

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = fig.coord.X;
            Y_new = (byte)(fig.coord.Y - dist);

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static Coord left(Figure fig, int dist, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = (byte) (fig.coord.X + dist);
            Y_new = fig.coord.Y;

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = (byte) (fig.coord.X - dist);
            Y_new = fig.coord.Y;

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static Coord right(Figure fig, int dist, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = (byte) (fig.coord.X - dist);
            Y_new = fig.coord.Y;

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = (byte) (fig.coord.X + dist);
            Y_new = fig.coord.Y;

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static Coord forward_left(Figure fig, int dist, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = (byte)(fig.coord.X + dist);
            Y_new = (byte)(fig.coord.Y - dist);

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = (byte)(fig.coord.X - dist);
            Y_new = (byte)(fig.coord.Y + dist);

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static Coord forward_right(Figure fig, int dist, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = (byte)(fig.coord.X - dist);
            Y_new = (byte)(fig.coord.Y - dist);

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = (byte)(fig.coord.X + dist);
            Y_new = (byte)(fig.coord.Y + dist);

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static Coord backward_left(Figure fig, int dist, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = (byte)(fig.coord.X + dist);
            Y_new = (byte)(fig.coord.Y + dist);

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = (byte)(fig.coord.X - dist);
            Y_new = (byte)(fig.coord.Y - dist);

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static Coord backward_right(Figure fig, int dist, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = (byte)(fig.coord.X - dist);
            Y_new = (byte)(fig.coord.Y + dist);

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = (byte)(fig.coord.X + dist);
            Y_new = (byte)(fig.coord.Y - dist);

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static Coord knight_fl(Figure fig, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = (byte)(fig.coord.X + 1);
            Y_new = (byte)(fig.coord.Y - 2);

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = (byte)(fig.coord.X - 1);
            Y_new = (byte)(fig.coord.Y + 2);

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static Coord knight_fr(Figure fig, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = (byte)(fig.coord.X - 1);
            Y_new = (byte)(fig.coord.Y - 2);

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = (byte)(fig.coord.X + 1);
            Y_new = (byte)(fig.coord.Y + 2);

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static Coord knight_bl(Figure fig, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = (byte)(fig.coord.X + 1);
            Y_new = (byte)(fig.coord.Y + 2);

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = (byte)(fig.coord.X - 1);
            Y_new = (byte)(fig.coord.Y - 2);

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static Coord knight_br(Figure fig, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = (byte)(fig.coord.X - 1);
            Y_new = (byte)(fig.coord.Y + 2);

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = (byte)(fig.coord.X + 1);
            Y_new = (byte)(fig.coord.Y - 2);

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static Coord knight_lf(Figure fig, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = (byte)(fig.coord.X + 2);
            Y_new = (byte)(fig.coord.Y - 1);

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = (byte)(fig.coord.X - 2);
            Y_new = (byte)(fig.coord.Y + 1);

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static Coord knight_rf(Figure fig, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = (byte)(fig.coord.X - 2);
            Y_new = (byte)(fig.coord.Y - 1);

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = (byte)(fig.coord.X + 2);
            Y_new = (byte)(fig.coord.Y + 1);

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static Coord knight_lb(Figure fig, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = (byte)(fig.coord.X + 2);
            Y_new = (byte)(fig.coord.Y + 1);

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = (byte)(fig.coord.X - 2);
            Y_new = (byte)(fig.coord.Y - 1);

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static Coord knight_rb(Figure fig, Chess chess) {
        byte X_new, Y_new;

        if (fig.color != chess.mainColor) {
            X_new = (byte)(fig.coord.X - 2);
            Y_new = (byte)(fig.coord.Y + 1);

            return move(chess.getBoard(), fig, X_new, Y_new);
        } else {
            X_new = (byte)(fig.coord.X + 2);
            Y_new = (byte)(fig.coord.Y - 1);

            return move(chess.getBoard(), fig, X_new, Y_new);
        }
    }

    private static boolean isAllowed(Tyle[][] board, byte X_new, byte Y_new) {
        if (X_new > 7 || Y_new > 7 || X_new < 0 || Y_new < 0) return false;

        return true;
    }

    private static Coord move(Tyle[][] board, Figure fig, byte X_new, byte Y_new) {
        if (isAllowed(board, X_new, Y_new)) {
            return new Coord(X_new, Y_new, false);
        } else {
            return new Coord((byte)0, (byte)0, true);
        }
    }

    public static Tyle[][] make(Tyle[][] board, Figure fig, Coord c, boolean update) {
        if (isAllowed(board, c.X, c.Y)) {
            if (!update) {
                Tyle[][] b = board.clone();
                if(b[c.Y][c.X].isFigure()) b[c.Y][c.X].getFig().setEated();
                b[c.Y][c.X] = b[fig.coord.Y][fig.coord.X];
                b[fig.coord.Y][fig.coord.X] = Tyle.empty;

                return b;
            } else {
                if (board[c.Y][c.X].isFigure()) board[c.Y][c.X].getFig().setEated();
                board[c.Y][c.X] = board[fig.coord.Y][fig.coord.X];
                board[fig.coord.Y][fig.coord.X] = Tyle.empty;

                return board;
            }
        } else {
            return null;
        }
    }
}
