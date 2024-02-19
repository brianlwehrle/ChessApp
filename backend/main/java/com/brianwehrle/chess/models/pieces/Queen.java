package com.brianwehrle.chess.models.pieces;

import com.brianwehrle.chess.models.Color;
import com.brianwehrle.chess.models.Direction;

import java.util.ArrayList;
import java.util.Arrays;

public class Queen extends Piece {

    public Queen(Color color) {
        super(color);
        super.type = PieceType.QUEEN;
        super.value = 9;
    }

    @Override
    public ArrayList<Direction> getDirections() {
        return new ArrayList<>(Arrays.asList(
                new Direction(1, 1),
                new Direction(-1, -1),
                new Direction(1, -1),
                new Direction(-1, 1),
                new Direction(0, 1),
                new Direction(0, -1),
                new Direction(1, 0),
                new Direction(-1, 0)
        ));
    }
}
