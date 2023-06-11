package com.brianwehrle.chess.models;

import com.brianwehrle.chess.models.pieces.Piece;

import java.util.*;

public class Game {

    public enum GameStatus {
        BLACK_TO_MOVE,
        WHITE_TO_MOVE,
        VICTORY_WHITE,
        VICTORY_BLACK,
        STALEMATE,
        DRAW,
        INVALID_MOVE
    }

    private final UUID gameId;
    private Chessboard board;
    private final Player whitePlayer, blackPlayer;
    private Player currentPlayer;
    private GameStatus status;
    private final ArrayList<Move> prevMoves;
    private ArrayList<Move> legalMoves;
    private final HashMap<String, Integer> positionCounts;
    private int moveNumber;
    private int halfMoveNumber;



    public Game(Player whitePlayer, Player blackPlayer) {
        gameId = UUID.randomUUID();
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        whitePlayer.setColor(Color.WHITE);
        blackPlayer.setColor(Color.BLACK);
        board = new Chessboard();
        currentPlayer = whitePlayer;
        status = GameStatus.WHITE_TO_MOVE;
        prevMoves = new ArrayList<>();
        legalMoves = new ArrayList<>();
        positionCounts = new HashMap<>();
        moveNumber = 1;
        halfMoveNumber = 0;
    }

//TODO load from fen

    // move is a legal move
    // updates the status of the game after the move is made
    public void makeMove(Move move) {
        setCastleFlag(move);
        setEnPassantSquare(move);

        board.move(move);
        prevMoves.add(move);

        if (currentPlayer == blackPlayer) moveNumber++;
        // track half move number for 50 move rule
        if (move.getMovingPiece() == Piece.PieceType.PAWN || move.getCapturedPiece().isPresent())
            halfMoveNumber = 0;
        else
            halfMoveNumber += 1;

        // switch player
        currentPlayer = (currentPlayer == whitePlayer ? blackPlayer : whitePlayer);

        storeFenPosition();

        legalMoves = calculateLegalMoves();

        updateStatus();
    }

    public boolean isLegalMove(Move move) {
        return legalMoves.contains(move);
    }

