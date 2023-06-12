package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.Piece;
import com.brianwehrle.chess.utilities.Convert;

import java.util.Optional;

public class Move {

    public enum MoveType {
        STANDARD,
        CAPTURE,
        EN_PASSANT,
        CASTLE,
        PROMOTION
    }

    //private final Square start, end;
    private final int initialRow, initialCol, finalRow, finalCol;
    private final MoveType moveType;
    private final Piece.PieceType typeOfPiece;
    private final Piece.PieceType promotionType;

    public Move (Piece.PieceType typeOfPiece, MoveType moveType, Square start, Square end) {
        initialRow = start.getRow();
        initialCol = start.getCol();
        finalRow = end.getRow();
        finalCol = end.getCol();
        this.moveType = moveType;
        this.typeOfPiece = typeOfPiece;
        promotionType = null;
    }

    // promotion
    public Move (Piece.PieceType typeOfPiece, MoveType moveType, Piece.PieceType promotionType, Square start, Square end) {
        initialRow = start.getRow();
        initialCol = start.getCol();
        finalRow = end.getRow();
        finalCol = end.getCol();
        this.moveType = moveType;
        this.typeOfPiece = typeOfPiece;
        this.promotionType = promotionType;
    }

    public String toString(int code) {
        return Convert.moveToAlgebraic(this, code);
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

    public MoveType moveType() {
        return moveType;
    }

    public Piece.PieceType getTypeOfPiece() { return typeOfPiece; }

    public Piece.PieceType getPromotionType() {
        return promotionType;
    }
}
