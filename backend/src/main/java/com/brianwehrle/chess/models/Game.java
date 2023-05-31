package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.Piece;
import com.brianwehrle.chess.models.pieces.PieceType;

import java.util.ArrayList;
import java.util.Scanner;

public class Game {

    private final Chessboard board;
    private Player whitePlayer, blackPlayer, activePlayer;
    private ArrayList<Move> prevMoves;
    private ArrayList<Square> threatMap; // list of squares currently under attack


    public Game(Player white, Player black) {
        this.whitePlayer = white;
        this.blackPlayer = black;
        whitePlayer.setColor(Color.WHITE);
        blackPlayer.setColor(Color.BLACK);

        activePlayer = whitePlayer;

        board = new Chessboard();
        prevMoves = new ArrayList<>();
        threatMap = new ArrayList<>();
    }

    public void run() {

        while (true) {
            System.out.println(board);
            System.out.println(board.toString(threatMap));

            ArrayList<Move> possibleMoves = getPossibleMoves(activePlayer, false);
            removeIllegalMoves(possibleMoves);

            // Game over!
            if (possibleMoves.isEmpty()) {
                System.out.println(activePlayer.getColor() + " loses!");
                break;
            }

            System.out.println(activePlayer.getColor() + " to move: ");
            for (int i = 0; i < possibleMoves.size(); i++) {
                System.out.println(i+1 + ": " + possibleMoves.get(i));
            }

            Scanner scanner = new Scanner(System.in);
            String nextMove = scanner.next();

            board.makeMove(possibleMoves.get(Integer.valueOf(nextMove) - 1));
            prevMoves.add(possibleMoves.get(Integer.valueOf(nextMove) - 1));

            threatMap.clear(); // get rid of old threat map
            getPossibleMoves(activePlayer, true); // has only the effect of creating a new threat map.

            activePlayer = (activePlayer == whitePlayer ? blackPlayer : whitePlayer);
        }
    }

    private void removeIllegalMoves(ArrayList<Move> possibleMoves) {
        // king cannot go into check
        possibleMoves.removeIf(move -> move.start().getPiece().getType() == PieceType.KING && isUnderAttack(move.end()));

        // simulate each move and see if my king would be in check
        for (Move move : possibleMoves) {
            Chessboard tempBoard = board;
            ArrayList<Square> tempThreatMap = threatMap;

            tempBoard.makeMove(move);

        }
    }

    private ArrayList<Move> getPossibleMoves(Player activePlayer, boolean newThreatMap) {
        ArrayList<Move> moves = new ArrayList<>();
        ArrayList<Piece> curPieces =  (activePlayer.getColor() == Color.WHITE) ? board.getWhitePieces() : board.getBlackPieces();

        for (Piece piece : curPieces) {
            moves.addAll(getPossiblePieceMoves(piece, newThreatMap));
        }

        return moves;
    }

    private ArrayList<Move> getPossiblePieceMoves(Piece piece, boolean newThreatMap) {
        ArrayList<Move> moves = new ArrayList<>();

        switch (piece.getType()) {
            case KNIGHT, KING -> {
                Square start = piece.getCurSquare();

                for (Pair direction : piece.getDirections()) {
                    Square nextSquare = board.getSquareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

                    if (nextSquare != null) { // is in bounds
                        if (nextSquare.getPiece() != null) { // if the next square contains a piece
                            if (nextSquare.getPiece().getColor() != piece.getColor()) { // different color, add move to list
                                moves.add(new Move(start, nextSquare));
                            }
                        } else {
                            moves.add(new Move(start, nextSquare));
                        }

                        if (newThreatMap) threatMap.add(nextSquare);
                    }
                }
            }
            case PAWN -> {
                Square start = piece.getCurSquare();
                int dy = piece.getDirections().get(0).dy();
                Square nextSquare = board.getSquareAt(start.getRow() + dy, start.getCol());

                assert(nextSquare != null);

                // moving 1 space
                if (nextSquare.getPiece() == null) {
                    moves.add(new Move(start, nextSquare));
                }

                // moving 2 spaces
                nextSquare = board.getSquareAt(start.getRow() + dy * 2, start.getCol());
                if (!piece.hasMoved() && nextSquare.getPiece() == null) {
                    moves.add(new Move(start, nextSquare));
                }

                //attacking
                for (int dx = -1; dx <= 1; dx += 2) { // just checks both forward diagonals
                    nextSquare = board.getSquareAt(start.getRow() + dy, start.getCol() + dx);
                    if (nextSquare != null) {
                        if (newThreatMap) threatMap.add(nextSquare);
                        if (nextSquare.getPiece() != null) {
                            moves.add(new Move(start, nextSquare));
                        }
                    }
                }

            }
            case BISHOP, ROOK, QUEEN -> {
                Square start = piece.getCurSquare();

                // for each direction build a path of moves until you hit another piece
                for (Pair direction : piece.getDirections()) {
                    Square nextSquare = board.getSquareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

                    for (int scalar = 2; nextSquare != null; scalar++) {
                        if (newThreatMap) threatMap.add(nextSquare);

                        if (nextSquare.getPiece() == null) {
                            moves.add(new Move(start, nextSquare));
                        } else if (nextSquare.getPiece().getColor() != piece.getColor()) {
                            moves.add(new Move(start, nextSquare));
                            break;
                        } else {
                            break;
                        }

                        nextSquare = board.getSquareAt(start.getRow() + direction.dy() * scalar, start.getCol() + direction.dx() * scalar);
                    }
                }
            }
        }

        return moves;
    }

    private boolean isUnderAttack(Square square) {
        return threatMap.contains(square);
    }
}
