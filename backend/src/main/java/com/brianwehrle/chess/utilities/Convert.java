package com.brianwehrle.chess.utilities;

import com.brianwehrle.chess.models.Game;
import com.brianwehrle.chess.models.Move;
import com.brianwehrle.chess.models.pieces.Piece;

import java.util.ArrayList;
/*
    Handles various notation conversion functions, such as converting from PGN,
    fen, etc. to Move/List of Moves and vice versa
 */
public class Convert {

    public static String moveToAlgebraic(Move move, int code) {
        String piece = "";
        char startCol = (char)(move.getInitialCol() + 'a');
        String finalRow = String.valueOf(move.getFinalRow() + 1);
        char finalCol = (char)(move.getFinalCol() + 'a');

        switch (move.getTypeOfPiece()) {
            case KING -> piece = (code == 0 ? "♔" : "K");
            case QUEEN -> piece = (code == 0 ? "♕" : "Q");
            case ROOK -> piece = (code == 0 ? "♖" : "R");
            case BISHOP -> piece = (code == 0 ? "♗" : "B");
            case KNIGHT -> piece = (code == 0 ? "♘" : "N");
            case PAWN -> piece += (code == 0 ? "♙" : startCol);
            default -> piece = "Piece not associated with a type?";
        }

        if (move.getTypeOfPiece() == Piece.PieceType.PAWN) {
            if (move.moveType() == Move.MoveType.EN_PASSANT) {
                return startCol + "x" + finalCol + finalRow;
            }
            if (move.moveType() == Move.MoveType.CAPTURE) {
                piece = startCol + "x" + finalCol + finalRow;
            } else { // move
                piece = startCol+ finalRow;
            }
            if (move.getPromotionType() != null) {
                switch (move.getPromotionType()) {
                    case BISHOP -> piece += "=B";
                    case QUEEN -> piece += "=Q";
                    case ROOK -> piece += "=R";
                    case KNIGHT -> piece += "=N";
                }
            }

            return piece;
        }

        // capture
        if (move.moveType() == Move.MoveType.CAPTURE) {
            return piece + "x" + finalCol + finalRow;
        }

        // long castle
        if (move.moveType() == Move.MoveType.CASTLE && move.getInitialCol() == 0) {
            return "O-O-O";
        }

        // short castle
        if (move.moveType() == Move.MoveType.CASTLE && move.getInitialCol()== 7) {
            return "O-O";
        }

        return piece + finalCol + finalRow;
    }

    public static Move algebraicToMove(String notation) {
        return null;
    }

    public static String gameToFen(Game game) {
        return  "";
    }

    public static ArrayList<Move> pgnToMoveList() {
        ArrayList<Move> moves = new ArrayList<>();

        return moves;
    }
}
