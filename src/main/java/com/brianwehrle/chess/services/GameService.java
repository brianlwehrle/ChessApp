package com.brianwehrle.chess.services;

import com.brianwehrle.chess.dtos.PositionDTO;
import com.brianwehrle.chess.models.Game;
import com.brianwehrle.chess.models.Move;
import java.util.List;

import java.util.UUID;

public interface GameService {

    Game.GameStatus makeMove(UUID gameId, int moveIndex);

    UUID startNewGame();

    PositionDTO getPosition(UUID gameId);
}
