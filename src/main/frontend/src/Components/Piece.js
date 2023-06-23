import React from "react";

import { useDraggable } from "@dnd-kit/core";
import { CSS } from "@dnd-kit/utilities";

export default function Piece({ id, piece }) {
  const { attributes, listeners, setNodeRef, transform } = useDraggable({
    id: id,
  });

  const style = {
    transform: CSS.Translate.toString(transform),
  };

  const imagePath = `../images/lichess_alpha/${piece}.svg`;

  return (
    <img
      className="piece"
      src={imagePath}
      ref={setNodeRef}
      style={style}
      {...listeners}
      {...attributes}
    />
  );
}
