package com.brianwehrle.chess.models.pieces;

import com.brianwehrle.chess.models.Color;
import com.brianwehrle.chess.models.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(Color color) {
        super(color);
        super.type = PieceType.PAWN;
    }

    @Override
    public ArrayList<Direction> getDirections() {
        int dy = (color == Color.WHITE ? 1 : -1);

        if (square.getRow() == 1  || square.getRow() == 6) {
            return new ArrayList<>(Arrays.asList(
                    new Direction(0, dy),
                    new Direction(0, dy * 2)
            ));
        } else {
            return new ArrayList<>(List.of(
                    new Direction(0, dy)
            ));
        }
    }
}
