package com.brianwehrle.chess.models.pieces;

import com.brianwehrle.chess.models.Color;
import com.brianwehrle.chess.models.Direction;

import java.util.ArrayList;
import java.util.Arrays;

public class Bishop extends Piece{
    public Bishop(Color color) {
        super(color);
        super.type = PieceType.BISHOP;
        super.value = 3;
    }

    @Override
    public ArrayList<Direction> getDirections() {
        return new ArrayList<>(Arrays.asList(
                new Direction(1, 1),
                new Direction(-1, -1),
                new Direction(1, -1),
                new Direction(-1, 1)
        ));
    }
}
