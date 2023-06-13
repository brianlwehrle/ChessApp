package com.brianwehrle.chess.services;

import com.brianwehrle.chess.models.Game;
import com.brianwehrle.chess.models.Move;
import java.util.List;

import java.util.UUID;

public interface GameService {

    public Game.GameStatus makeMove(UUID gameId, String move);

    UUID startNewGame();

    List<Move> getLegalMoves(UUID gameId);
}
