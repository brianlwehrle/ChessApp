package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.Piece;
import com.brianwehrle.chess.models.pieces.PieceType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Game {

    private final Chessboard board;
    private final Player whitePlayer, blackPlayer;
    private Color currentPlayer;
    private final ArrayList<Move> prevMoves;
    private ArrayList<Square> threatMap;


    public Game(Player white, Player black) {
        board = new Chessboard();

        this.whitePlayer = white;
        this.blackPlayer = black;
        whitePlayer.setColor(Color.WHITE);
        blackPlayer.setColor(Color.BLACK);
        currentPlayer = Color.WHITE;

        prevMoves = new ArrayList<>();
    }

    public void run() {
        ArrayList<Move> possibleMoves;

        // Game loop
        while (true) {
            System.out.println(board);

            possibleMoves = getThreatsAndPossibleMoves(currentPlayer);
            removeIllegalMoves(possibleMoves);

            if (gameOver(possibleMoves)) return;

            getAndMakeMove(possibleMoves);

            currentPlayer = (currentPlayer == Color.WHITE ? Color.BLACK : Color.WHITE);
        }
    }

    private void getAndMakeMove(ArrayList<Move> possibleMoves) {
        Scanner scanner = new Scanner(System.in);

        System.out.println(currentPlayer + " to move: ");
        for (int i = 1; i <= possibleMoves.size(); i++) {
            System.out.print(i + ":" + possibleMoves.get(i-1) + " ");
            if (i % 5 == 0) System.out.println();
        }

        int nextMove = Integer.valueOf(scanner.next()) - 1;

        board.movePiece(possibleMoves.get(nextMove));
        prevMoves.add(possibleMoves.get(nextMove));
    }

    // each call makes a new threat map for currentPlayer
    private ArrayList<Move> getThreatsAndPossibleMoves(Color color) {
        ArrayList<Move> moves = new ArrayList<>();
        threatMap = new ArrayList<>();

        ArrayList<Piece> curPieces = (color == Color.WHITE ? board.getWhitePieces() : board.getBlackPieces());

        for (Piece piece : curPieces) {
            Square start = piece.getCurSquare();

            switch (piece.getType()) {
                case KING -> {
                    for (Direction direction : piece.getDirections()) {
                        Square nextSquare = board.squareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

                        if (nextSquare != null) { // is in bounds
                            if (nextSquare.getPiece() == null || !piece.sameColor(nextSquare.getPiece()))
                                moves.add(new Move(start, nextSquare));
                            threatMap.add(nextSquare);
                        }
                    }

                    moves.addAll(castle(color));
                }

                case PAWN -> {
                    //moving
                    for (Direction direction : piece.getDirections()) {
                        int dy = direction.dy();
                        Square nextSquare = board.squareAt(start.getRow() + dy, start.getCol());
                        if (nextSquare.getPiece() == null)
                            moves.add(new Move(start, nextSquare));
                    }

                    //attacking
                    int dy = piece.getDirections().get(0).dy();
                    for (int dx = -1; dx <= 1; dx += 2) { // just checks both forward diagonals
                        Square nextSquare = board.squareAt(start.getRow() + dy, start.getCol() + dx);
                        if (nextSquare != null) {
                            threatMap.add(nextSquare);
                            if (nextSquare.getPiece() != null) {
                                moves.add(new Move(start, nextSquare));
                            }
                        }
                    }

                }

                case QUEEN, ROOK, BISHOP, KNIGHT -> {
                    // for each direction build a path of moves until you hit another piece
                    for (Direction direction : piece.getDirections()) {
                        Square nextSquare = board.squareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

                        for (int scalar = 2; nextSquare != null; scalar++) {
                            threatMap.add(nextSquare);

                            if (nextSquare.getPiece() == null) {
                                moves.add(new Move(start, nextSquare));
                            } else if (!piece.sameColor(nextSquare.getPiece())) {
                                moves.add(new Move(start, nextSquare));
                                break;
                            } else {
                                break;
                            }

                            // knights have no scalars
                            if (piece.getType() == PieceType.KNIGHT) break;

                            nextSquare = board.squareAt(start.getRow() + direction.dy() * scalar, start.getCol() + direction.dx() * scalar);
                        }
                    }
                }
            }
        }
        return moves;
    }

    // basically moves that allow your king to be in check
    private void removeIllegalMoves(ArrayList<Move> possibleMoves) {
        // cannot move if my king would be in check
        // simulate each move and see if my king would be in check
        Color otherColor = (currentPlayer == Color.WHITE ? Color.BLACK : Color.WHITE);
        for (Iterator<Move> iterator = possibleMoves.iterator(); iterator.hasNext(); ) {
            Move move = iterator.next();

            board.movePiece(move);
            getThreatsAndPossibleMoves(otherColor); // has only the effect of creating a new threat map.

            if (isInCheck(currentPlayer)) {
               iterator.remove();
            }

            board.undoMove(move);
        }

    }

    private boolean isUnderAttack(Square square) {
        return threatMap.contains(square);
    }

    private boolean isInCheck(Color color) {
        return isUnderAttack(board.findKing(color));
    }

    private ArrayList<Move> castle(Color color) {
        ArrayList<Move> moves = new ArrayList<>();
        int row = (color == Color.WHITE ? 0 : 7);

        if (!board.findKing(color).getPiece().hasMoved()) {
            moves.addAll(kingSideCastle(row));
        }

        return moves;
    }

    private ArrayList<Move> kingSideCastle(int row) {
        ArrayList<Move> moves = new ArrayList<>();

        Piece rook = board.pieceAt(row, 7);
        if (rook == null || rook.hasMoved() || rook.getType() != PieceType.ROOK) return moves;
        if (board.squareAt(row, 6).getPiece() != null && board.squareAt(row, 5).getPiece() != null) return moves;

        for (int col = 6; col <= 7; col++) {
            Square curSquare = board.squareAt(row, col);
            if (curSquare.getPiece() != null || isUnderAttack(curSquare)) return moves;
        }

        // all clear to castle
        moves.add(new Move(board.squareAt(row, 4), board.squareAt(row, 6))); // move king
        moves.add(new Move(board.squareAt(row, 7), board.squareAt(row, 5))); // move rook

        return moves;
    }

    private boolean gameOver(ArrayList<Move> legalMoves) {
        if (legalMoves.isEmpty()) {
            if (isInCheck(currentPlayer)) {
                System.out.println(currentPlayer + " loses!");
            } else {
                System.out.println("Stalemate!");
            }
            return true;
        }
        return false;
    }
}