    public UUID getGameId() {
        return gameId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public String getFen() {
        return convertToFen();
    }

    public static Move convertToMove(String notationMove) {
        return null;
    }

    private void updateStatus() {
        if (legalMoves.isEmpty()) {
            if (isInCheck(currentPlayer.getColor())) {
                //checkmate
                status = (currentPlayer.getColor() == Color.WHITE ? GameStatus.VICTORY_BLACK : GameStatus.VICTORY_WHITE);
            } else {
                status = GameStatus.STALEMATE;
            }
        } else if (isDraw()){
            status = GameStatus.DRAW;
        } else {
            status = (currentPlayer.getColor() == Color.WHITE ? GameStatus.WHITE_TO_MOVE : GameStatus.BLACK_TO_MOVE);
        }
    }

    private ArrayList<Move> calculateLegalMoves() {
        legalMoves = getPossibleMoves(currentPlayer.getColor());
        removeIllegalMoves(legalMoves);
        return legalMoves;
    }

    private void storeFenPosition() {
        String fen = convertToFen();
        String fenPosition = fen.substring(0, fen.indexOf(" "));
        positionCounts.put(fenPosition, positionCounts.getOrDefault(fenPosition, 0) + 1);
    }

    private boolean isDraw() {
        // 3 move repetition
        if (positionCounts.get(this.convertToFen()) >= 3) {
            System.out.println("Draw by 3 move repetition!");
            return true;
        }

        // 50 move rule
        if (halfMoveNumber >= 50) {
            System.out.println("Draw by 50 move rule!");
            return true;
        }

        return false;
    }

    // adjusts the castle rights flag in Player if a rook or king moves
    private void setCastleFlag(Move move) {
        if (move.getMovingPiece() == Piece.PieceType.KING || move.moveType() == Move.MoveType.CASTLE) {
            currentPlayer.setCastle(false, false);
        } else if (move.getMovingPiece() == Piece.PieceType.ROOK && move.getInitialCol() == 0) { // queenside
            currentPlayer.setCastle(false, true);
        } else if (move.getMovingPiece() == Piece.PieceType.ROOK && move.getInitialCol() == 7) { // kingside
            currentPlayer.setCastle(true, false);
        }
    }

    // sets the square available for En Passant in Player
    private void setEnPassantSquare(Move move) {
        if (move.moveType() == Move.MoveType.DOUBLE) {
            char col = (char)(move.getInitialCol() + 'a');
            int row = (currentPlayer.getColor() == Color.WHITE ? 3 : 6);
            currentPlayer.setEnPassantSquare(col + String.valueOf(row));
        } else {
            currentPlayer.setEnPassantSquare("-");
        }
    }

    //TODO convert to bitboard
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
                case QUEEN, ROOK, BISHOP, KNIGHT -> addQRNBMoves(piece, start, moves);
            }
        }

        return moves;
    }

    private void addKingMoves(Piece piece, Square start, ArrayList<Move> moves) {
            for (Direction direction : piece.getDirections()) {
                Square nextSquare = board.squareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

                if (nextSquare != null) { // is in bounds
                    if (nextSquare.isEmpty() || piece.differentColor(nextSquare.getPiece().get()))
                        moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare, currentPlayer.getColor()));
                }
            }
    }

    private void addQRNBMoves(Piece piece, Square start, ArrayList<Move> moves) {
        // for each direction build a path of moves until you hit another piece
        for (Direction direction : piece.getDirections()) {
            Square nextSquare = board.squareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

            for (int scalar = 2; nextSquare != null; scalar++) {
                if (nextSquare.isEmpty()) {
                    moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare, currentPlayer.getColor()));
                } else if (piece.differentColor(nextSquare.getPiece().get())) {
                    moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare, currentPlayer.getColor()));
                    break;
                } else {
                    break;
                }

                // knights have no scalars
                if (piece.getType() == Piece.PieceType.KNIGHT) break;

                nextSquare = board.squareAt(start.getRow() + direction.dy() * scalar, start.getCol() + direction.dx() * scalar);
            }
        }

        //TODO could switch castling to king
        //castling
        Square finalSquare = canCastle(piece);
        if (piece.getType() == Piece.PieceType.ROOK && finalSquare != null)
            moves.add(new Move(piece.getType(), Move.MoveType.CASTLE, piece.square(), finalSquare, currentPlayer.getColor()));
    }

    private void addPawnMoves(Piece piece, Square start, ArrayList<Move> moves) {
        // moving
        for (Direction direction : piece.getDirections()) {
            int dy = direction.dy();
            Square nextSquare = board.squareAt(start.getRow() + dy, start.getCol());
            if (nextSquare != null && nextSquare.isEmpty()) {
                if (Math.abs(dy) == 2) {
                    moves.add(new Move(piece.getType(), Move.MoveType.DOUBLE, start, nextSquare, currentPlayer.getColor()));
                } else {
                    if (nextSquare.getRow() == 0 || nextSquare.getRow() == 7) { // promotion
                        addPromotions(moves, start, nextSquare);
                    } else {
                        moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare, currentPlayer.getColor()));
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
                // en passant
                if (nextSquare.getNotation().equals(otherPlayer().getEnPassantSquare())) {
                    Piece capturedPawn = board.pieceAt(board.squareAt(nextSquare.getRow() - dy, nextSquare.getCol()));
                    moves.add(new Move(piece.getType(), Move.MoveType.EN_PASSANT, start, nextSquare, capturedPawn, currentPlayer.getColor()));
                }

                if (!nextSquare.isEmpty() && piece.differentColor(nextSquare.getPiece().get())) {
                    if (nextSquare.getRow() == 0 || nextSquare.getRow() == 7) {
                        addPromotions(moves, start, nextSquare); // promotion
                    } else {
                        moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare, currentPlayer.getColor()));
                    }
                }
            }
        }
    }

    private void addPromotions(ArrayList<Move> moves, Square start, Square nextSquare) {
        moves.add(new Move(Piece.PieceType.PAWN, Move.MoveType.PROMOTION, Piece.PieceType.KNIGHT, start, nextSquare, currentPlayer.getColor()));
        moves.add(new Move(Piece.PieceType.PAWN, Move.MoveType.PROMOTION, Piece.PieceType.BISHOP, start, nextSquare, currentPlayer.getColor()));
        moves.add(new Move(Piece.PieceType.PAWN, Move.MoveType.PROMOTION, Piece.PieceType.ROOK, start, nextSquare, currentPlayer.getColor()));
        moves.add(new Move(Piece.PieceType.PAWN, Move.MoveType.PROMOTION, Piece.PieceType.QUEEN, start, nextSquare, currentPlayer.getColor()));
    }

    // any moves that allow your king to be in check
    private void removeIllegalMoves(ArrayList<Move> possibleMoves) {
        // cannot move if my king would be in check
        // simulate each move and see if my king would be in check
        Chessboard tempBoard = board;
        for (Iterator<Move> iterator = possibleMoves.iterator(); iterator.hasNext(); ) {
            board = new Chessboard(prevMoves);
            board.move(iterator.next());

            if (isInCheck(currentPlayer.getColor()))
                iterator.remove();
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
        Color color = currentPlayer.getColor();

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

    private Player otherPlayer() {
        return (currentPlayer == whitePlayer ? blackPlayer : whitePlayer);
    }

    private String convertToFen() {
        StringBuilder fen = new StringBuilder();
        int emptySquares = 0;
        String letter = "";

        for (int i = board.getBoard().length - 8; i >= 0; i++) {
            if (board.getBoard()[i].isEmpty()) {
                emptySquares++;
            } else {
                if (emptySquares > 0) {
                    fen.append(emptySquares);
                    emptySquares = 0;
                }
                switch (board.getBoard()[i].getPiece().get().getType()) {
                    case BISHOP -> letter = "B";
                    case QUEEN -> letter = "Q";
                    case ROOK -> letter = "R";
                    case PAWN -> letter = "P";
                    case KING -> letter = "K";
                    case KNIGHT -> letter = "N";
                }
                if (board.getBoard()[i].getPiece().get().getColor() == Color.BLACK)
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
        fen.deleteCharAt(fen.length()-1);
        fen.append(" ");
        fen.append((currentPlayer == whitePlayer ? "w" : "b"));
        fen.append(" ");
        if (whitePlayer.canCastle("Short")) fen.append("K");
        if (whitePlayer.canCastle("Long")) fen.append("Q");
        if (blackPlayer.canCastle("Short")) fen.append("k");
        if (blackPlayer.canCastle("Long")) fen.append("q");
        fen.append(" ");
        fen.append(otherPlayer().getEnPassantSquare());
        fen.append(" ");
        fen.append(halfMoveNumber);
        fen.append(" ");
        fen.append(moveNumber);

        return fen.toString();
    }
}
