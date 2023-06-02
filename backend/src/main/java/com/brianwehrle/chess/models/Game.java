package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.Piece;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Game {

    private final Chessboard board;
    private final Player whitePlayer, blackPlayer;
    private Color currentColor;
    private final ArrayList<Move> prevMoves;


    public Game(Player white, Player black) {
        board = new Chessboard();

        this.whitePlayer = white;
        this.blackPlayer = black;
        whitePlayer.setColor(Color.WHITE);
        blackPlayer.setColor(Color.BLACK);
        currentColor = Color.WHITE;

        prevMoves = new ArrayList<>();
    }

    public void run() {
        ArrayList<Move> possibleMoves;

        // Game loop
        while (true) {
            System.out.println(board);

            possibleMoves = getPossibleMoves(currentColor);
            removeIllegalMoves(possibleMoves);

            if (gameOver(possibleMoves)) return;

            getAndMakeMove(possibleMoves);

            currentColor = (currentColor == Color.WHITE ? Color.BLACK : Color.WHITE);
        }
    }

    private void getAndMakeMove(ArrayList<Move> possibleMoves) {
        Scanner scanner = new Scanner(System.in);

        System.out.println(currentColor + " to move: ");
        for (int i = 1; i <= possibleMoves.size(); i++) {
            System.out.println(i + ") " + possibleMoves.get(i-1) + " ");
            //if (i == possibleMoves.size() / 2) System.out.println();
        }

        int nextMove = Integer.parseInt(scanner.next()) - 1;

        board.move(possibleMoves.get(nextMove));
        prevMoves.add(possibleMoves.get(nextMove));
    }

    private ArrayList<Square> getThreatMap(Color color ) {
        ArrayList<Square> threatMap = new ArrayList<>();

        ArrayList<Piece> curPieces = board.getPieces(color);

        for (Piece piece : curPieces) {
            Square start = piece.square();

            switch (piece.getType()) {
                case KING -> {
                    for (Direction direction : piece.getDirections()) {
                        Square nextSquare = board.squareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

                        if (nextSquare != null) { // is in bounds
                            threatMap.add(nextSquare);
                        }
                    }
                }

                case PAWN -> {
                    // en passant threats aren't added because they can never be used to check a king

                    //attacking
                    int dy = piece.getDirections().get(0).dy();
                    for (int dx = -1; dx <= 1; dx += 2) { // just checks both forward diagonals
                        Square nextSquare = board.squareAt(start.getRow() + dy, start.getCol() + dx);
                        if (nextSquare != null) {
                            threatMap.add(nextSquare);
                        }
                    }
                }

                case QUEEN, ROOK, BISHOP, KNIGHT -> {
                    // for each direction build a path of moves until you hit another piece
                    for (Direction direction : piece.getDirections()) {
                        Square nextSquare = board.squareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

                        for (int scalar = 2; nextSquare != null; scalar++) {
                            threatMap.add(nextSquare);

                            if (nextSquare.getPiece().isPresent()) break;

                            // knights have no scalars
                            if (piece.getType() == Piece.PieceType.KNIGHT) break;

                            nextSquare = board.squareAt(start.getRow() + direction.dy() * scalar, start.getCol() + direction.dx() * scalar);
                        }
                    }
                }
            }
        }
        return threatMap;
    }

    private ArrayList<Move> getPossibleMoves(Color color) {
        ArrayList<Move> moves = new ArrayList<>();

        ArrayList<Piece> curPieces = board.getPieces(color);

        for (Piece piece : curPieces) {
            Square start = piece.square();

            switch (piece.getType()) {
                case KING -> {
                    for (Direction direction : piece.getDirections()) {
                        Square nextSquare = board.squareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

                        if (nextSquare != null) { // is in bounds
                            if (nextSquare.isEmpty() || piece.differentColorThan(nextSquare.getPiece().get()))
                                moves.add(new Move(Move.MoveType.STANDARD, start, nextSquare));
                        }
                    }
                }

                case PAWN -> {
                    //moving
                    for (Direction direction : piece.getDirections()) {
                        int dy = direction.dy();
                        Square nextSquare = board.squareAt(start.getRow() + dy, start.getCol());
                        if (nextSquare.isEmpty()) {
                            if (Math.abs(dy) == 2) {
                                moves.add(new Move(Move.MoveType.DOUBLE, start, nextSquare));
                            } else {
                                moves.add(new Move(Move.MoveType.STANDARD, start, nextSquare));
                            }
                        } else {
                            break;
                        }
                    }

                    //attacking
                    int dy = piece.getDirections().get(0).dy();
                    for (int dx = -1; dx <= 1; dx += 2) { // just checks both forward diagonals
                        Square nextSquare = board.squareAt(start.getRow() + dy, start.getCol() + dx);
                        if (nextSquare != null) {
                            if (!nextSquare.isEmpty() && piece.differentColorThan(nextSquare.getPiece().get()))
                                moves.add(new Move(Move.MoveType.STANDARD, start, nextSquare));
                        }
                    }

                    // en passant
                    Square endSquare = canEnPassant(piece);
                    if (endSquare != null) {
                        Piece capturedPawn = board.pieceAt(board.squareAt(endSquare.getRow() - dy, endSquare.getCol()));
                        moves.add(new Move(Move.MoveType.EN_PASSANT, start, endSquare, capturedPawn));
                    }
                }

                case QUEEN, ROOK, BISHOP, KNIGHT -> {
                    // for each direction build a path of moves until you hit another piece
                    for (Direction direction : piece.getDirections()) {
                        Square nextSquare = board.squareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

                        for (int scalar = 2; nextSquare != null; scalar++) {
                            if (nextSquare.isEmpty()) {
                                moves.add(new Move(Move.MoveType.STANDARD, start, nextSquare));
                            } else if (piece.differentColorThan(nextSquare.getPiece().get())) {
                                moves.add(new Move(Move.MoveType.STANDARD, start, nextSquare));
                                break;
                            } else {
                                break;
                            }

                            // knights have no scalars
                            if (piece.getType() == Piece.PieceType.KNIGHT) break;

                            nextSquare = board.squareAt(start.getRow() + direction.dy() * scalar, start.getCol() + direction.dx() * scalar);
                        }
                    }
                    //castling
                    Square finalSquare = canCastle(piece);
                    if (piece.getType() == Piece.PieceType.ROOK && finalSquare != null)
                        moves.add(new Move(Move.MoveType.CASTLE, piece.square(), finalSquare));
                }
            }
        }
        return moves;
    }

    // basically moves that allow your king to be in check
    private void removeIllegalMoves(ArrayList<Move> possibleMoves) {
        // cannot move if my king would be in check
        // simulate each move and see if my king would be in check
        for (Iterator<Move> iterator = possibleMoves.iterator(); iterator.hasNext(); ) {
            Move move = iterator.next();
            // TODO: see if this is necessary
            if (move.getType() == Move.MoveType.CASTLE) continue;
            board.move(move);
            if (isInCheck(currentColor)) {
               iterator.remove();
            }
            board.undoMove(move);
        }
    }

    private boolean isUnderAttack(Color color, Square square) {
        // check threats of other color
        return getThreatMap(color == Color.WHITE ? Color.BLACK : Color.WHITE).contains(square);
    }

    private boolean isInCheck(Color color) {
        return isUnderAttack(color, board.getKingLoc(color));
    }

    // returns the final square of the rook if castling is possible, null otherwise
    private Square canCastle(Piece rook) {
        Color color = rook.getColor();

        Piece king = board.getKingLoc(color).getPiece().get();
        if (king.hasMoved() || rook.hasMoved()) return null;

        // white long
        if (rook.square() == board.squareAt(0, 0)) {
            if (board.squareAt(0, 1).isEmpty() && !isUnderAttack(color, board.squareAt(0, 1)) &&
                board.squareAt(0, 2).isEmpty() && !isUnderAttack(color, board.squareAt(0, 2)) &&
                board.squareAt(0, 3).isEmpty() && !isUnderAttack(color, board.squareAt(0, 3))) {

                return board.squareAt(0, 3);
            }
        }
        // white short
        if (rook.square() == board.squareAt(0, 7)) {
            if (board.squareAt(0, 6).isEmpty() && !isUnderAttack(color, board.squareAt(0, 6)) &&
                board.squareAt(0, 5).isEmpty() && !isUnderAttack(color, board.squareAt(0, 5))) {

                return board.squareAt(0, 5);
            }
        }
        // black long
        if (rook.square() == board.squareAt(7, 0)) {
            if (board.squareAt(7, 1).isEmpty() && !isUnderAttack(color, board.squareAt(7, 1)) &&
                board.squareAt(7, 2).isEmpty() && !isUnderAttack(color, board.squareAt(7, 2)) &&
                board.squareAt(7, 3).isEmpty() && !isUnderAttack(color, board.squareAt(7, 3))) {

                return board.squareAt(7, 3);
            }
        }
        // black short
        if (rook.square() == board.squareAt(7, 7)) {
            if (board.squareAt(7, 6).isEmpty() && !isUnderAttack(color, board.squareAt(7, 6)) &&
                board.squareAt(7, 5).isEmpty() && !isUnderAttack(color, board.squareAt(7, 5))) {

                return board.squareAt(7, 5);
            }
        }

        return null;
    }

    // returns the square that the capturing pawn will end on
    private Square canEnPassant(Piece pawn) {
        if (prevMoves.isEmpty()) return null;

        Move prev = prevMoves.get(prevMoves.size() - 1);

        if (prev.getType() == Move.MoveType.DOUBLE &&
            areAdjacent(pawn, prev.getMovingPiece())) {

            int dy = (prev.getMovingPiece().getColor() == Color.WHITE ? -1 : 1);
            return board.squareAt(prev.end().getRow() + dy, prev.end().getCol());
        }

        return null;
    }

    private boolean areAdjacent(Piece p1, Piece p2) {
        return (p1.square().getRow() == p2.square().getRow() &&
                Math.abs(p1.square().getCol() - p2.square().getCol()) == 1);
    }

    private boolean gameOver(ArrayList<Move> legalMoves) {
        if (legalMoves.isEmpty()) {
            if (isInCheck(currentColor)) {
                System.out.println(currentColor == Color.WHITE ? Color.BLACK : Color.WHITE + " wins!");
            } else {
                System.out.println("Stalemate!");
            }
            return true;
        }
        return false;
    }
}
