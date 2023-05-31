package com.brianwehrle.chess.models.pieces;

import com.brianwehrle.chess.models.Color;
import com.brianwehrle.chess.models.Pair;
import com.brianwehrle.chess.models.PieceType;

import java.util.ArrayList;
import java.util.Arrays;

public class Queen extends Piece {

    public Queen(Color color) {
        super(color);
        super.type = PieceType.QUEEN;
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
