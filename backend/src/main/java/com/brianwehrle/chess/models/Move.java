package com.brianwehrle.chess.models;

public record Move(Square start, Square end) {

    public String toString() {
        return start.toString(1) + (char)(end.getCol() + 'a') + (end.getRow() + 1);
    }

}
