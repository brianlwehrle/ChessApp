package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.Piece;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Game {

    private Chessboard board;
    //private final Player whitePlayer, blackPlayer;
    private Color currentColor;
    private final ArrayList<Move> prevMoves;


    public Game() {
        board = new Chessboard();
        currentColor = Color.WHITE;
        prevMoves = new ArrayList<>();
    }

    // Load Game
//    public Game(ArrayList<Move> moveList) {
//        board = new Chessboard(moveList);
//        currentColor = Color.WHITE;
//        prevMoves = moveList;
//    }

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
            System.out.println(i + ") " + possibleMoves.get(i - 1).toString(0) + " ");
            //if (i == possibleMoves.size() / 2) System.out.println();
        }

        Move move = possibleMoves.get(Integer.parseInt(scanner.next()) - 1);

        board.move(move);
        prevMoves.add(move);
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

        for (Piece piece : board.getPieces(color)) {
            Square start = piece.square();
            switch (piece.getType()) {
                case KING -> addKingMoves(piece, start, moves);
                case PAWN -> addPawnMoves(piece, start, moves);
                case QUEEN, ROOK, BISHOP, KNIGHT -> addQRKBMoves(piece, start, moves);
            }
        }

        return moves;
    }

    private void addKingMoves(Piece piece, Square start, ArrayList<Move> moves) {
            for (Direction direction : piece.getDirections()) {
                Square nextSquare = board.squareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

                if (nextSquare != null) { // is in bounds
                    if (nextSquare.isEmpty() || piece.differentColor(nextSquare.getPiece().get()))
                        moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare, currentColor));
                }
            }
    }

    private void addQRKBMoves(Piece piece, Square start, ArrayList<Move> moves) {
        // for each direction build a path of moves until you hit another piece
        for (Direction direction : piece.getDirections()) {
            Square nextSquare = board.squareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

            for (int scalar = 2; nextSquare != null; scalar++) {
                if (nextSquare.isEmpty()) {
                    moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare, currentColor));
                } else if (piece.differentColor(nextSquare.getPiece().get())) {
                    moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare, currentColor));
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
            moves.add(new Move(piece.getType(), Move.MoveType.CASTLE, piece.square(), finalSquare, currentColor));
    }

    private void addPawnMoves(Piece piece, Square start, ArrayList<Move> moves) {
        // moving
        for (Direction direction : piece.getDirections()) {
            int dy = direction.dy();
            Square nextSquare = board.squareAt(start.getRow() + dy, start.getCol());
            if (nextSquare != null && nextSquare.isEmpty()) {
                if (Math.abs(dy) == 2) {
                    moves.add(new Move(piece.getType(), Move.MoveType.DOUBLE, start, nextSquare, currentColor));
                } else {
                    if (nextSquare.getRow() == 0 || nextSquare.getRow() == 7) { // promotion
                        addPromotion(moves, start, nextSquare);
                    } else {
                        moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare, currentColor));
                    }
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
                if (!nextSquare.isEmpty() && piece.differentColor(nextSquare.getPiece().get())) {
                    if (nextSquare.getRow() == 0 || nextSquare.getRow() == 7) {
                        addPromotion(moves, start, nextSquare); // promotion
                    } else {
                        moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare, currentColor));
                    }
                }
            }
        }

        // en passant
        Square endSquare = canEnPassant(piece);
        if (endSquare != null) {
            Piece capturedPawn = board.pieceAt(board.squareAt(endSquare.getRow() - dy, endSquare.getCol()));
            moves.add(new Move(piece.getType(), Move.MoveType.EN_PASSANT, start, endSquare, capturedPawn, currentColor));
        }
    }

    private void addPromotion(ArrayList<Move> moves, Square start, Square nextSquare) {
        moves.add(new Move(Piece.PieceType.PAWN, Move.MoveType.PROMOTION, Piece.PieceType.KNIGHT, start, nextSquare, currentColor));
        moves.add(new Move(Piece.PieceType.PAWN, Move.MoveType.PROMOTION, Piece.PieceType.BISHOP, start, nextSquare, currentColor));
        moves.add(new Move(Piece.PieceType.PAWN, Move.MoveType.PROMOTION, Piece.PieceType.ROOK, start, nextSquare, currentColor));
        moves.add(new Move(Piece.PieceType.PAWN, Move.MoveType.PROMOTION, Piece.PieceType.QUEEN, start, nextSquare, currentColor));
    }

    // any moves that allow your king to be in check
    private void removeIllegalMoves(ArrayList<Move> possibleMoves) {
        // cannot move if my king would be in check
        // simulate each move and see if my king would be in check
        Chessboard tempBoard = board;
        for (Iterator<Move> iterator = possibleMoves.iterator(); iterator.hasNext(); ) {
            Move move = iterator.next();
            board = new Chessboard(prevMoves);
            board.move(move);
            if (isInCheck(currentColor)) {
                iterator.remove();
            }
        }
        board = tempBoard;
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

        if (prev.getMoveType() == Move.MoveType.DOUBLE &&
            areAdjacent(pawn, board.pieceAt(prev.getFinalRow(), prev.getFinalCol()).get())) {

            int dy = (prev.getColor() == Color.WHITE ? -1 : 1);
            return board.squareAt(prev.getFinalRow() + dy, prev.getFinalCol());
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
