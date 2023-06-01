package com.brianwehrle.chess.models.pieces;

import com.brianwehrle.chess.models.Color;
import com.brianwehrle.chess.models.Direction;

import java.util.ArrayList;
import java.util.Arrays;

public class Pawn extends Piece {

    public Pawn(Color color) {
        super(color);
        super.type = PieceType.PAWN;
    }

    @Override
    public ArrayList<Direction> getDirections() {
        int dy = (super.color == Color.WHITE ? 1 : -1);

        return new ArrayList<>(Arrays.asList(
                new Direction(0, dy)
        ));
    }
}
