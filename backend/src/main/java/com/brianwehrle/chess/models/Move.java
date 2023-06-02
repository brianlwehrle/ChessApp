package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.Piece;

public class Move {

    public enum MoveType {
        STANDARD,
        EN_PASSANT,
        CASTLE,
        DOUBLE
    }

    private final Square start, end;
    private final MoveType type;
    private Piece movingPiece;
    //private Optional<Piece> capturedPiece;
    private Piece capturedPiece;

    public Move (MoveType type, Square start, Square end) {
        this.start = start;
        this.end = end;
        this.type = type;
        movingPiece = start.getPiece();
        capturedPiece = end.getPiece(); // could be null
    }

    // en passant
    public Move (MoveType type, Square start, Square end, Piece capturedPiece) {
        this.start = start;
        this.end = end;
        this.type = type;
        movingPiece = start.getPiece();
        this.capturedPiece = capturedPiece; // not null
    }

    public String toString() {
        char startCol = (char)(start.getCol() + 'a');
        char endCol = (char)(end.getCol() + 'a');
        //String startRow = String.valueOf(start.getRow() + 1);
        String endRow = String.valueOf(end.getRow() + 1);

        if (movingPiece.getType() == Piece.PieceType.PAWN) {
            if (type == MoveType.EN_PASSANT) {
                return "" + startCol + "x" + endCol + endRow;
            }
            if (end.getPiece() != null) { // capture
                return "" + startCol + "x" + endCol + endRow;
            } else { // move
                return "" + startCol + endRow;
            }
        }

        // capture
        if (end.getPiece() != null) {
            return start.toString(0) + "x" + endCol + endRow;
        }

        // long castle
        if (type==MoveType.CASTLE && start.getCol() == 0) {
            return "O-O-O";
        }

        // short castle
        if (type==MoveType.CASTLE && start.getCol() == 7) {
            return "O-O";
        }

        return start.toString(0) + endCol + endRow;
    }

    public Square start() {
        return start;
    }

    public Square end() {
        return end;
    }

    public MoveType getType() {
        return type;
    }

    public Piece getMovingPiece() { return movingPiece; }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }
}
