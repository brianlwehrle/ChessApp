package com.brianwehrle.chess.dtos;

import com.brianwehrle.chess.models.Move;
import com.brianwehrle.chess.models.pieces.Piece;

public class MoveDto {
    private int startRow;
    private int startCol;
    private int endRow;
    private int endCol;
    private Move.MoveType moveType;
    private Piece.PieceType pieceType;

    public MoveDto() {}

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public int getEndCol() {
        return endCol;
    }

    public void setEndCol(int endCol) {
        this.endCol = endCol;
    }

    public Move.MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(Move.MoveType moveType) {
        this.moveType = moveType;
    }

    public Piece.PieceType getPieceType() {
        return pieceType;
    }

    public void setPieceType(Piece.PieceType pieceType) {
        this.pieceType = pieceType;
    }
}
