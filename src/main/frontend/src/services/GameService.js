import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

export const getNewGame = async () => {
  try {
    const response = await api.post("/api/v1/newGame");
    return response.data;
  } catch (error) {
    console.error("Error making api call in getNewGame:", error);
    throw error;
  }
};

export const positionRequest = async (gameId) => {
  try {
    const response = await api.get(`/api/v1/${gameId}/getPosition`);
    return response.data; // current position and list of moves
  } catch (error) {
    console.error("Error making api call in positionRequest:", error);
    throw error;
  }
};

export const sendMove = async (gameId, moveIndex) => {
  try {
    const response = await api.post(`/api/v1/${gameId}/makeMove/`, moveIndex);
    return response.data;
  } catch (error) {
    console.error("Error making api call in sendMove:", error);
    throw error;
  }
};
