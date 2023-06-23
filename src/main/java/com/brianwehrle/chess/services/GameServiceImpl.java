package com.brianwehrle.chess.services;

import com.brianwehrle.chess.dtos.PositionDto;
import com.brianwehrle.chess.models.Game;
import com.brianwehrle.chess.models.Game.GameStatus;
import com.brianwehrle.chess.models.Move;
import com.brianwehrle.chess.models.Player;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Qualifier("GameServiceImpl")
@Service
public class GameServiceImpl implements GameService {

    private static Map<UUID, Game> games = new HashMap<>();

    @Override
    public UUID startNewGame() {
        UUID gameId = UUID.randomUUID();
        Game game = new Game(new Player("white"), new Player("black"), gameId);
        games.put(gameId, game);
        return gameId;
    }

    @Override
    public PositionDto getPosition(UUID gameId) {
        Game game = games.get(gameId);
        return new PositionDto(game.getFenPosition(), game.getLegalMoves());
    }

    @Override
    public GameStatus makeMove(UUID gameId, Move move) {
        return games.get(gameId).makeMove(move);
    }
}
