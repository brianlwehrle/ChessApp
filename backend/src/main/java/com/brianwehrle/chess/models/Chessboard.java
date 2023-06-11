package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.*;

import java.util.ArrayList;
import java.util.Optional;

public class Chessboard {
    private static int NUM_ROWS, NUM_COLS;

    // represents a 2d array
    // rows and cols grow down and right
    private final Square[] board;
    private final ArrayList<Piece> pieces;

    public Chessboard() {
        NUM_COLS = NUM_ROWS = 8;

        board = new Square[NUM_COLS * NUM_ROWS];
        pieces = new ArrayList<>();

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                board[row * NUM_COLS + col] = new Square(row, col);
            }
        }

        initialSetup();
    }

    //load position
    public Chessboard(ArrayList<Move> moveList) {
        NUM_COLS = NUM_ROWS = 8;

        board = new Square[NUM_COLS * NUM_ROWS];
        pieces = new ArrayList<>();

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                board[row * NUM_COLS + col] = new Square(row, col);
            }
        }

        initialSetup();

        for (Move move : moveList) {
            move(move);
        }
    }

    public void move(Move move) {
        switch (move.moveType()) {
            case STANDARD, DOUBLE -> movePiece(squareAt(move.getInitialRow(), move.getInitialCol()), squareAt(move.getFinalRow(), move.getFinalCol()));
            case CASTLE -> castle(move);
            case EN_PASSANT -> enPassant(move);
            case PROMOTION -> promote(move);
            default -> {
                System.out.println("Invalid move");
                System.exit(1);
            }
        }
    }

    private void promote(Move move) {
        Square start = squareAt(move.getInitialRow(), move.getInitialCol());
        Square end = squareAt(move.getFinalRow(), move.getFinalCol());

        movePiece(start, end);
        pieces.remove(end.getPiece().get());

        Color color = move.getColor();
        Piece newPiece;

        switch (move.getPromotionType()) {
            case BISHOP -> newPiece = new Bishop(color);
            case ROOK -> newPiece = new Rook(color);
            case KNIGHT -> newPiece = new Knight(color);
            case QUEEN -> newPiece = new Queen(color);
            default -> throw new RuntimeException("No promotion type specified."); //TODO make some exceptions
        }

        pieces.add(newPiece);
        setPiece(end, newPiece);
    }

    private void enPassant(Move move) {
        Square start = squareAt(move.getInitialRow(), move.getInitialCol());
        Square end = squareAt(move.getFinalRow(), move.getFinalCol());

        movePiece(start, end);

        setPiece(move.getCapturedPiece().get().square(), null);
        pieces.remove(move.getCapturedPiece().get());
    }

    private void movePiece(Square start, Square end) {
        if (!end.isEmpty())
            pieces.remove(end.getPiece().get());

        setPiece(end, start.getPiece().get());
        setPiece(start, null);
    }

    public Square getKingLoc(Color color) {
        Piece king = pieces.stream()
                .filter(piece -> piece.getType() == Piece.PieceType.KING)
                .filter(piece -> piece.getColor() == color)
                .findFirst()
                .orElse(null);

        if (king != null) {
            return king.square();
        } else {
            System.err.println("Error, couldn't find King!!");
            return null;
        }
    }

    public Square squareAt(int row, int col) {
        if (row > 7 || col > 7 || row < 0 || col < 0) return null;

        return board[row * NUM_COLS + col];
    }

    public Optional<Piece> pieceAt(int row, int col) {
        return squareAt(row, col).getPiece();
    }

    public Piece pieceAt(Square square) {
        return pieceAt(square.getRow(), square.getCol()).get();
    }

    public String toString() {

        StringBuilder line = new StringBuilder();
        StringBuilder res = new StringBuilder();

        for (int i = board.length - 1; i >= 0; i--) {
            line.insert(0, "|" + board[i].toString(0));

            if (i % NUM_ROWS == 0) {
                line.append("|\n");
                res.append(line);
                line = new StringBuilder();
            }
        }

        return res.toString();
    }

    public String toString(ArrayList<Square> threatMap) {

        StringBuilder line = new StringBuilder();
        StringBuilder res = new StringBuilder();
        String output;

        for (int i = board.length - 1; i >= 0; i--) {
            if (threatMap.contains(board[i])) {
                output = "X";
            } else {
                output = "_";
            }
            line.insert(0, "|" + output);

            if (i % NUM_ROWS == 0) {
                line.append("|\n");
                res.append(line);
                line = new StringBuilder();
            }
        }

        return res.toString();
    }

    private void setPiece(Square square, Piece piece) {
        if (piece != null) {
            piece.setSquare(square);
            square.setPiece(piece);
        } else {
            square.setPiece(null);
        }
    }

    private void castle(Move move) {
        // move the rook
        Square start = squareAt(move.getInitialRow(), move.getInitialCol());
        Square end = squareAt(move.getFinalRow(), move.getFinalCol());

        movePiece(start, end);

        // move king
        // white long
        if (start == squareAt(0, 0))
            movePiece(squareAt(0, 4), squareAt(0, 2));
        // white short
        if (start == squareAt(0, 7))
            movePiece(squareAt(0, 4), squareAt(0, 6));
        // black long
        if (start == squareAt(7, 0))
            movePiece(squareAt(7, 4), squareAt(7, 2));
        // black short
        if (start == squareAt(7, 7))
            movePiece(squareAt(7, 4), squareAt(7, 6));
    }

    private void initialSetup() {
        // Pawns
        for (int i = 0; i < 8; i++) {
            setPiece(squareAt(1, i), new Pawn(Color.WHITE));
            setPiece(squareAt(6, i), new Pawn(Color.BLACK));

            pieces.add(pieceAt(1, i).get());
            pieces.add(pieceAt(6, i).get());
        }

        setUpKingRow(0, Color.WHITE);
        setUpKingRow(7, Color.BLACK);
    }

    private void setUpKingRow(int row, Color color) {
        setPiece(squareAt(row, 0), new Rook(color));
        setPiece(squareAt(row, 1), new Knight(color));
        setPiece(squareAt(row, 2), new Bishop(color));
        setPiece(squareAt(row, 3), new Queen(color));
        setPiece(squareAt(row, 4), new King(color));
        setPiece(squareAt(row, 5), new Bishop(color));
        setPiece(squareAt(row, 6), new Knight(color));
        setPiece(squareAt(row, 7), new Rook(color));

        for (int i = 0; i < 8; i++) {
            pieces.add(pieceAt(row, i).get());
        }
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    public ArrayList<Piece> getPieces(Color color) {
        ArrayList<Piece> colorPieces = new ArrayList<>();

        for (Piece piece : pieces) {
            if (piece.getColor() == color) colorPieces.add(piece);
        }

        return colorPieces;
    }

    public Square[] getBoard() {
        return board;
    }
}
