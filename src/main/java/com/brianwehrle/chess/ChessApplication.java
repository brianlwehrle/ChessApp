package com.brianwehrle.chess;

import com.brianwehrle.chess.models.Chessboard;
import com.brianwehrle.chess.models.Game;
import com.brianwehrle.chess.models.Move;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@SpringBootApplication
@RestController
public class ChessApplication extends SpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChessApplication.class, args);

		//testPawnBlockingCheck();
	}

	public static void testPawnBlockingCheck() {
		Chessboard testBoard = new Chessboard();
		Game game;
		String fen;
		ArrayList<Move> receivedMoves;

		// given
		fen = "4k3/8/8/8/1q6/8/2P5/4K3";
		testBoard.loadPositionFromFen(fen);
		game = new Game(testBoard, Game.GameStatus.WHITE_TO_MOVE);

		// when
		receivedMoves = game.getLegalMoves();

		// then
		assert(receivedMoves.size() != 0);

		// given
		fen = "rnbqk1nr/pppp1ppp/8/4p3/1b2P3/3P4/PPP2PPP/RNBQKBNR";
		testBoard.loadPositionFromFen(fen);
		game = new Game(testBoard, Game.GameStatus.WHITE_TO_MOVE);

		// when
		receivedMoves = game.getLegalMoves();

		// then
		System.out.println(testBoard);
		System.out.println(receivedMoves);
		assert(receivedMoves.size() != 0);
	}
}
