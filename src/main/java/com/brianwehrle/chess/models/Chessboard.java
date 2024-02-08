package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.*;

import java.util.ArrayList;

public class Chessboard {
    private static final int NUM_ROWS = 8;
    private static final int NUM_COLS = 8;

    // represents a 2d array
    // rows and cols grow down and right
    // 0, 0 is a1, 0, 7 is a8
    private Square[] board;
    private ArrayList<Piece> pieces;

    public Chessboard() {
        initialSetup();
    }

    // load position from moveList
    public Chessboard(ArrayList<Move> moveList) {
        initialSetup();

        for (Move move : moveList) {
            move(move);
        }
    }

    public void move(Move move) {
        switch (move.getMoveType()) {
            case STANDARD -> movePiece(getSquareAt(move.getStartRow(), move.getStartCol()), getSquareAt(move.getEndRow(), move.getEndCol()));
            case CASTLE -> castle(move);
            case EN_PASSANT -> enPassant(move);
            case PROMOTION_KNIGHT, PROMOTION_BISHOP, PROMOTION_ROOK, PROMOTION_QUEEN -> promote(move);
            default -> {
                System.out.println("Invalid move");
                System.exit(1);
            }
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

    public void setPiece(Square square, Piece piece) {
        if (piece != null) {
            piece.setSquare(square);
            square.setPiece(piece);
        } else {
            square.setPiece(null);
        }
    }

    public void addNewPiece(Square square, Piece piece) {
        setPiece(square, piece);
        pieces.add(piece);
    }

    public Square getSquareAt(int row, int col) {
        if (row > 7 || col > 7 || row < 0 || col < 0) return null;

        return board[row * NUM_COLS + col];
    }

    public Piece pieceAt(int row, int col) {
        return getSquareAt(row, col).getPiece();
    }

    public String toString() {

        StringBuilder line = new StringBuilder();
        StringBuilder res = new StringBuilder();

        for (int i = board.length - 1; i >= 0; i--) {
            line.insert(0, "|" + board[i].toString());

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


    // returns only the position portion of a FEN.
    // Game information is required for the full FEN.
    public String convertPositionToFen() {
        StringBuilder fen = new StringBuilder();
        int emptySquares = 0;
        String letter = "";

        for (int i = board.length - 8; i >= 0; i++) {
            if (board[i].isEmpty()) {
                emptySquares++;
            } else {
                if (emptySquares > 0) {
                    fen.append(emptySquares);
                    emptySquares = 0;
                }
                switch (board[i].getPiece().getType()) {
                    case BISHOP -> letter = "B";
                    case QUEEN -> letter = "Q";
                    case ROOK -> letter = "R";
                    case PAWN -> letter = "P";
                    case KING -> letter = "K";
                    case KNIGHT -> letter = "N";
                }
                if (board[i].getPiece().getColor() == Color.BLACK)
                    letter = letter.toLowerCase();

                fen.append(letter);
            }
            if ((i + 1) % 8 == 0) {
                if (emptySquares > 0)
                    fen.append(emptySquares);
                fen.append("/");
                emptySquares = 0;
                i -= 16;
            }
        }

        return fen.toString();
    }

    public void loadPositionFromFen(String fen) {
        initializeSquares(); // start new board

        int row = 7;
        int col = 0;
        for (int i = 0; i < fen.length(); i++) {
            Character letter = fen.charAt(i);

            if (letter.equals('/')) {
                row--;
                col = 0;
            } else if (letter >= 48 && letter <= 57) {
                int num = letter - 48;
                while (num > 0) {
                    setPiece(getSquareAt(row, col), null);
                    col++;
                    num--;
                }
            } else {
                switch(letter) {
                    case 'r' -> addNewPiece(getSquareAt(row, col), new Rook(Color.BLACK));
                    case 'n' -> addNewPiece(getSquareAt(row, col), new Knight(Color.BLACK));
                    case 'b' -> addNewPiece(getSquareAt(row, col), new Bishop(Color.BLACK));
                    case 'q' -> addNewPiece(getSquareAt(row, col), new Queen(Color.BLACK));
                    case 'k' -> addNewPiece(getSquareAt(row, col), new King(Color.BLACK));
                    case 'p' -> addNewPiece(getSquareAt(row, col), new Pawn(Color.BLACK));

                    case 'R' -> addNewPiece(getSquareAt(row, col), new Rook(Color.WHITE));
                    case 'N' -> addNewPiece(getSquareAt(row, col), new Knight(Color.WHITE));
                    case 'B' -> addNewPiece(getSquareAt(row, col), new Bishop(Color.WHITE));
                    case 'Q' -> addNewPiece(getSquareAt(row, col), new Queen(Color.WHITE));
                    case 'K' -> addNewPiece(getSquareAt(row, col), new King(Color.WHITE));
                    case 'P' -> addNewPiece(getSquareAt(row, col), new Pawn(Color.WHITE));

                    default -> {
                        System.out.println("Unrecognized piece");
                        System.exit(1);
                    }
                }
                col++;
            }
        }
    }

    private void initializeSquares() {
        board = new Square[NUM_COLS * NUM_ROWS];
        pieces = new ArrayList<>();

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                board[row * NUM_COLS + col] = new Square(row, col);
            }
        }
    }

    private void movePiece(Square start, Square end) {
        if (!end.isEmpty())
            pieces.remove(end.getPiece());

        setPiece(end, start.getPiece());
        setPiece(start, null);
    }

    private void promote(Move move) {
        Square start = getSquareAt(move.getStartRow(), move.getStartCol());
        Square end = getSquareAt(move.getEndRow(), move.getEndCol());
        Color color = start.getPiece().getColor();

        movePiece(start, end);
        pieces.remove(end.getPiece());

        Piece newPiece;
        switch (move.getMoveType()) {
            case PROMOTION_BISHOP -> newPiece = new Bishop(color);
            case PROMOTION_ROOK -> newPiece = new Rook(color);
            case PROMOTION_KNIGHT -> newPiece = new Knight(color);
            case PROMOTION_QUEEN -> newPiece = new Queen(color);
            default -> throw new RuntimeException("No promotion type specified."); //TODO make some exceptions
        }

        addNewPiece(end, newPiece);
    }

    private void enPassant(Move move) {
        Square start = getSquareAt(move.getStartRow(), move.getStartCol());
        Square end = getSquareAt(move.getEndRow(), move.getEndCol());
        Color color = start.getPiece().getColor();

        movePiece(start, end);

        Square capturedPawnSquare = getSquareAt(end.getRow() - (color == Color.WHITE ? 1 : -1), end.getCol());
        pieces.remove(capturedPawnSquare.getPiece());
        setPiece(capturedPawnSquare, null);
    }

    private void castle(Move move) {
        Square start = getSquareAt(move.getStartRow(), move.getStartCol());
        Square end = getSquareAt(move.getEndRow(), move.getEndCol());

        // move the king
        movePiece(start, end);

        // move rook
        int row = (end.getPiece().getColor() == Color.WHITE ? 0 : 7);
        int startCol = (end.getCol() == 2 ? 0 : 7);
        int endCol = (end.getCol() == 2 ? 3 : 5);

        movePiece(getSquareAt(row, startCol), getSquareAt(row, endCol));
    }

    private void initialSetup() {
        initializeSquares();

        for (int i = 0; i < 8; i++) {
            addNewPiece(getSquareAt(1, i), new Pawn(Color.WHITE));
            addNewPiece(getSquareAt(6, i), new Pawn(Color.BLACK));
        }

        setUpKingRow(0, Color.WHITE);
        setUpKingRow(7, Color.BLACK);
    }

    private void setUpKingRow(int row, Color color) {
        addNewPiece(getSquareAt(row, 0), new Rook(color));
        addNewPiece(getSquareAt(row, 1), new Knight(color));
        addNewPiece(getSquareAt(row, 2), new Bishop(color));
        addNewPiece(getSquareAt(row, 3), new Queen(color));
        addNewPiece(getSquareAt(row, 4), new King(color));
        addNewPiece(getSquareAt(row, 5), new Bishop(color));
        addNewPiece(getSquareAt(row, 6), new Knight(color));
        addNewPiece(getSquareAt(row, 7), new Rook(color));
    }
}
