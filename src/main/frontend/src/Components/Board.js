import React, { useState, useEffect } from "react";
import { DndContext, rectIntersection } from "@dnd-kit/core";

import Square from "./Square";
import Piece from "./Piece";
import "../App.css";

function fenToBoard(fenString) {
  let boardState = [];

  let newRow = [];
  for (let i = 0; i < fenString.length; i++) {
    let character = fenString.charAt(i);

    if (character === "/") {
      boardState.push(newRow);
      newRow = [];
    } else if (!isNaN(character)) {
      let num = Number.parseInt(character);
      while (num > 0) {
        newRow.push(null);
        num--;
      }
    } else {
      switch (character) {
        case "r":
          newRow.push("rook_b");
          break;
        case "n":
          newRow.push("knight_b");
          break;
        case "b":
          newRow.push("bishop_b");
          break;
        case "q":
          newRow.push("queen_b");
          break;
        case "k":
          newRow.push("king_b");
          break;
        case "p":
          newRow.push("pawn_b");
          break;
        case "R":
          newRow.push("rook_w");
          break;
        case "N":
          newRow.push("knight_w");
          break;
        case "B":
          newRow.push("bishop_w");
          break;
        case "Q":
          newRow.push("queen_w");
          break;
        case "K":
          newRow.push("king_w");
          break;
        case "P":
          newRow.push("pawn_w");
          break;
        default:
          console.log(`Unrecognized fen String: ${fenString}`);
      }
    }
  }

  return boardState;
}

export default function Board({ legalMoves, executeMove, fenString }) {
  const [currentBoard, setCurrentBoard] = useState(
    fenToBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/")
  );

  useEffect(() => {
    // Whenever the `fenString` prop changes, update the `currentBoard` state
    if (fenString) {
      setCurrentBoard(fenToBoard(fenString));
    }
  }, [fenString]);

  function viableMove(startRow, startCol, endRow, endCol) {
    let moveIndex = 0;
    for (const legalMove of legalMoves) {
      if (
        // this is where the coordinate conversion happens
        legalMove.startRow === Math.abs((startRow - 7) % 8) &&
        legalMove.startCol === Number(startCol) &&
        legalMove.endRow === Math.abs((endRow - 7) % 8) &&
        legalMove.endCol === Number(endCol)
      ) {
        return moveIndex;
      }
      moveIndex++;
    }

    return false;
  }

  const handleDrop = (startRow, startCol, endRow, endCol) => {
    let moveIndex = viableMove(startRow, startCol, endRow, endCol);
    if (moveIndex) {
      // send the move to the server
      executeMove({ moveIndex });
    } else {
      // give bad move feedback
      console.log("Invalid move");
    }
  };

  const renderSquare = (x, y) => {
    const darkSquare = (x + y) % 2 === 0;
    const piece = currentBoard[x][y];
    const pieceImage = piece ? <Piece id={`${x}, ${y}`} piece={piece} /> : null;

    return (
      <div
        key={`${x}, ${y}`}
        style={{ width: "12.5%", height: "12.5%" }}
        // onClick={() => handleSquareClick(x, y)}
      >
        <Square id={`${x}, ${y}`} darkSquare={darkSquare}>
          {pieceImage}
        </Square>
      </div>
    );
  };

  return (
    <DndContext
      collisionDetection={rectIntersection}
      onDragEnd={(e) => {
        handleDrop(
          e.active.id.charAt(0),
          e.active.id.charAt(3),
          e.over.id.charAt(0),
          e.over.id.charAt(3)
        );
      }}
    >
      <div id="board">
        {Array.from({ length: 8 }, (_, x) =>
          Array.from({ length: 8 }, (_, y) => renderSquare(x, y))
        )}
      </div>
    </DndContext>
  );
}
