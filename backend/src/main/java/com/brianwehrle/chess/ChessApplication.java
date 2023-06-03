package com.brianwehrle.chess;

import com.brianwehrle.chess.models.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ChessApplication {

	public static void main(String[] args) {
		Player player1 = new Player("Player1");
		Player player2 = new Player("Player2");
		Game game = new Game();

		game.run();

		//SpringApplication.run(ChessApplication.class, args);
	}

	@GetMapping("/")
	public String apiRoot() {
		return "Test";
	}

}
