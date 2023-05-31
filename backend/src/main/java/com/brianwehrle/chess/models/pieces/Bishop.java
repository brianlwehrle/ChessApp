package com.brianwehrle.chess.models.pieces;

import com.brianwehrle.chess.models.Color;
import com.brianwehrle.chess.models.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public class Bishop extends Piece{
    public Bishop(Color color) {
        super(color);
        super.type = PieceType.BISHOP;
    }

    @Override
    public ArrayList<Pair> getDirections() {
        return new ArrayList<>(Arrays.asList(
                new Pair(1, 1),
                new Pair(-1, -1),
                new Pair(1, -1),
                new Pair (-1, 1)
        ));
    }
}
