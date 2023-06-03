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
    private Optional<Piece> lastCapturedPiece;

    public Chessboard() {
        NUM_COLS = NUM_ROWS = 8;

        board = new Square[NUM_COLS * NUM_ROWS];
        pieces = new ArrayList<>();
        lastCapturedPiece = Optional.empty();

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
        lastCapturedPiece = Optional.empty();

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
        switch (move.getMoveType()) {
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
        Color color = move.getColor();
        Piece piece;

        switch (move.getPromotionType()) {
            case BISHOP -> piece = new Bishop(color);
            case ROOK -> piece = new Rook(color);
            case KNIGHT -> piece = new Knight(color);
            case QUEEN -> piece = new Queen(color);
            default -> throw new RuntimeException("No promotion type specified."); //TODO make some exceptions
        }

        pieces.add(piece);
        pieces.remove(move.getMovingPiece());
        setPieceAt(end, piece);
    }

    private void enPassant(Move move) {
        Square start = squareAt(move.getInitialRow(), move.getInitialCol());
        Square end = squareAt(move.getFinalRow(), move.getFinalCol());

        movePiece(start, end);

        setPieceAt(move.getCapturedPiece().get().square(), null);
        pieces.remove(move.getCapturedPiece().get());
    }

    private void movePiece(Square start, Square end) {
        Piece movingPiece = start.getPiece().get();
        lastCapturedPiece = end.getPiece();

        setPieceAt(start, null);
        movingPiece.setSquare(end);
        setPieceAt(end, movingPiece);

        lastCapturedPiece.ifPresent(pieces::remove);

        if (!movingPiece.hasMoved()) {
            movingPiece.setHasMoved(true);
            movingPiece.setMovedLastTurn(true);
        } else {
            movingPiece.setMovedLastTurn(false);
        }
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

    //TODO refactor using StringBuilder
    public String toString() {

        String line = "";
        String res = "";

        for (int i = board.length - 1; i >= 0; i--) {
            line = "|" + board[i].toString(0) + line;

            if (i % NUM_ROWS == 0) {
                line += "|\n";
                res += line;
                line = "";
            }
        }

        return res;
    }

    public String toString(ArrayList<Square> threatMap) {

        String line = "";
        String res = "";
        String output;

        for (int i = board.length - 1; i >= 0; i--) {
            if (threatMap.contains(board[i])) {
                output = "X";
            } else {
                output = "_";
            }
            line = "|" + output + line;

            if (i % NUM_ROWS == 0) {
                line += "|\n";
                res += line;
                line = "";
            }
        }

        return res;
    }

    private void setPieceAt(Square square, Piece piece) {
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
        if (start == squareAt(0, 0)) movePiece(squareAt(0, 4), squareAt(0, 2));
        // white short
        if (start == squareAt(0, 7)) movePiece(squareAt(0, 4), squareAt(0, 6));
        // black long
        if (start == squareAt(7, 0)) movePiece(squareAt(7, 4), squareAt(7, 2));
        // black short
        if (start == squareAt(7, 7)) movePiece(squareAt(7, 4), squareAt(7, 6));
    }

    private void initialSetup() {
        // Pawns
        for (int i = 0; i < 8; i++) {
            setPieceAt(squareAt(1, i), new Pawn(Color.WHITE));
            setPieceAt(squareAt(6, i), new Pawn(Color.BLACK));

            pieces.add(pieceAt(1, i).get());
            pieces.add(pieceAt(6, i).get());
        }

        setUpKingRow(0, Color.WHITE);
        setUpKingRow(7, Color.BLACK);
    }

    private void setUpKingRow(int row, Color color) {
        setPieceAt(squareAt(row, 0), new Rook(color));
        setPieceAt(squareAt(row, 1), new Knight(color));
        setPieceAt(squareAt(row, 2), new Bishop(color));
        setPieceAt(squareAt(row, 3), new Queen(color));
        setPieceAt(squareAt(row, 4), new King(color));
        setPieceAt(squareAt(row, 5), new Bishop(color));
        setPieceAt(squareAt(row, 6), new Knight(color));
        setPieceAt(squareAt(row, 7), new Rook(color));

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


}
