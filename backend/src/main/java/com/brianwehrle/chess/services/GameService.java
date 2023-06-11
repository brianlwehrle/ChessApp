package com.brianwehrle.chess.services;

import com.brianwehrle.chess.models.Game;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public interface GameService {

    public String getFen();

    public Game.GameStatus makeMove(String move);

    public UUID getGameId();
}
