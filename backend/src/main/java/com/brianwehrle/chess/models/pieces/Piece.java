package com.brianwehrle.chess.models.pieces;

import com.brianwehrle.chess.models.Color;
import com.brianwehrle.chess.models.Direction;
import com.brianwehrle.chess.models.Square;

import java.util.ArrayList;

public abstract class Piece{

    protected PieceType type;
    protected final Color color;
    protected Square curSquare;
    protected boolean hasMoved;
    private boolean movedLastTurn; // only used for undoing moves

    public Piece(Color color) {
        this.curSquare = null;
        this.color = color;
        hasMoved = false;
        movedLastTurn = false;
    }

    public void setMovedLastTurn(boolean movedLastTurn) {
        this.movedLastTurn = movedLastTurn;
    }

    public boolean movedLastTurn() {
        return movedLastTurn;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public PieceType getType() {
        return type;
    }

    public Square getCurSquare() {
        return curSquare;
    }

    public Color getColor() {
        return color;
    }

    public void setCurSquare(Square curSquare) {
        this.curSquare = curSquare;
    }

    public abstract ArrayList<Direction> getDirections();

    public String toString(int code) {
        String res = "";

        if (color == Color.BLACK) {
            switch (type) {
                case KING -> res = (code == 0 ? "\u2654" : "K");
                case QUEEN -> res = (code == 0 ? "\u2655" : "Q");
                case ROOK -> res = (code == 0 ? "\u2656" : "R");
                case BISHOP -> res = (code == 0 ? "\u2657" : "B");
                case KNIGHT -> res = (code == 0 ? "\u2658" : "N");
                case PAWN -> res += (code == 0 ? "\u2659" : (char)(curSquare.getCol() + 'a'));
                default -> res = "Piece not associated with a type?";
            }
        } else {
            switch (type) {
                case KING -> res = (code == 0 ? "\u265A" : "K");
                case QUEEN -> res = (code == 0 ? "\u265B" : "Q");
                case ROOK -> res = (code == 0 ? "\u265C" : "R");
                case BISHOP -> res = (code == 0 ? "\u265D" : "B");
                case KNIGHT -> res = (code == 0 ? "\u265E" : "N");
                case PAWN -> res += (code == 0 ? "\u265F" : (char)(curSquare.getCol() + 'a'));
                default -> res = "Piece not associated with a type?";
            }
        }

        return res;
    }
}
