package com.brianwehrle.chess.models.pieces;

import com.brianwehrle.chess.models.Color;
import com.brianwehrle.chess.models.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public class Pawn extends Piece {

    public Pawn(Color color) {
        super(color);
        super.type = PieceType.PAWN;
    }

    @Override
    public ArrayList<Pair> getDirections() {
        int dy = (super.color == Color.WHITE ? 1 : -1);

        return new ArrayList<>(Arrays.asList(
                new Pair(0, dy)
        ));
    }
}
