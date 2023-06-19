import React from "react";

import "../style/Square.css";

export default function Square({ darkSquare, children, isSelected }) {
  const color = darkSquare ? "dark" : "light";
  const squareClasses = `square ${color} ${isSelected ? "selected" : ""}`;

  return <div className={squareClasses}>{children}</div>;
}
