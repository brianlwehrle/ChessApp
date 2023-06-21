package com.brianwehrle.chess.services;

import com.brianwehrle.chess.dtos.PositionDTO;
import com.brianwehrle.chess.models.Game;
import com.brianwehrle.chess.models.Game.GameStatus;
import com.brianwehrle.chess.models.Move;
import com.brianwehrle.chess.models.Player;
import com.brianwehrle.chess.utilities.Converter;
import com.fasterxml.jackson.core.JsonParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.json.JsonParserFactory;
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
    public PositionDTO getPosition(UUID gameId) {
        Game game = games.get(gameId);
        return new PositionDTO(game.getFenPosition(), game.getLegalMoves());
    }

    @Override // todo probably should just transfer an actual move object rather than position in the list
    public GameStatus makeMove(UUID gameId, int moveIndex) {
        Game game = games.get(gameId);
        List<Move> legalMoves = game.getLegalMoves();

        Move move;

        try {
            move = legalMoves.get(moveIndex);
        } catch (IndexOutOfBoundsException e) {
            move = null;
        }

        return game.makeMove(move);
    }
}
