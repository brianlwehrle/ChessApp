package com.brianwehrle.chess.controllers;

import com.brianwehrle.chess.dtos.MoveDto;
import com.brianwehrle.chess.dtos.PositionDto;
import com.brianwehrle.chess.models.Game;
import com.brianwehrle.chess.models.Move;
import com.brianwehrle.chess.services.GameService;
import org.modelmapper.ModelMapper;
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
    private ModelMapper modelMapper;

    @Autowired
    @Qualifier("GameServiceImpl")
    private GameService gameService;

    @PostMapping("/newGame")
    public ResponseEntity<?> startNewGame() {
        String gameId = gameService.startNewGame().toString();
        return ResponseEntity.ok(gameId);
    }

    @GetMapping("{gameId}/getPosition")
    public PositionDto getPosition(@PathVariable UUID gameId) {
        return gameService.getPosition(gameId);
    }

    @PostMapping("{gameId}/makeMove/")
    public ResponseEntity<?> makeMove(@RequestBody MoveDto moveDTO, @PathVariable UUID gameId) {
        Move move = modelMapper.map(moveDTO, Move.class);

        Game.GameStatus tryMove = gameService.makeMove(gameId, move);

        if (tryMove == Game.GameStatus.INVALID_MOVE) {
            return ResponseEntity.badRequest().body(tryMove);
        } else {
            return ResponseEntity.ok(tryMove);
        }
    }
}
