import axios from 'axios';

const api = axios.create({
    baseURL: "http://localhost:8080"
})

class GameService {
    startNewGame() {
        return api.post("/api/v1/newGame");
    }

    getMoves(gameId: string) {
        return api.get(`/api/v1/${gameId}/getMoves`);
    }

    makeMove(gameId: string, move: string) {
        return api.post(`/api/v1/${gameId}/getMoves`, move);
    }
}

export default new GameService()
