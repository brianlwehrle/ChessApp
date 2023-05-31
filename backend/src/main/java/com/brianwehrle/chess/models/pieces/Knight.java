package com.brianwehrle.chess.models.pieces;

import com.brianwehrle.chess.models.Color;
import com.brianwehrle.chess.models.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public class Knight extends Piece {
    public Knight(Color color) {
        super(color);
        super.type = PieceType.KNIGHT;
    }

    @Override
    public ArrayList<Pair> getDirections() {
        return new ArrayList<>(Arrays.asList(
                new Pair(2, 1),
                new Pair(2, -1),
                new Pair(-2, 1),
                new Pair (-2, -1),
                new Pair(1, 2),
                new Pair(1, -2),
                new Pair (-1, 2),
                new Pair(-1, -2)
        ));
    }
}
