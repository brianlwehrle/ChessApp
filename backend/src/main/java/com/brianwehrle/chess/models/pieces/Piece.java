package com.brianwehrle.chess.models.pieces;

import com.brianwehrle.chess.models.Color;
import com.brianwehrle.chess.models.Direction;
import com.brianwehrle.chess.models.Square;

import java.util.ArrayList;

public abstract class Piece {

    public enum PieceType {
        PAWN,
        KNIGHT,
        BISHOP,
        ROOK,
        QUEEN,
        KING
    }

    protected PieceType type;
    protected final Color color;
    protected Square square;
    protected int value;

    public Piece(Color color) {
        this.square = null;
        this.color = color;
    }

    public boolean differentColor(Piece piece) {
        return this.color != piece.getColor();
    }

    public void setSquare(Square curSquare) {
        this.square = curSquare;
    }

    public PieceType getType() {
        return type;
    }

    public Square square() {
        return square;
    }

    public Color getColor() {
        return color;
    }

    public abstract ArrayList<Direction> getDirections();

    public String toString(int code) {
        String res = "";

        if (color == Color.BLACK) {
            switch (type) {
                case KING -> res = (code == 0 ? "♔" : "K");
                case QUEEN -> res = (code == 0 ? "♕" : "Q");
                case ROOK -> res = (code == 0 ? "♖" : "R");
                case BISHOP -> res = (code == 0 ? "♗" : "B");
                case KNIGHT -> res = (code == 0 ? "♘" : "N");
                case PAWN -> res += (code == 0 ? "♙" : (char)(square.getCol() + 'a'));
                default -> res = "Piece not associated with a type?";
            }
        } else {
            switch (type) {
                case KING -> res = (code == 0 ? "♚" : "K");
                case QUEEN -> res = (code == 0 ? "♛" : "Q");
                case ROOK -> res = (code == 0 ? "♜" : "R");
                case BISHOP -> res = (code == 0 ? "♝" : "B");
                case KNIGHT -> res = (code == 0 ? "♞" : "N");
                case PAWN -> res += (code == 0 ? "♟" : (char)(square.getCol() + 'a'));
                default -> res = "Piece not associated with a type?";
            }
        }

        return res;
    }
}
