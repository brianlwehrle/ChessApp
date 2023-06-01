package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.Piece;
import com.brianwehrle.chess.models.pieces.PieceType;

import java.util.ArrayList;
import java.util.Iterator;
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

            ArrayList<Move> possibleMoves = getPossibleMoves(activePlayer, null);
            removeIllegalMoves(possibleMoves);

            // Game over!
            if (possibleMoves.isEmpty()) {
                if (isCheck(activePlayer.getColor(), threatMap)) System.out.println(activePlayer.getColor() + " loses!");
                else System.out.println("Stalemate!");
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
            getPossibleMoves(activePlayer, threatMap); // has only the effect of creating a new threat map.

            activePlayer = (activePlayer == whitePlayer ? blackPlayer : whitePlayer);
        }
    }

    private ArrayList<Move> getPossibleMoves(Player activePlayer, ArrayList<Square> threatMap) {
        ArrayList<Move> moves = new ArrayList<>();
        ArrayList<Piece> curPieces =  (activePlayer.getColor() == Color.WHITE) ? board.getWhitePieces() : board.getBlackPieces();

        for (Piece piece : curPieces) {
            moves.addAll(getEachPieceMoves(piece, threatMap));
        }

        return moves;
    }

    private ArrayList<Move> getEachPieceMoves(Piece piece, ArrayList<Square> threatMap) {
        ArrayList<Move> moves = new ArrayList<>();

        switch (piece.getType()) {
            case KNIGHT, KING -> {
                Square start = piece.getCurSquare();

                for (Direction direction : piece.getDirections()) {
                    Square nextSquare = board.getSquareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

                    if (nextSquare != null) { // is in bounds
                        if (nextSquare.getPiece() != null) { // if the next square contains a piece
                            if (nextSquare.getPiece().getColor() != piece.getColor()) { // different color, add move to list
                                moves.add(new Move(start, nextSquare));
                            }
                        } else {
                            moves.add(new Move(start, nextSquare));
                        }

                        if (threatMap != null) threatMap.add(nextSquare);
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
                        if (threatMap != null) threatMap.add(nextSquare);
                        if (nextSquare.getPiece() != null) {
                            moves.add(new Move(start, nextSquare));
                        }
                    }
                }

            }
            case BISHOP, ROOK, QUEEN -> {
                Square start = piece.getCurSquare();

                // for each direction build a path of moves until you hit another piece
                for (Direction direction : piece.getDirections()) {
                    Square nextSquare = board.getSquareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

                    for (int scalar = 2; nextSquare != null; scalar++) {
                        if (threatMap != null) threatMap.add(nextSquare);

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

    private void removeIllegalMoves(ArrayList<Move> possibleMoves) {
        // king cannot move into check
        possibleMoves.removeIf(move -> move.start().getPiece().getType() == PieceType.KING && isUnderAttack(move.end(), threatMap));

        // cannot move if my king would be in check
        // simulate each move and see if my king would be in check
        for (Iterator<Move> iterator = possibleMoves.iterator(); iterator.hasNext(); ) {
            Move move = iterator.next();

            board.makeMove(move);
            ArrayList<Square> tempThreatMap = new ArrayList<>();
            Player otherPlayer = (activePlayer == whitePlayer ? blackPlayer : whitePlayer);
            getPossibleMoves(otherPlayer, tempThreatMap); // has only the effect of creating a new threat map.

            if (isCheck(activePlayer.getColor(), tempThreatMap)) {
               iterator.remove();
            }

            board.undoMove(move);
        }
    }

    private boolean isUnderAttack(Square square, ArrayList<Square> threatMap) {
        return threatMap.contains(square);
    }

    private boolean isCheck(Color color, ArrayList<Square> tempThreatMap) {
        assert(board.getKing(color).getType() == PieceType.KING);

        return isUnderAttack(board.getKing(color).getCurSquare(), tempThreatMap);
    }
}
