import React from "react";
import { useDroppable } from "@dnd-kit/core";

export default function Square({ id, darkSquare, children }) {
  const { setNodeRef } = useDroppable({
    id: id,
  });

  const color = darkSquare ? "dark" : "light";

  return (
    <div className={`square ${color} `} ref={setNodeRef}>
      {children}
    </div>
  );
}
