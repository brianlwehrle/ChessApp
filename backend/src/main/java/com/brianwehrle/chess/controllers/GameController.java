package com.brianwehrle.chess.controllers;

import com.brianwehrle.chess.models.Game;
import com.brianwehrle.chess.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/game")
public class GameController {

    @Autowired
    @Qualifier("GameServiceImpl")
    private GameService gameService;

    @GetMapping("/getFen")
    public ResponseEntity<String> getFen() {
        return new ResponseEntity<>(gameService.getFen(), HttpStatus.OK);
    }

    @PostMapping("/makeMove/{move}")
    public ResponseEntity<?> makeMove(@RequestBody String move) {
        Game.GameStatus tryMove = gameService.makeMove(move);

        if (tryMove == Game.GameStatus.INVALID_MOVE) {
            String errorMessage = "Invalid move. Please try again.";
            return ResponseEntity.badRequest().body(errorMessage);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }



}
