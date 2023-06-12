package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.Piece;

public class Square {
    private final int row;
    private final int col;
    private Piece piece;

    public Square(int row, int col) {
        this.row = row;
        this.col = col;
        this.piece = null;
    }

    public boolean isEmpty() {
        return piece == null;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Square that)) return false;
        return row == that.row && col == that.col;
    }

    public String toString(int code) {
        return piece.toString(code);
    }

    public String getNotation() {
        char file = (char)(col + 'a');
        String rank = String.valueOf(row + 1);

        return file + rank;
    }
}
