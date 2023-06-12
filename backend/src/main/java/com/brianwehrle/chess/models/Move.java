package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.Piece;
import com.brianwehrle.chess.utilities.Converter;

public class Move {

    public enum MoveType {
        STANDARD,
        CAPTURE,
        EN_PASSANT,
        CASTLE,
        PROMOTION_KNIGHT,
        PROMOTION_BISHOP,
        PROMOTION_ROOK,
        PROMOTION_QUEEN
    }

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

    public String toString(int code) {
        return Converter.moveToAlgebraic(this, code);
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
