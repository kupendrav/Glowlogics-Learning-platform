const THEME_KEY = "gl-theme";

export function initializeThemeToggle() {
  const button = document.getElementById("theme-toggle");
  const saved = localStorage.getItem(THEME_KEY);
  const initialTheme = saved === "dark" || saved === "light" ? saved : "light";
  setTheme(initialTheme);

  if (!button) {
    return;
  }

  updateThemeButton(button, initialTheme);
  button.addEventListener("click", () => {
    const active = document.documentElement.dataset.theme === "dark" ? "dark" : "light";
    const next = active === "dark" ? "light" : "dark";
    setTheme(next);
    updateThemeButton(button, next);
  });
}

function setTheme(theme) {
  document.documentElement.dataset.theme = theme;
  localStorage.setItem(THEME_KEY, theme);
}

function updateThemeButton(button, theme) {
  const dark = theme === "dark";
  button.setAttribute("aria-pressed", String(dark));
  button.setAttribute("aria-label", dark ? "Switch to light mode" : "Switch to dark mode");
}
