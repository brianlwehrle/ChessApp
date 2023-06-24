import React, { useState, useEffect } from "react";
import { DndContext, rectIntersection } from "@dnd-kit/core";

import { snapCenterToCursor } from "@dnd-kit/modifiers";

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
          newRow.push("bR");
          break;
        case "n":
          newRow.push("bN");
          break;
        case "b":
          newRow.push("bB");
          break;
        case "q":
          newRow.push("bQ");
          break;
        case "k":
          newRow.push("bK");
          break;
        case "p":
          newRow.push("bP");
          break;
        case "R":
          newRow.push("wR");
          break;
        case "N":
          newRow.push("wN");
          break;
        case "B":
          newRow.push("wB");
          break;
        case "Q":
          newRow.push("wQ");
          break;
        case "K":
          newRow.push("wK");
          break;
        case "P":
          newRow.push("wP");
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
    for (const legalMove of legalMoves) {
      if (
        // this is where the coordinate conversion happens
        legalMove.startRow === Math.abs((startRow - 7) % 8) &&
        legalMove.startCol === Number(startCol) &&
        legalMove.endRow === Math.abs((endRow - 7) % 8) &&
        legalMove.endCol === Number(endCol)
      ) {
        return legalMove;
      }
    }
  }

  const handleDrop = (startRow, startCol, endRow, endCol) => {
    if (startRow === endRow && startCol === endCol) return;

    let move = viableMove(startRow, startCol, endRow, endCol);
    if (move === null) {
      console.log("Invalid move");
    } else {
      executeMove(move);
    }
  };

  const renderSquare = (x, y) => {
    const darkSquare = (x + y) % 2 === 0;
    const piece = currentBoard[x][y];
    const pieceImage = piece ? <Piece id={`${x}, ${y}`} piece={piece} /> : null;

    //TODO: change id to ${x}${y}
    return (
      <div key={`${x}, ${y}`} style={{ width: "12.5%", height: "12.5%" }}>
        <Square id={`${x}, ${y}`} darkSquare={darkSquare}>
          {pieceImage}
        </Square>
      </div>
    );
  };

  return (
    <DndContext
      modifiers={[snapCenterToCursor]}
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
