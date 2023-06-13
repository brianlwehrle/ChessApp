import React, { useState } from "react";
import "./style/App.css";
import Board from "./components/Board";
import GameService from "./services/GameService";

function App() {
  const [gameId, setGameId] = useState("");

  const newGameHandler = () => {
    GameService.startNewGame().then((res) => {
      console.log("Started game with gameId: ", res.data);
      setGameId(res.data);
    })
      .catch(error => {
        console.error("Error starting new game: ", error);
      });
  };

  const getMovesHandler = () => {
    GameService.getMoves(gameId).then((res) => {
      console.log("Moves", res.data);
    })
      .catch(error => {
        console.error("Error retrieving moves: ", error);
      });
  }

  return (
    <div id="app">
      <p style={{ color: 'red' }}>Game ID: {gameId}</p>
      <Board />
      <button onClick={newGameHandler}>Start New Game</button>
      <button onClick={getMovesHandler}>Get Moves</button>
    </div>
  );
}

export default App;
