package com.brianwehrle.chess.dtos;

import com.brianwehrle.chess.models.Move;

import java.util.ArrayList;

public record PositionDTO(String fenPosition, ArrayList<Move> legalMoves){}
