package com.brianwehrle.chess;

import com.brianwehrle.chess.models.Chessboard;
import com.brianwehrle.chess.models.Move;
import com.brianwehrle.chess.models.Color;
import com.brianwehrle.chess.models.Game;
import com.brianwehrle.chess.models.pieces.Bishop;
import com.brianwehrle.chess.models.pieces.Pawn;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

//@SpringBootTest
class GameLogicTests {
    private Chessboard testBoard = new Chessboard();
    private Game game;

    @Test
    void testPawnBlockingCheck() {
        // given
        String fen = "4k3/8/8/8/1q6/8/2P5/4K3 w - - 0 1";
        testBoard.loadPositionFromFen(fen);
        game = new Game(testBoard, Game.GameStatus.WHITE_TO_MOVE);

        // when
        ArrayList<Move> receivedMoves = game.getLegalMoves();

        // then
        System.out.println(receivedMoves);
        assert(receivedMoves.size() == 0);
    }
}
