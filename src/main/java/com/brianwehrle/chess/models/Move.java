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

    private final int startRow, startCol, endRow, endCol;
    private final MoveType moveType;
    private final Piece.PieceType typeOfPiece;

    public Move (Piece.PieceType typeOfPiece, MoveType moveType, Square start, Square end) {
        startRow = start.getRow();
        startCol = start.getCol();
        endRow = end.getRow();
        endCol = end.getCol();

        this.moveType = moveType;
        this.typeOfPiece = typeOfPiece;
    }

    // to get fancy chess icon string
    public String toString(int code) {
        return Converter.moveToAlgebraic(this, code);
    }

    @Override
    public String toString() {
        return Converter.moveToAlgebraic(this, 0);
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getEndRow() {
        return endRow;
    }

    public int getEndCol() {
        return endCol;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public Piece.PieceType getTypeOfPiece() { return typeOfPiece; }
}
