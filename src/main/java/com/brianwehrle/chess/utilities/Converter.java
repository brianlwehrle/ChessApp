package com.brianwehrle.chess.utilities;

import com.brianwehrle.chess.models.Game;
import com.brianwehrle.chess.models.Move;
import com.brianwehrle.chess.models.pieces.Piece;

import java.util.ArrayList;
/*
    Handles various notation conversion functions, such as converting from PGN,
    fen, etc. to Move/List of Moves and vice versa
 */
public class Converter {

    public static String moveToAlgebraic(Move move, int code) {
        String piece = "";
        char startCol = (char)(move.getInitialCol() + 'a');
        String finalRow = String.valueOf(move.getFinalRow() + 1);
        char finalCol = (char)(move.getFinalCol() + 'a');

        switch (move.getTypeOfPiece()) {
            case KING -> piece = (code == 1 ? "♔" : "K");
            case QUEEN -> piece = (code == 1 ? "♕" : "Q");
            case ROOK -> piece = (code == 1 ? "♖" : "R");
            case BISHOP -> piece = (code == 1 ? "♗" : "B");
            case KNIGHT -> piece = (code == 1 ? "♘" : "N");
            case PAWN -> piece += (code == 1 ? "♙" : startCol);
            default -> piece = "Piece not associated with a type?";
        }

        if (move.getTypeOfPiece() == Piece.PieceType.PAWN) {
            if (move.getMoveType() == Move.MoveType.EN_PASSANT) {
                return startCol + "x" + finalCol + finalRow;
            }
            if (move.getMoveType() == Move.MoveType.CAPTURE) {
                piece = startCol + "x" + finalCol + finalRow;
            } else { // move
                piece = startCol+ finalRow;
            }

            switch (move.getMoveType()) {
                case PROMOTION_QUEEN -> piece += "=Q";
                case PROMOTION_ROOK -> piece += "=R";
                case PROMOTION_BISHOP -> piece += "=B";
                case PROMOTION_KNIGHT -> piece += "=N";
            }

            return piece;
        }

        // capture
        if (move.getMoveType() == Move.MoveType.CAPTURE) {
            return piece + "x" + finalCol + finalRow;
        }

        // long castle
        if (move.getMoveType() == Move.MoveType.CASTLE && move.getInitialCol() == 0) {
            return "O-O-O";
        }

        // short castle
        if (move.getMoveType() == Move.MoveType.CASTLE && move.getInitialCol()== 7) {
            return "O-O";
        }

        return piece + finalCol + finalRow;
    }

    public static Move algebraicToMove(String notation) {
        return null;
    }

    public String gameToFen(Game game) {
        return game.getFen();
    }

    public static ArrayList<Move> pgnToMoveList() {
        ArrayList<Move> moves = new ArrayList<>();

        return moves;
    }
}
