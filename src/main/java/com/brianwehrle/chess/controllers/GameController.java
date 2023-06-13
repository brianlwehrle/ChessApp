package com.brianwehrle.chess.controllers;

import com.brianwehrle.chess.models.Game;
import com.brianwehrle.chess.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1")
public class GameController {

    @Autowired
    @Qualifier("GameServiceImpl")
    private GameService gameService;

    @PostMapping("/newGame")
    public ResponseEntity<?> startNewGame() {
        String gameId = gameService.startNewGame().toString();
        return ResponseEntity.ok(gameId);
    }

    @GetMapping("{gameId}/getMoves")
    public ResponseEntity<?> getMoves(@PathVariable UUID gameId) {
        String legalMoves = gameService.getLegalMoves(gameId).toString();
        System.out.println(gameService.getLegalMoves(gameId).toString());
        return ResponseEntity.ok("Possible moves: " + legalMoves);
    }

    @PostMapping("{gameId}/makeMove")
    public ResponseEntity<?> makeMove(@RequestBody String move, @PathVariable UUID gameId) {
        Game.GameStatus tryMove = gameService.makeMove(gameId, move);

        if (tryMove == Game.GameStatus.INVALID_MOVE) {
            return ResponseEntity.badRequest().body(tryMove.toString());
        } else {
            return ResponseEntity.ok(tryMove.toString());
        }
    }
}
