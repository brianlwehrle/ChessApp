package com.brianwehrle.chess;

import com.brianwehrle.chess.models.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ChessApplication extends SpringApplication {
	public static void main(String[] args) {
		SpringApplication.run(ChessApplication.class, args);
	}
}
