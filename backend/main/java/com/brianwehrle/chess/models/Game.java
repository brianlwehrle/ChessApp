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
    private final ArrayList<Move> moveHistory;
    private ArrayList<Move> legalMoves;
    private final HashMap<String, Integer> positionCounts;
    private int moveNumber;
    private int halfMoveNumber;


    public Game(Player whitePlayer, Player blackPlayer, UUID gameId) {
        this.gameId = gameId;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        whitePlayer.setColor(Color.WHITE);
        blackPlayer.setColor(Color.BLACK);
        board = new Chessboard();
        currentPlayer = whitePlayer;
        status = GameStatus.WHITE_TO_MOVE;
        moveHistory = new ArrayList<>();
        legalMoves = calculateLegalMoves();
        positionCounts = new HashMap<>();
        moveNumber = 1;
        halfMoveNumber = 0;
    }

    // for testing
    public Game(Chessboard testBoard, GameStatus status) {
        gameId = UUID.randomUUID();
        whitePlayer = new Player("whiteTest");
        blackPlayer = new Player("blackTest");
        whitePlayer.setColor(Color.WHITE);
        blackPlayer.setColor(Color.BLACK);
        board = testBoard;
        currentPlayer = (status == GameStatus.WHITE_TO_MOVE ? whitePlayer : blackPlayer);
        this.status = status;
        moveHistory = new ArrayList<>();
        legalMoves = calculateLegalMoves();
        positionCounts = new HashMap<>();
        moveNumber = 1;
        halfMoveNumber = 0;
    }

