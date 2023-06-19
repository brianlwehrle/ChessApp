import React from "react";

export default function Piece({ piece }) {
  const imagePath = `../images/${piece}.png`;
  return (
    <div id="piece">
      <img src={imagePath} />
    </div>
  );
}
