import React, { useState, useEffect } from "react";
import Square from "./Square";
import Piece from "./Piece";
import "../style/Board.css";

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
  const [selectedPiece, setSelectedPiece] = useState(null);
  const [piecePosition, setPiecePosition] = useState({ x: null, y: null });

  useEffect(() => {
    // Whenever the `fenString` prop changes, update the `currentBoard` state
    if (fenString) {
      setCurrentBoard(fenToBoard(fenString));
    }
  }, [fenString]);

  function viableMove(startX, startY, endX, endY) {
    let moveIndex = 0;
    for (const legalMove of legalMoves) {
      if (
        legalMove.initialRow === Math.abs((startX - 7) % 8) &&
        legalMove.initialCol === startY &&
        legalMove.finalRow === Math.abs((endX - 7) % 8) &&
        legalMove.finalCol === endY
      ) {
        return moveIndex;
      }
      moveIndex++;
    }

    return false;
  }

  const handleSquareClick = (x, y) => {
    if (selectedPiece && (piecePosition.x !== x || piecePosition.y !== y)) {
      let moveIndex = viableMove(piecePosition.x, piecePosition.y, x, y);
      if (moveIndex) {
        // send the move to the server
        executeMove({ moveIndex });
      } else {
        // give bad move feedback
        console.log("Invalid move");
      }

      // reset the selected piece and piece position
      setSelectedPiece(null);
      setPiecePosition({ x: null, y: null });
    } else {
      const piece = currentBoard[x][y];

      if (piece) {
        setSelectedPiece(piece);
        setPiecePosition({ x, y });
      }
    }
  };

  const renderSquare = (x, y) => {
    const darkSquare = (x + y) % 2 === 1;
    const isSelected = piecePosition.x === x && piecePosition.y === y;
    const piece = currentBoard[x][y];
    const pieceImage = piece ? <Piece piece={piece} /> : null;

    return (
      <div
        key={`${x}-${y}`}
        style={{ width: "12.5%", height: "12.5%" }}
        onClick={() => handleSquareClick(x, y)}
      >
        <Square darkSquare={darkSquare} isSelected={isSelected}>
          {pieceImage}
        </Square>
      </div>
    );
  };

  return (
    <div id="board">
      {Array.from({ length: 8 }, (_, x) =>
        Array.from({ length: 8 }, (_, y) => renderSquare(x, y))
      )}
    </div>
  );
}
