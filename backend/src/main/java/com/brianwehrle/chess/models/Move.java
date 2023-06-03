package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.Bishop;
import com.brianwehrle.chess.models.pieces.Piece;

import java.util.Optional;

public class Move {

    public enum MoveType {
        STANDARD,
        DOUBLE,
        EN_PASSANT,
        CASTLE,
        PROMOTION
    }

    //private final Square start, end;
    private final int initialRow, initialCol, finalRow, finalCol;
    private final MoveType moveType;
    private final Piece.PieceType movingPiece;
    private final Optional<Piece> capturedPiece;
    private final Piece.PieceType promotionType;
    private final Color color;

    public Move (Piece.PieceType movingPiece, MoveType moveType, Square start, Square end, Color color) {
        initialRow = start.getRow();
        initialCol = start.getCol();
        finalRow = end.getRow();
        finalCol = end.getCol();
        this.color = color;
        this.moveType = moveType;
        this.movingPiece = movingPiece;
        capturedPiece = end.getPiece(); // could be null
        promotionType = null;
    }

    // promotion
    public Move (Piece.PieceType movingPiece, MoveType moveType, Piece.PieceType promotionType, Square start, Square end, Color color) {
        initialRow = start.getRow();
        initialCol = start.getCol();
        finalRow = end.getRow();
        finalCol = end.getCol();
        this.color = color;
        this.moveType = moveType;
        this.movingPiece = movingPiece;
        capturedPiece = end.getPiece(); // could be null
        this.promotionType = promotionType;
    }

    // en passant
    public Move (Piece.PieceType movingPiece, MoveType moveType, Square start, Square end, Piece piece, Color color) {
        initialRow = start.getRow();
        initialCol = start.getCol();
        finalRow = end.getRow();
        finalCol = end.getCol();
        this.color = color;
        this.moveType = moveType;
        this.movingPiece = movingPiece;
        this.capturedPiece = Optional.ofNullable(piece);
        promotionType = null;
    }

    public String toString(int code) {
        String res = "";
        char startCol = (char)(this.initialCol + 'a');
        String finalRow = String.valueOf(this.finalRow + 1);
        char finalCol = (char)(this.finalCol + 'a');

        if (color == Color.BLACK) {
            switch (movingPiece) {
                case KING -> res = (code == 0 ? "♔" : "K");
                case QUEEN -> res = (code == 0 ? "♕" : "Q");
                case ROOK -> res = (code == 0 ? "♖" : "R");
                case BISHOP -> res = (code == 0 ? "♗" : "B");
                case KNIGHT -> res = (code == 0 ? "♘" : "N");
                case PAWN -> res += (code == 0 ? "♙" : startCol);
                default -> res = "Piece not associated with a type?";
            }
        } else {
            switch (movingPiece) {
                case KING -> res = (code == 0 ? "♚" : "K");
                case QUEEN -> res = (code == 0 ? "♛" : "Q");
                case ROOK -> res = (code == 0 ? "♜" : "R");
                case BISHOP -> res = (code == 0 ? "♝" : "B");
                case KNIGHT -> res = (code == 0 ? "♞" : "N");
                case PAWN -> res += (code == 0 ? "♟" : startCol);
                default -> res = "Piece not associated with a type?";
            }
        }

        if (movingPiece == Piece.PieceType.PAWN) {
            if (moveType == MoveType.EN_PASSANT) {
                return (char) startCol + "x" + finalCol + finalRow;
            }
            if (capturedPiece.isPresent()) { // capture
                res = startCol + "x" + finalCol + finalRow;
            } else { // move
                res = startCol+ finalRow;
            }
            if (promotionType != null) {
                switch (promotionType) {
                    case BISHOP -> res += "=B";
                    case QUEEN -> res += "=Q";
                    case ROOK -> res += "=R";
                    case KNIGHT -> res += "=N";
                }
            }

            return res;
        }

        // capture
        if (capturedPiece.isPresent()) {
            return res + "x" + finalCol + finalRow;
        }

        // long castle
        if (moveType == MoveType.CASTLE && initialCol == 0) {
            return "O-O-O";
        }

        // short castle
        if (moveType == MoveType.CASTLE && initialCol == 7) {
            return "O-O";
        }

        return res + finalCol + finalRow;
    }

    public int getInitialRow() {
        return initialRow;
    }

    public int getInitialCol() {
        return initialCol;
    }

    public int getFinalRow() {
        return finalRow;
    }

    public int getFinalCol() {
        return finalCol;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public Piece.PieceType getMovingPiece() { return movingPiece; }

//    public Optional<Piece> getCapturedPiece() {
//        return capturedPiece;
//    }

    public Piece.PieceType getPromotionType() {
        return promotionType;
    }

    public Color getColor() {
        return color;
    }
}
