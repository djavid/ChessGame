package com.djavid.game.ChessLogic;

import com.djavid.game.Network.Packets;
import com.djavid.game.Network.Packets.*;
import java.util.ArrayList;
import java.util.List;


public class  Figure {
    public class AllowedMove {
        public String moveName;
        public Coord dest;
        public boolean eats;
        public byte dist;

        public AllowedMove(String moveName, Coord dest, boolean eats, byte dist) {
            this.moveName = moveName;
            this.dest = dest;
            this.eats = eats;
            this.dist = dist;
        }

        @Override
        public String toString() {
            return "AllowedMove{" +
                    "moveName='" + moveName + '\'' +
                    ",dest=" + dest +
                    ",eats=" + eats +
                    '}';
        }
    }

    private boolean threatens = false;
    public boolean eated;
    private byte max_dist;
    public boolean isNull;
    public String Name;
    public List<String> figureMoves;
    public Coord coord;
    public boolean color; //the color of figure; 0 - black, 1 - white


    public Tyle[][] move(AllowedMove move, Chess chess, boolean update) {
        if (!chess.getPieceByCoord(coord).isNull) {
            if (figureMoves.contains(move.moveName)) {
                if (canMove(move, chess, move.dest, move.dist, false)) {
                    Tyle[][] b = Move.make(chess.getBoard(), this, move.dest, update);
                    if (b == null) return null;

                    if (update) {
                        chess.updateCoords();
                        if (Name.startsWith("pawn")) {
                            max_dist = 1;
                        }
                    } else {
                        return b;
                    }
                }
            }
        }

        return null;
    }

    public Tyle[][] move(Packets.Move move, Chess chess, boolean update) {
        Coord dest = new Coord(move.destX, move.destY, false).invert();
        AllowedMove m = new AllowedMove(move.moveName, dest, move.eats, move.dist);

        if (!chess.getPieceByCoord(coord).isNull) {
            if (figureMoves.contains(m.moveName)) {
                if (canMove(m, chess, m.dest, m.dist, false)) {
                    Tyle[][] b = Move.make(chess.getBoard(), this, m.dest, update);

                    if (update) {
                        chess.updateCoords();
                        if (chess.Checkmate(!color)) move.checkmate = true;
                        if (Name.startsWith("pawn")) {
                            max_dist = 1;
                        }
                    } else {
                        return b;
                    }
                }
            }
        }

        return new Tyle[0][];
    }

    private boolean canMove(AllowedMove allowedMove, Chess chess, Coord new_coord, byte dist, boolean checkForCheck) {
        boolean key = true;

        if (new_coord.empty) return false;

        if (chess.getPieceByCoord(new_coord).isNull) { //if is empty
            if (Name.startsWith("pawn")) {
                if (allowedMove.moveName.equals("forward_left") || allowedMove.moveName.equals("forward_right")) {
                    return false;
                }
            }
        } else { //if is figure
            Figure fig = chess.getPieceByCoord(new_coord);
            boolean isEnemy = isEnemy(chess.getTyleByCoord(new_coord));

            if (isEnemy) {
                if (Name.startsWith("pawn")) { //if a player is pawn
                    if (allowedMove.moveName.equals("forward_left") || allowedMove.moveName.equals("forward_right")) {
                        if (dist > 1) return false;
                    }

                    if (allowedMove.moveName.equals("forward")) {
                        return false;
                    }
                }

                if (fig.Name.startsWith("king")) { //if enemy is king
                    threatens = true;
                    return false;
                }

            } else {
                //TODO: рокировка
                key = false;
            }
        }

        if (checkForCheck && chess.Check(coord, new_coord, color)) {
            key = false;
        }

        return key;
    }

    public Figure(String name, Coord coord, boolean color) {
        eated = false;
        Name = name;
        this.coord = coord;
        this.color = color;
        this.isNull = false;

        figureMoves = new ArrayList<String>();
        if (name.startsWith("pawn")) {
            figureMoves.add("forward");
            figureMoves.add("forward_left");
            figureMoves.add("forward_right");
            max_dist = 2;
        }
        else if (name.startsWith("rook")) {
            figureMoves.add("forward");
            figureMoves.add("backward");
            figureMoves.add("left");
            figureMoves.add("right");
            max_dist = 7;
        }
        else if (name.startsWith("knight")) {
            figureMoves.add("knight_fl");
            figureMoves.add("knight_fr");
            figureMoves.add("knight_bl");
            figureMoves.add("knight_br");
            figureMoves.add("knight_lf");
            figureMoves.add("knight_rf");
            figureMoves.add("knight_lb");
            figureMoves.add("knight_rb");
            max_dist = 1;
        }
        else if (name.startsWith("bishop")) {
            figureMoves.add("forward_left");
            figureMoves.add("forward_right");
            figureMoves.add("backward_left");
            figureMoves.add("backward_right");
            max_dist = 7;
        }
        else if (name.startsWith("queen") || name.startsWith("king")) {
            figureMoves.add("forward");
            figureMoves.add("backward");
            figureMoves.add("left");
            figureMoves.add("right");
            figureMoves.add("forward_left");
            figureMoves.add("forward_right");
            figureMoves.add("backward_left");
            figureMoves.add("backward_right");

            if (name.startsWith("queen")) {
                max_dist = 7;
            } else {
                max_dist = 1;
            }
        }
    }

    public Figure() {
        isNull = true;
    }

    public List<AllowedMove> getAllowedMoves(Chess chess, boolean checkForCheck) {
        threatens = false;
        List<AllowedMove> allowedMoves = new ArrayList<AllowedMove>();
        for (String move_name: figureMoves) {

            for (byte dist = 1; dist <= max_dist; dist++) {
                Coord new_coord = Move.get(move_name, this, dist, chess);

                Tyle fig = chess.getTyleByCoord(new_coord);
                AllowedMove allowedMove = new AllowedMove(move_name, new_coord, true, dist);

                if (canMove(allowedMove, chess, new_coord, dist, checkForCheck)) {
                    if (isEnemy(fig)) {
                        allowedMoves.add(new AllowedMove(move_name, new_coord, true, dist));
                        break;
                    } else {
                        allowedMoves.add(new AllowedMove(move_name, new_coord, false, dist));
                    }

                } else break;
            }
        }

        return allowedMoves;
    }



    public boolean isEnemy(Tyle tyle) {
        if (tyle.name().equals("empty") || tyle.name().equals("nothing")) return false;
        char color = tyle.name().split("_")[1].charAt(0);
        boolean c = color == 'w';

        return c != this.color;
    }

    public boolean threatensKing(Chess chess) {
        getAllowedMoves(chess, false);

        return threatens;
    }

    public void setEated() {
        eated = true;
    }
    public boolean isEated() {
        return eated;
    }
}
