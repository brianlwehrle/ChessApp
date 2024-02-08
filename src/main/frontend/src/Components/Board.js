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
  const [showPromotionPopup, setShowPromotionPopup] = useState(false);
  const [selectedMove, setSelectedMove] = useState(null);

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

  const handlePromotionSelection = (promotionType) => {
    // Update the move type of selectedMove
    const updatedMove = { ...selectedMove, moveType: promotionType };
    setShowPromotionPopup(false);

    executeMove(updatedMove);
  };

  const handleDrop = (startRow, startCol, endRow, endCol) => {
    let move = viableMove(startRow, startCol, endRow, endCol);

    const promotions = [
      "PROMOTION_KNIGHT",
      "PROMOTION_BISHOP",
      "PROMOTION_ROOK",
      "PROMOTION_QUEEN",
    ];

    if (!move) {
      console.log("Invalid move");
    } else if (promotions.includes(move.moveType)) {
      setShowPromotionPopup(true);

      setSelectedMove(move);
    } else {
      // Update the board state immediately
      const updatedBoard = [...currentBoard];
      const piece = updatedBoard[startRow][startCol];
      updatedBoard[startRow][startCol] = null;
      updatedBoard[endRow][endCol] = piece;
      setCurrentBoard(updatedBoard);

      executeMove(move);
    }
  };

  const renderSquare = (x, y) => {
    const darkSquare = (x + y) % 2 === 0;
    const piece = currentBoard[x][y];
    const pieceImage = piece ? <Piece id={`${x}${y}`} piece={piece} /> : null;

    return (
      <div key={`${x}${y}`} style={{ width: "12.5%", height: "12.5%" }}>
        <Square id={`${x}${y}`} darkSquare={darkSquare}>
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
          e.active.id.charAt(1),
          e.over.id.charAt(0),
          e.over.id.charAt(1)
        );
      }}
    >
      <div id="board">
        {Array.from({ length: 8 }, (_, x) =>
          Array.from({ length: 8 }, (_, y) => renderSquare(x, y))
        )}
        {showPromotionPopup && (
          <PromotionPopup onSelect={handlePromotionSelection} />
        )}
      </div>
    </DndContext>
  );
}

function PromotionPopup({ onSelect }) {
  const handlePromotionClick = (promotionType) => {
    onSelect(promotionType);
  };

  return (
    <div className="promotion-popup">
      <button
        className="promotion-button"
        onClick={() => handlePromotionClick("PROMOTION_KNIGHT")}
      >
        <img src="../images/lichess_alpha/wN.svg" className="promotion-img" />
      </button>
      <button
        className="promotion-button"
        onClick={() => handlePromotionClick("PROMOTION_BISHOP")}
      >
        <img src="../images/lichess_alpha/wB.svg" className="promotion-img" />
      </button>
      <button
        className="promotion-button"
        onClick={() => handlePromotionClick("PROMOTION_ROOK")}
      >
        <img src="../images/lichess_alpha/wR.svg" className="promotion-img" />
      </button>
      <button
        className="promotion-button"
        onClick={() => handlePromotionClick("PROMOTION_QUEEN")}
      >
        <img src="../images/lichess_alpha/wQ.svg" className="promotion-img" />
      </button>
    </div>
  );
}
