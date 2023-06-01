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

    public void makeMove(Move move) {
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

    public Piece getKing(Color color) {
        if (color == Color.BLACK) {
            for (Piece piece : blackPieces) {
                if (piece.getType() == PieceType.KING) return piece;
            }
        } else {
            for (Piece piece : whitePieces) {
                if (piece.getType() == PieceType.KING) return piece;
            }
        }

        System.err.println("Error, couldn't find King!!");
        return null;
    }

    public Square getSquareAt(int row, int col) {
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

    private void initialSetup() {
        // Pawns
        for (int i = 0; i < 8; i++) {
            Piece blackPawn = new Pawn(Color.BLACK);
            Piece whitePawn = new Pawn(Color.WHITE);
            setPieceAt(getSquareAt(1, i), whitePawn);
            setPieceAt(getSquareAt(6, i), blackPawn);
        }

        // Knights
        Piece blackKnight1 = new Knight(Color.BLACK);
        Piece blackKnight2 = new Knight(Color.BLACK);
        Piece whiteKnight1 = new Knight(Color.WHITE);
        Piece whiteKnight2 = new Knight(Color.WHITE);
        setPieceAt(getSquareAt(7, 1), blackKnight1);
        setPieceAt(getSquareAt(7, 6), blackKnight2);
        setPieceAt(getSquareAt(0, 1), whiteKnight1);
        setPieceAt(getSquareAt(0, 6), whiteKnight2);

        // Rooks
        Piece blackRook1 = new Rook(Color.BLACK);
        Piece blackRook2 = new Rook(Color.BLACK);
        Piece whiteRook1 = new Rook(Color.WHITE);
        Piece whiteRook2 = new Rook(Color.WHITE);
        setPieceAt(getSquareAt(7, 0), blackRook1);
        setPieceAt(getSquareAt(7, 7), blackRook2);
        setPieceAt(getSquareAt(0, 0), whiteRook1);
        setPieceAt(getSquareAt(0, 7), whiteRook2);

        // Bishops
        Piece blackBishop1 = new Bishop(Color.BLACK);
        Piece blackBishop2 = new Bishop(Color.BLACK);
        Piece whiteBishop1 = new Bishop(Color.WHITE);
        Piece whiteBishop2 = new Bishop(Color.WHITE);
        setPieceAt(getSquareAt(7, 2), blackBishop1);
        setPieceAt(getSquareAt(7, 5), blackBishop2);
        setPieceAt(getSquareAt(0, 2), whiteBishop1);
        setPieceAt(getSquareAt(0, 5), whiteBishop2);

        // Kings
        Piece whiteKing = new King(Color.WHITE);
        Piece blackKing = new King(Color.BLACK);
        setPieceAt(getSquareAt(7, 3), blackKing);
        setPieceAt(getSquareAt(0, 3), whiteKing);

        // Queens
        Piece whiteQueen = new Queen(Color.WHITE);
        Piece blackQueen = new Queen(Color.BLACK);
        setPieceAt(getSquareAt(7, 4), blackQueen);
        setPieceAt(getSquareAt(0, 4), whiteQueen);

        for (int i = 0; i < 16; i++) {
            if (board[i].getPiece() == null) continue;
            whitePieces.add(board[i].getPiece());
            // System.out.println("Added a " + board[i].getPiece().getColor() + " " + board[i].getPiece().getType() + " to square " + board[i].getRow() + ", " + board[i].getCol());
        }

        for (int i = 48; i < board.length; i++) {
            if (board[i].getPiece() == null) continue;
            blackPieces.add(board[i].getPiece());
            // System.out.println("Added a " + board[i].getPiece().getColor() + " " + board[i].getPiece().getType() + " to square " + board[i
        }
    }
}
