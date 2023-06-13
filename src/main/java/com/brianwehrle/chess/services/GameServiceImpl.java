package com.brianwehrle.chess.services;

import com.brianwehrle.chess.models.Game;
import com.brianwehrle.chess.models.Game.GameStatus;
import com.brianwehrle.chess.models.Move;
import com.brianwehrle.chess.models.Player;
import com.brianwehrle.chess.utilities.Converter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

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
    public List<Move> getLegalMoves(UUID gameId) {
        return games.get(gameId).getLegalMoves();
    }

    @Override
    public GameStatus makeMove(UUID gameId, String algebraicMove) {
        Game game = games.get(gameId);

        Move move  = Converter.algebraicToMove(algebraicMove);

        if (game.isLegalMove(move)) {
            game.makeMove(move);
            return game.getStatus();
        } else {
            return GameStatus.INVALID_MOVE;
        }
    }
}
