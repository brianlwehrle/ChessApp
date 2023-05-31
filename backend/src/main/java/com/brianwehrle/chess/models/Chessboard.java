package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.*;

import java.util.ArrayList;

public class Chessboard {
    private static int NUM_ROWS, NUM_COLS;


    private Square[] board; // rows and cols grow down and right
                            // simpler than a 2d array
    private ArrayList<Piece> blackPieces;
    private ArrayList<Piece> whitePieces;
    private Piece capturedPiece;

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
        capturedPiece = null;

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                board[row * NUM_COLS + col] = new Square(row, col);
            }
        }

        initialSetup();
    }

    public void makeMove(Move move) {
        Piece movingPiece = move.start().getPiece();
        capturedPiece = move.end().getPiece();

        move.start().setPiece(null);
        movingPiece.setCurSquare(move.end());
        move.end().setPiece(movingPiece);

        if (capturedPiece != null) {
            if (capturedPiece.getColor() == Color.WHITE) {
                whitePieces.remove(capturedPiece);
            } else {
                blackPieces.remove(capturedPiece);
            }
        }

        if (!movingPiece.hasMoved()) movingPiece.setHasMoved(true);
    }

    public void undoMove(Move move) {
        Piece movingPiece = move.start().getPiece();

        move.start().setPiece(movingPiece);
        if (capturedPiece != null) {
            move.end().setPiece(capturedPiece);
        }




    }


    public Square getSquareAt(int row, int col) {
        if (row > 7 || col > 7 || row < 0 || col < 0) return null;

        return board[row * NUM_COLS + col];
    }

    public Piece getPieceAt(int row, int col) {
        return getSquareAt(row, col).getPiece();
    }

    public Piece getPieceAt(Square square) {
        return getPieceAt(square.getRow(), square.getCol());
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

    private void initialSetup() {
        // pawns
        for (int i = 0; i < 8; i++) {
            Piece blackPawn = new Pawn(Color.BLACK);
            Piece whitePawn = new Pawn(Color.WHITE);
            setPieceAt(whitePawn, 1, i);
            setPieceAt(blackPawn, 6, i);
        }

        // knights
        Piece blackKnight1 = new Knight(Color.BLACK);
        Piece blackKnight2 = new Knight(Color.BLACK);
        Piece whiteKnight1 = new Knight(Color.WHITE);
        Piece whiteKnight2 = new Knight(Color.WHITE);
        setPieceAt(blackKnight1, 7, 1);
        setPieceAt(blackKnight2, 7, 6);
        setPieceAt(whiteKnight1, 0, 1);
        setPieceAt(whiteKnight2, 0, 6);

        // Rooks
        Piece blackRook1 = new Rook(Color.BLACK);
        Piece blackRook2 = new Rook(Color.BLACK);
        Piece whiteRook1 = new Rook(Color.WHITE);
        Piece whiteRook2 = new Rook(Color.WHITE);
        setPieceAt(blackRook1, 7, 0);
        setPieceAt(blackRook2, 7, 7);
        setPieceAt(whiteRook1, 0, 0);
        setPieceAt(whiteRook2, 0, 7);

        // Bishops
        Piece blackBishop1 = new Bishop(Color.BLACK);
        Piece blackBishop2 = new Bishop(Color.BLACK);
        Piece whiteBishop1 = new Bishop(Color.WHITE);
        Piece whiteBishop2 = new Bishop(Color.WHITE);
        setPieceAt(blackBishop1, 7, 2);
        setPieceAt(blackBishop2, 7, 5);
        setPieceAt(whiteBishop1, 0, 2);
        setPieceAt(whiteBishop2, 0, 5);

        // Kings
        Piece whiteKing = new King(Color.WHITE);
        Piece blackKing = new King(Color.BLACK);
        setPieceAt(blackKing, 7, 3);
        setPieceAt(whiteKing, 0, 3);

        // Queens
        Piece whiteQueen = new Queen(Color.WHITE);
        Piece blackQueen = new Queen(Color.BLACK);
        setPieceAt(blackQueen, 7, 4);
        setPieceAt(whiteQueen, 0, 4);

        for (int i = 0; i < 16; i++) {
            if (board[i].getPiece() == null) continue;
            whitePieces.add(board[i].getPiece());
            //System.out.println("Added a " + board[i].getPiece().getColor() + " " + board[i].getPiece().getType() + " to square " + board[i].getRow() + ", " + board[i].getCol());
        }

        for (int i = 48; i < board.length; i++) {
            if (board[i].getPiece() == null) continue;
            blackPieces.add(board[i].getPiece());
            //System.out.println("Added a " + board[i].getPiece().getColor() + " " + board[i].getPiece().getType() + " to square " + board[i].getRow() + ", " + board[i].getCol());
        }
    }

    private void setPieceAt(Piece piece, int row, int col) {
        Square square = getSquareAt(row, col);
        piece.setCurSquare(square);
        square.setPiece(piece);
    }
}
