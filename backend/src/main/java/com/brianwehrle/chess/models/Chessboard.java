package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.*;

import java.util.ArrayList;

public class Chessboard {
    private static int NUM_ROWS, NUM_COLS;


    private Square[] board; // rows and cols grow down and right
                            // simpler than a 2d array
    private ArrayList<Piece> blackPieces;
    private ArrayList<Piece> whitePieces;
    private Piece lastCapturedPiece;

    public ArrayList<Piece> getBlackPieces() {
        return blackPieces;
    }

    public ArrayList<Piece> getWhitePieces() {
        return whitePieces;
    }

    public Chessboard() {
        NUM_COLS = NUM_ROWS = 8;

        board = new Square[NUM_COLS * NUM_ROWS];
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        lastCapturedPiece = null;

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                board[row * NUM_COLS + col] = new Square(row, col);
            }
        }

        initialSetup();
    }

    public void movePiece(Move move) {
        Piece movingPiece = move.start().getPiece();
        lastCapturedPiece = move.end().getPiece();

        setPieceAt(move.start(), null);
        movingPiece.setCurSquare(move.end());
        setPieceAt(move.end(), movingPiece);

        if (lastCapturedPiece != null) {
            if (lastCapturedPiece.getColor() == Color.WHITE) {
                whitePieces.remove(lastCapturedPiece);
            } else {
                blackPieces.remove(lastCapturedPiece);
            }
        }

        if (!movingPiece.hasMoved()) {
            movingPiece.setHasMoved(true);
            movingPiece.setMovedLastTurn(true);
        } else {
            movingPiece.setMovedLastTurn(false);
        }
    }

    public void undoMove(Move move) {
        Piece movingPiece = move.end().getPiece();

        setPieceAt(move.start(), movingPiece);

        if (lastCapturedPiece != null) {
            setPieceAt(move.end(), lastCapturedPiece);
            if (lastCapturedPiece.getColor() == Color.WHITE) {
                whitePieces.add(lastCapturedPiece);
            } else {
                blackPieces.add(lastCapturedPiece);
            }
        } else {
            setPieceAt(move.end(), null);
        }

        if (movingPiece.movedLastTurn()) {
            movingPiece.setHasMoved(false);
        }
    }

    public Square findKing(Color color) {
        if (color == Color.BLACK) {
            for (Piece piece : blackPieces) {
                if (piece.getType() == PieceType.KING) return piece.getCurSquare();
            }
        } else {
            for (Piece piece : whitePieces) {
                if (piece.getType() == PieceType.KING) return piece.getCurSquare();
            }
        }

        System.err.println("Error, couldn't find King!!");
        return null;
    }

    public Square squareAt(int row, int col) {
        if (row > 7 || col > 7 || row < 0 || col < 0) return null;

        return board[row * NUM_COLS + col];
    }

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
        String output = "";

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

    public void setPieceAt(Square square, Piece piece) {
        if (piece != null) {
            piece.setCurSquare(square);
            square.setPiece(piece);
        } else {
            square.setPiece(null);
        }
    }

    public Piece pieceAt(int row, int col) {
        return squareAt(row, col).getPiece();
    }

    private void initialSetup() {
        // Pawns
        for (int i = 0; i < 8; i++) {
            setPieceAt(squareAt(1, i), new Pawn(Color.WHITE));
            setPieceAt(squareAt(6, i), new Pawn(Color.BLACK));
        }
        setUpKingRow(0, Color.WHITE);
        setUpKingRow(7, Color.BLACK);

        for (int i = 0; i < 16; i++) {
            if (board[i].getPiece() == null) continue;
            whitePieces.add(board[i].getPiece());
        }

        for (int i = 48; i < board.length; i++) {
            if (board[i].getPiece() == null) continue;
            blackPieces.add(board[i].getPiece());
        }
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
    }
}
