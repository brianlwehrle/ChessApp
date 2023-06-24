import { useState, useEffect } from "react";

import Board from "./Components/Board";
import { getNewGame, positionRequest, sendMove } from "./services/GameService";

export default function App() {
  const [gameId, setGameId] = useState("");
  const [legalMoves, setLegalMoves] = useState([]);
  const [currentFenPosition, setCurrentFenPosition] = useState("");
  const [status, setStatus] = useState("");

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
        setStatus("WHITE_TO_MOVE");
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

  const executeMove = (move) => {
    sendMove(gameId, move)
      .then((response) => {
        setStatus(response);
        console.log(`made move with status ${response}`);
        getCurrentPosition(gameId);
      })
      .catch(() => {
        console.error("Could not execute move");
      });
  };

  return (
    <div id="App">
      <div id="chatbox">Chat goes here</div>
      <div id="white-player">White Player</div>
      <div id="black-player">Black Player</div>
      <div id="status">Current Game Status: {status}</div>
      <Board
        legalMoves={legalMoves}
        executeMove={executeMove}
        fenString={currentFenPosition}
      />
      <button className="button" id="new-game-button" onClick={newGame}>
        New Game
      </button>
    </div>
  );
}
