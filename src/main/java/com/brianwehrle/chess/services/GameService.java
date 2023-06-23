package com.brianwehrle.chess.services;

import com.brianwehrle.chess.dtos.PositionDto;
import com.brianwehrle.chess.models.Game;
import com.brianwehrle.chess.models.Move;

import java.util.UUID;

public interface GameService {

    Game.GameStatus makeMove(UUID gameId, Move move);

    UUID startNewGame();

    PositionDto getPosition(UUID gameId);
}
