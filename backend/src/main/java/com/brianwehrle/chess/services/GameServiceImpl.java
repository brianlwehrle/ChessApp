package com.brianwehrle.chess.services;

import com.brianwehrle.chess.models.Game;
import com.brianwehrle.chess.models.Game.GameStatus;
import com.brianwehrle.chess.models.Move;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Qualifier("GameServiceImpl")
@Service
public class GameServiceImpl implements GameService {

    private Game game;

    public void startNewGame() {

    }
    @Override
    public String getFen() {
        return game.getFen();
    }

    @Override
    public GameStatus makeMove(String notationMove) {
        Move move  = Game.convertToMove(notationMove);

        if (game.isLegalMove(move)) {
            game.makeMove(move);
            return game.getStatus();
        } else {
            return GameStatus.INVALID_MOVE;
        }
    }

    @Override
    public UUID getGameId() {
        return game.getGameId();
    }
}
