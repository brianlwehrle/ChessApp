package com.brianwehrle.chess.controllers;

import com.brianwehrle.chess.dtos.PositionDTO;
import com.brianwehrle.chess.models.Game;
import com.brianwehrle.chess.models.Move;
import com.brianwehrle.chess.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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

    @GetMapping("{gameId}/getPosition")
    public PositionDTO getPosition(@PathVariable UUID gameId) {
        return gameService.getPosition(gameId);
    }

    @PostMapping("{gameId}/makeMove/") // TODO see why DTO didn't work here for moveIndex
    public ResponseEntity<?> makeMove(@RequestBody Map<String, Integer> moveIndex, @PathVariable UUID gameId) {

        Game.GameStatus tryMove = gameService.makeMove(gameId, moveIndex.get("moveIndex"));

        if (tryMove == Game.GameStatus.INVALID_MOVE) {
            return ResponseEntity.badRequest().body(tryMove.toString());
        } else {
            return ResponseEntity.ok(tryMove.toString());
        }
    }
}
