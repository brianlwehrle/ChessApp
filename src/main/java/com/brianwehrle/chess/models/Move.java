package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.Piece;
import com.brianwehrle.chess.utilities.Converter;

import java.util.Objects;

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

    private int startRow, startCol, endRow, endCol;
    private MoveType moveType;
    private Piece.PieceType pieceType;

    public Move() {

    }

    public Move (Piece.PieceType pieceType, MoveType moveType, Square start, Square end) {
        startRow = start.getRow();
        startCol = start.getCol();
        endRow = end.getRow();
        endCol = end.getCol();

        this.moveType = moveType;
        this.pieceType = pieceType;
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

    public Piece.PieceType getPieceType() { return pieceType; }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public void setEndCol(int endCol) {
        this.endCol = endCol;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    public void setPieceType(Piece.PieceType pieceType) {
        this.pieceType = pieceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move move)) return false;
        return startRow == move.startRow && startCol == move.startCol && endRow == move.endRow && endCol == move.endCol && moveType == move.moveType && pieceType == move.pieceType;
    }
}
