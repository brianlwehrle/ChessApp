package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.Pawn;
import com.brianwehrle.chess.models.pieces.Piece;

import java.util.ArrayList;
import java.util.Scanner;

public class Game {

    private Chessboard board;
    private Player whitePlayer, blackPlayer, activePlayer;
    private ArrayList<Move> prevMoves;

    public Game(Player white, Player black) {
        this.whitePlayer = white;
        this.blackPlayer = black;
        whitePlayer.setColor(Color.WHITE);
        blackPlayer.setColor(Color.BLACK);

        activePlayer = whitePlayer;

        board = new Chessboard();
        prevMoves = new ArrayList<>();
    }

    public void run() {

        while (!isCheckmate()) {
            System.out.println(board);

            ArrayList<Move> legalMoves = getPossibleMoves(board, activePlayer);

            System.out.println(activePlayer.getColor() + " to move: ");
            for (int i = 0; i < legalMoves.size(); i++) {
                System.out.println(i+1 + ": " + legalMoves.get(i));
            }

            Scanner scanner = new Scanner(System.in);
            String choice = scanner.next();

            board.movePiece(legalMoves.get(Integer.valueOf(choice) - 1));
            prevMoves.add(legalMoves.get(Integer.valueOf(choice) - 1));

            activePlayer = (activePlayer == whitePlayer ? blackPlayer : whitePlayer);
        }
    }

    private ArrayList<Move> getPossibleMoves(Chessboard board, Player activePlayer) {
        ArrayList<Move> moves = new ArrayList<>();
        ArrayList<Piece> curPieces =  (activePlayer.getColor() == Color.WHITE) ? board.getWhitePieces() : board.getBlackPieces();

        for (Piece piece : curPieces) {
            moves.addAll(getPossiblePieceMoves(board, piece));
        }

        return moves;
    }

    private ArrayList<Move> getPossiblePieceMoves(Chessboard board, Piece piece) {
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
                    if (nextSquare != null && nextSquare.getPiece() != null) {
                        moves.add(new Move(start, nextSquare));
                    }
                }

            }
            case BISHOP, ROOK, QUEEN -> {
                Square start = piece.getCurSquare();

                for (Pair direction : piece.getDirections()) {
                    Square nextSquare = board.getSquareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

                    for (int scalar = 2; nextSquare != null; scalar++) {
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

    private boolean isCheckmate() {
        return false;
    }
}
