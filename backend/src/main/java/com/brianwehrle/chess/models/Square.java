package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.Piece;

import java.util.Optional;

public class Square {
    private final int row;
    private final int col;
    private Optional<Piece> piece;

    public Square(int row, int col) {
        this.row = row;
        this.col = col;
        this.piece = Optional.empty();
    }

    public boolean isEmpty() {
        return piece.isEmpty();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Optional<Piece> getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = Optional.ofNullable(piece);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Square that)) return false;
        return row == that.row && col == that.col;
    }

    public String toString(int code) {
        return piece.map(p -> p.toString(code)).orElse("\u2003");
    }

    public String getNotation() {
        char file = (char)(col + 'a');
        String rank = String.valueOf(row + 1);

        return file + rank;
    }
}
