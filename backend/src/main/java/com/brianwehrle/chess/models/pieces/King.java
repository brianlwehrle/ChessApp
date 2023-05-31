package com.brianwehrle.chess.models.pieces;

import com.brianwehrle.chess.models.Color;
import com.brianwehrle.chess.models.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public class King extends Piece {

    public King(Color color) {
        super(color);
        super.type = PieceType.KING;
    }

    @Override
    public ArrayList<Pair> getDirections() {
        return new ArrayList<>(Arrays.asList(
                new Pair(1, 1),
                new Pair(-1, -1),
                new Pair(1, -1),
                new Pair (-1, 1),
                new Pair(0, 1),
                new Pair(0, -1),
                new Pair(1, 0),
                new Pair (-1, 0)
        ));
    }
}
