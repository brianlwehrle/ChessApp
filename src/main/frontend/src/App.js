import { useState, useEffect } from "react";

import "./style/App.css";
import Board from "./Components/Board";
import { getNewGame, positionRequest, sendMove } from "./services/GameService";

export default function App() {
  const [gameId, setGameId] = useState("");
  const [legalMoves, setLegalMoves] = useState([]);
  const [currentFenPosition, setCurrentFenPosition] = useState("");

  useEffect(() => {
    if (gameId) {
      getCurrentPosition();
    }
  }, [gameId, currentFenPosition]);

  const newGame = () => {
    getNewGame()
      .then((response) => {
        const newGameId = response;
        setGameId(newGameId);
        console.log(`Started new game with id ${newGameId}`);
      })
      .catch(() => {
        console.error(
          "Could not start new game due to bad api request. Is the server on?"
        );
      });
  };

  const getCurrentPosition = () => {
    positionRequest(gameId)
      .then((response) => {
        const movesData = response;
        setLegalMoves(movesData["legalMoves"]);
        setCurrentFenPosition(movesData["fenPosition"]);
        console.log("successfully fetched moves", currentFenPosition);
      })
      .catch(() => {
        console.error("Could not get current position");
      });
  };

  const executeMove = (moveIndex) => {
    sendMove(gameId, moveIndex)
      .then((response) => {
        console.log(`made move with status ${response}`);
        getCurrentPosition(gameId);
      })
      .catch(() => {
        console.error("Could not execute move");
      });
  };

  return (
    <div id="App">
      <div id="board-container">
        <Board
          legalMoves={legalMoves}
          executeMove={executeMove}
          fenString={currentFenPosition}
        />
      </div>
      <p style={{ color: "red" }}>Game ID: {gameId}</p>
      <button style={{ color: "red" }} onClick={newGame}>
        Start New Game
      </button>
    </div>
  );
}
