export function initializeCursor() {
  const isTouch = window.matchMedia("(pointer: coarse)").matches;
  const reducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  if (isTouch || reducedMotion) {
    return;
  }

  const dot = document.createElement("div");
  dot.className = "cursor-dot";
  document.body.appendChild(dot);

  document.addEventListener("mousemove", (event) => {
    dot.style.transform = `translate(${event.clientX}px, ${event.clientY}px)`;
  });
}