//TODO load from fen

    public ArrayList<Move> getLegalMoves() {
        return legalMoves;
    }

    // updates the status of the game after the move is made
    public GameStatus makeMove(Move move) {
        if (move == null || !legalMoves.contains(move))
            return GameStatus.INVALID_MOVE;

        // game is over
        if (status != GameStatus.BLACK_TO_MOVE && status != GameStatus.WHITE_TO_MOVE) {
            return status;
        }

        setCastleRights(move);
        setEnPassantSquare(move);

        board.move(move);
        moveHistory.add(move);

        if (currentPlayer == blackPlayer) moveNumber++;
        // track half move number for 50 move rule
        if (move.getPieceType() == Piece.PieceType.PAWN || move.getMoveType() == Move.MoveType.CAPTURE)
            halfMoveNumber = 0;
        else
            halfMoveNumber += 1;

        currentPlayer = otherPlayer();

        // add current position to position counts
        positionCounts.put(getFenPosition(), positionCounts.getOrDefault(getFenPosition(), 0) + 1);

        legalMoves = calculateLegalMoves();

        return updateStatus();
    }

    private GameStatus updateStatus() {
        if (legalMoves.isEmpty()) {
            if (inCheck(currentPlayer.getColor())) {
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

        return status;
    }

    private ArrayList<Move> calculateLegalMoves() {
        legalMoves = getPossibleMoves(currentPlayer.getColor());
        removeIllegalMoves(legalMoves);
        return legalMoves;
    }

    private boolean isDraw() {
        // 3 move repetition
        if (positionCounts.get(getFenPosition()) >= 3) {
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

    // sets the square available for En Passant in Player
    private void setEnPassantSquare(Move move) {
        if (move.getPieceType() == Piece.PieceType.PAWN && Math.abs(move.getEndRow() - move.getStartRow()) == 2) {
            char col = (char)(move.getStartCol() + 'a');
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
                        Square nextSquare = board.getSquareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

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
                        Square nextSquare = board.getSquareAt(start.getRow() + dy, start.getCol() + dx);
                        if (nextSquare != null) {
                            threatMap.add(nextSquare);
                        }
                    }
                }

                case QUEEN, ROOK, BISHOP, KNIGHT -> {
                    // for each direction build a path of moves until you hit another piece
                    for (Direction direction : piece.getDirections()) {
                        Square nextSquare = board.getSquareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

                        for (int scalar = 2; nextSquare != null; scalar++) {
                            threatMap.add(nextSquare);

                            if (nextSquare.getPiece() != null) break;

                            // knights have no scalars
                            if (piece.getType() == Piece.PieceType.KNIGHT) break;

                            nextSquare = board.getSquareAt(start.getRow() + direction.dy() * scalar, start.getCol() + direction.dx() * scalar);
                        }
                    }
                }
            }
        }
        return threatMap;
    }

    // "Pseudo-legal moves", i.e. moves that adhere to basic piece movement rules but
    // not concerned with things like being in check
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
            Square nextSquare = board.getSquareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

            if (nextSquare != null) { // is in bounds
                if (nextSquare.isEmpty() || piece.differentColor(nextSquare.getPiece()))
                    moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare));
            }
        }

        //castling
        if (currentPlayer.canCastle("Long") && castlingUnobstructed("Long")) {
            Square finalSquare = board.getSquareAt(start.getRow(), 2);
            moves.add(new Move(piece.getType(), Move.MoveType.CASTLE, piece.square(), finalSquare));
        }

        if (currentPlayer.canCastle("Short")  && castlingUnobstructed("Short")) {
            Square finalSquare = board.getSquareAt(start.getRow(), 6);
            moves.add(new Move(piece.getType(), Move.MoveType.CASTLE, piece.square(), finalSquare));
        }
    }

    private void addQRNBMoves(Piece piece, Square start, ArrayList<Move> moves) {
        // for each direction build a path of moves until you hit another piece
        for (Direction direction : piece.getDirections()) {
            Square nextSquare = board.getSquareAt(start.getRow() + direction.dy(), start.getCol() + direction.dx());

            for (int scalar = 2; nextSquare != null; scalar++) {
                if (nextSquare.isEmpty()) {
                    moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare));
                } else if (piece.differentColor(nextSquare.getPiece())) {
                    moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare));
                    break;
                } else {
                    break;
                }

                // knights have no scalars
                if (piece.getType() == Piece.PieceType.KNIGHT) break;

                nextSquare = board.getSquareAt(start.getRow() + direction.dy() * scalar, start.getCol() + direction.dx() * scalar);
            }
        }

    }

    private void addPawnMoves(Piece piece, Square start, ArrayList<Move> moves) {
        // moving
        for (Direction direction : piece.getDirections()) {
            int dy = direction.dy();
            Square nextSquare = board.getSquareAt(start.getRow() + dy, start.getCol());
            if (nextSquare != null && nextSquare.isEmpty()) {
                if (Math.abs(dy) == 2) {
                    moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare));
                } else {
                    if (nextSquare.getRow() == 0 || nextSquare.getRow() == 7) { // promotion
                        addPromotions(moves, start, nextSquare);
                    } else {
                        moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare));
                    }
                }
            } else {
                break;
            }
        }

        //attacking
        int dy = piece.getDirections().get(0).dy();
        for (int dx = -1; dx <= 1; dx += 2) { // just checks both forward diagonals
            Square nextSquare = board.getSquareAt(start.getRow() + dy, start.getCol() + dx);
            if (nextSquare != null) {
                // en passant
                if (nextSquare.getNotation().equals(otherPlayer().getEnPassantSquare())) {
                    moves.add(new Move(piece.getType(), Move.MoveType.EN_PASSANT, start, nextSquare));
                }

                if (!nextSquare.isEmpty() && piece.differentColor(nextSquare.getPiece())) {
                    if (nextSquare.getRow() == 0 || nextSquare.getRow() == 7) {
                        addPromotions(moves, start, nextSquare); // promotion
                    } else {
                        moves.add(new Move(piece.getType(), Move.MoveType.STANDARD, start, nextSquare));
                    }
                }
            }
        }
    }

    private void addPromotions(ArrayList<Move> moves, Square start, Square nextSquare) {
        moves.add(new Move(Piece.PieceType.PAWN, Move.MoveType.PROMOTION_KNIGHT, start, nextSquare));
        moves.add(new Move(Piece.PieceType.PAWN, Move.MoveType.PROMOTION_BISHOP, start, nextSquare));
        moves.add(new Move(Piece.PieceType.PAWN, Move.MoveType.PROMOTION_ROOK, start, nextSquare));
        moves.add(new Move(Piece.PieceType.PAWN, Move.MoveType.PROMOTION_QUEEN, start, nextSquare));
    }

    // any moves that allow your king to be in check
    private void removeIllegalMoves(ArrayList<Move> possibleMoves) {
        // cannot move if my king would be in check
        // simulate each move and see if my king would be in check
        Chessboard tempBoard = board;
        String currentPosition = getFenPosition();
        for (Iterator<Move> iterator = possibleMoves.iterator(); iterator.hasNext(); ) {
            board = new Chessboard();
            board.loadPositionFromFen(currentPosition);
            board.move(iterator.next());

            if (inCheck(currentPlayer.getColor()))
                iterator.remove();
        }
        board = tempBoard;
    }

    private boolean isUnderAttack(Color color, Square square) {
        // check threats of opposite color
        return getThreatMap(color == Color.WHITE ? Color.BLACK : Color.WHITE).contains(square);
    }

    private boolean inCheck(Color color) {
        return isUnderAttack(color, board.getKingLoc(color));
    }

    // adjusts the castle rights flag in Player if a rook or king moves
    private void setCastleRights(Move move) {
        if (move.getPieceType() == Piece.PieceType.KING) {
            currentPlayer.setCastle(false, false);
        } else if (move.getPieceType() == Piece.PieceType.ROOK) {

            if (move.getStartRow() == 0 || move.getStartRow() == 7) {
                if (move.getStartCol() == 0 ) { // queenside
                    currentPlayer.setCastle(false, true);
                } else if (move.getStartCol() == 7) { //kingside
                    currentPlayer.setCastle(true, false);
                }
            }

        }
    }

    private boolean castlingUnobstructed(String side) {
        if (inCheck(currentPlayer.getColor())) return false;

        int row = (currentPlayer.getColor() == Color.WHITE ? 0 : 7);
        int col = (side.equals("Long") ? 2 : 5);

        for (int i = 0; i < 2; i++) {
            if (!board.getSquareAt(row, col + i).isEmpty() || isUnderAttack(currentPlayer.getColor(), board.getSquareAt(row, col + i))) {
                return false;
            }
        }

        return true;
    }

    private Player otherPlayer() {
        return (currentPlayer == whitePlayer ? blackPlayer : whitePlayer);
    }

    private String convertToFullFen() {
        StringBuilder fen = new StringBuilder(board.convertPositionToFen());
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

    public UUID getGameId() {
        return gameId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public String getFen() {
        return convertToFullFen();
    }

    // only the position portion of the fen
    public String getFenPosition() {
        return board.convertPositionToFen();
    }
}
