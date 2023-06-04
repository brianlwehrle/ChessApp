package com.brianwehrle.chess.models;

public class Player {
    private Color color;
    private String name;
    // canCastle[0] represents Queen side, [1] King side
    private final boolean[] canCastle;
    private String enPassantSquare;

    public Player(String name) {
        this.name = name;
        canCastle = new boolean[]{true, true};
        enPassantSquare = "-";
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    // should only ever be switching to false
    public void setCastle(boolean queenSide, boolean kingSide) {
        if (!queenSide) canCastle[0] = false;
        if (!kingSide) canCastle[1] = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // 0 = long, 1 = short
    public boolean canCastle(String side) {
        if (side.equals("Long")) return canCastle[0];
        else if (side.equals("Short")) return canCastle[1];
        else {
            System.out.println("Invalid calling of canCastle");
            System.exit(1);
        }
        return false;
    }

    public String getEnPassantSquare() {
        return enPassantSquare;
    }

    public void setEnPassantSquare(String enPassantSquare) {
        this.enPassantSquare = enPassantSquare;
    }
}
