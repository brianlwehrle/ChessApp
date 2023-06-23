import React from "react";
import { useDroppable } from "@dnd-kit/core";

export default function Square({ id, darkSquare, children }) {
  const { isOver, setNodeRef } = useDroppable({
    id: id,
  });

  const style = {
    border: isOver ? "4px solid #bfc0aa" : undefined,
  };

  const color = darkSquare ? "dark" : "light";

  return (
    <div style={style} id={id} className={`square ${color} `} ref={setNodeRef}>
      {children}
    </div>
  );
}
