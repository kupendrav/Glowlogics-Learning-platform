import { initializeThemeToggle } from "./theme.js";
import { initializeCursor } from "./cursor.js";
import {
  getCourseProgressMap,
  markEnrolled,
  isEnrolled,
  setLessonCompletion
} from "./storage-sync.js";

initializeThemeToggle();
initializeCursor();
initializeAnimations();
initializeMobileNav();
initializeLearnerMenu();
initializeBlogComposer();
initializeCourseDetailPage();

function initializeAnimations() {
  const elements = document.querySelectorAll(".reveal");
  if (elements.length === 0) {
    return;
  }

  const reducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  if (reducedMotion || typeof window.IntersectionObserver === "undefined") {
    elements.forEach((element) => element.classList.add("revealed"));
    return;
  }

  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add("revealed");
          observer.unobserve(entry.target);
        }
      });
    },
    { rootMargin: "0px 0px -10% 0px", threshold: 0.1 }
  );

  elements.forEach((element) => observer.observe(element));
}

function initializeMobileNav() {
  const menuToggle = document.getElementById("menu-toggle");
  const nav = document.getElementById("primary-nav");
  if (!menuToggle || !nav) {
    return;
  }

  menuToggle.addEventListener("click", () => {
    nav.classList.toggle("open");
    const expanded = nav.classList.contains("open");
    menuToggle.setAttribute("aria-expanded", String(expanded));
  });
}

function initializeLearnerMenu() {
  const toggle = document.getElementById("learner-menu-toggle");
  const menu = document.getElementById("learner-menu-list");

  if (!toggle || !menu) {
    return;
  }

  const closeMenu = () => {
    menu.classList.remove("open");
    toggle.setAttribute("aria-expanded", "false");
  };

  toggle.addEventListener("click", (event) => {
    event.stopPropagation();
    const nextOpenState = !menu.classList.contains("open");
    menu.classList.toggle("open", nextOpenState);
    toggle.setAttribute("aria-expanded", String(nextOpenState));
  });

  menu.addEventListener("click", (event) => {
    event.stopPropagation();
  });

  document.addEventListener("click", closeMenu);
  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
      closeMenu();
    }
  });
}

function initializeBlogComposer() {
  if (document.body.dataset.page !== "blog-write") {
    return;
  }

  const editor = document.getElementById("blog-editor");
  const preview = document.getElementById("blog-preview");
  const counter = document.getElementById("editor-count");
  const actions = Array.from(document.querySelectorAll("[data-editor-action]"));

  if (!editor || !preview || !counter || actions.length === 0) {
    return;
  }

  const updatePreview = () => {
    const text = editor.value || "";
    counter.textContent = `${text.length} / 12000`;
    preview.innerHTML = renderMarkdownPreview(text);
  };

  actions.forEach((button) => {
    button.addEventListener("click", () => {
      const action = button.dataset.editorAction;

      if (action === "h2") {
        insertSnippetAtCursor(editor, "\n## Heading\n");
      }
      if (action === "h3") {
        insertSnippetAtCursor(editor, "\n### Subheading\n");
      }
      if (action === "image") {
        const imageUrl = window.prompt("Image URL (https://...)", "");
        if (!imageUrl) {
          return;
        }
        const altText = window.prompt("Image alt text", "Blog image") || "Blog image";
        insertSnippetAtCursor(editor, `\n![${altText}](${imageUrl})\n`);
      }

      updatePreview();
      editor.focus();
    });
  });

  editor.addEventListener("input", updatePreview);
  updatePreview();
}

function insertSnippetAtCursor(textarea, snippet) {
  const start = textarea.selectionStart ?? textarea.value.length;
  const end = textarea.selectionEnd ?? textarea.value.length;
  const before = textarea.value.slice(0, start);
  const after = textarea.value.slice(end);

  textarea.value = `${before}${snippet}${after}`;
  const nextCursor = start + snippet.length;
  textarea.setSelectionRange(nextCursor, nextCursor);
}

function renderMarkdownPreview(content) {
  const lines = String(content || "").split(/\r?\n/);
  const imagePattern = /^!\[(.*?)]\((https?:\/\/[^)\s]+)\)$/;
  const htmlParts = [];
  let paragraph = [];

  const flushParagraph = () => {
    if (paragraph.length === 0) {
      return;
    }
    htmlParts.push(`<p>${paragraph.join("<br/>")}</p>`);
    paragraph = [];
  };

  lines.forEach((line) => {
    const trimmed = line.trim();

    if (!trimmed) {
      flushParagraph();
      return;
    }

    const imageMatch = trimmed.match(imagePattern);
    if (imageMatch) {
      flushParagraph();
      const alt = escapeHtml(imageMatch[1]);
      const src = escapeHtml(imageMatch[2]);
      htmlParts.push(`<figure class=\"blog-inline-image\"><img src=\"${src}\" alt=\"${alt}\" loading=\"lazy\"/></figure>`);
      return;
    }

    if (trimmed.startsWith("## ")) {
      flushParagraph();
      htmlParts.push(`<h2>${escapeHtml(trimmed.slice(3).trim())}</h2>`);
      return;
    }

    if (trimmed.startsWith("### ")) {
      flushParagraph();
      htmlParts.push(`<h3>${escapeHtml(trimmed.slice(4).trim())}</h3>`);
      return;
    }

    paragraph.push(escapeHtml(trimmed));
  });

  flushParagraph();
  return htmlParts.join("") || "<p>Preview will appear here.</p>";
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function initializeCourseDetailPage() {
  if (document.body.dataset.page !== "course-detail") {
    return;
  }

  const root = document.querySelector("main[data-course-id]");
  if (!root) {
    return;
  }

  const enrollButton = document.getElementById("enroll-btn");
  const lessonChecks = Array.from(document.querySelectorAll(".lesson-check"));
  const lessonItems = Array.from(document.querySelectorAll(".lesson-item"));
  const authenticated = root.dataset.authenticated === "true";

  if (!enrollButton || lessonChecks.length === 0) {
    return;
  }

  initializeLessonNavigator(lessonItems);

  const learner = readIdentity();
  const courseId = Number(root.dataset.courseId);
  const messageBox = document.getElementById("course-message");
  const progressFill = document.getElementById("progress-fill");
  const progressLabel = document.getElementById("progress-label");
  const certificateLink = document.getElementById("certificate-link");

  hydrateProgressFromLocal(learner, courseId, lessonChecks, progressFill, progressLabel, certificateLink);

  if (!authenticated) {
    setMessage(messageBox, "Login to start enrollment and progress sync.", true);
    return;
  }

  reconcileServerWithLocal(courseId, messageBox, progressFill, progressLabel, certificateLink);

  if (isEnrolled(learner, courseId)) {
    enrollButton.textContent = "Enrolled";
    enrollButton.classList.add("is-enrolled");
  }

  enrollButton.addEventListener("click", async () => {
    enrollButton.disabled = true;
    try {
      await fetchJson("/api/enrollments", { courseId });
      markEnrolled(learner, courseId, true);
      enrollButton.textContent = "Enrolled";
      enrollButton.classList.add("is-enrolled");
      setMessage(messageBox, "Enrollment successful.", false);
      await reconcileServerWithLocal(courseId, messageBox, progressFill, progressLabel, certificateLink);
    } catch (error) {
      setMessage(messageBox, error.message, true);
    } finally {
      enrollButton.disabled = false;
    }
  });

  lessonChecks.forEach((checkbox) => {
    checkbox.addEventListener("change", async () => {
      const lessonId = Number(checkbox.dataset.lessonId);
      const completed = checkbox.checked;

      checkbox.disabled = true;
      try {
        const response = await fetchJson("/api/progress", {
          courseId,
          lessonId,
          completed
        });

        setLessonCompletion(learner, courseId, lessonId, completed);
        syncProgressUI(response.progressPercent, progressFill, progressLabel, certificateLink);
        setMessage(messageBox, completed ? "Lesson marked complete." : "Lesson marked incomplete.", false);
      } catch (error) {
        checkbox.checked = !completed;
        setMessage(messageBox, error.message, true);
      } finally {
        checkbox.disabled = false;
      }
    });
  });
}

function initializeLessonNavigator(lessonItems) {
  const lessonList = document.querySelector(".lesson-list");
  const previousButton = document.getElementById("lesson-prev");
  const nextButton = document.getElementById("lesson-next");
  const positionLabel = document.getElementById("lesson-position");

  if (!lessonList || !previousButton || !nextButton || !positionLabel || lessonItems.length === 0) {
    return;
  }

  let activeIndex = 0;
  lessonList.classList.add("is-slider-ready");

  const render = () => {
    lessonItems.forEach((item, index) => {
      const isActive = index === activeIndex;
      item.classList.toggle("is-active", isActive);
      item.setAttribute("aria-hidden", String(!isActive));
    });

    positionLabel.textContent = `${activeIndex + 1} / ${lessonItems.length}`;
    previousButton.disabled = activeIndex === 0;
    nextButton.disabled = activeIndex === lessonItems.length - 1;
  };

  previousButton.addEventListener("click", () => {
    if (activeIndex === 0) {
      return;
    }
    activeIndex -= 1;
    render();
  });

  nextButton.addEventListener("click", () => {
    if (activeIndex >= lessonItems.length - 1) {
      return;
    }
    activeIndex += 1;
    render();
  });

  render();
}

function readIdentity() {
  const identity = document.querySelector(".identity-chip");
  if (!identity) {
    return "guest";
  }

  const text = identity.textContent || "guest";
  return text.replace("@", "").trim().toLowerCase();
}

function hydrateProgressFromLocal(learner, courseId, lessonChecks, progressFill, progressLabel, certificateLink) {
  const localState = getCourseProgressMap(learner, courseId);

  lessonChecks.forEach((checkbox) => {
    checkbox.checked = Boolean(localState[checkbox.dataset.lessonId]);
  });

  const percent = calculatePercent(lessonChecks);
  syncProgressUI(percent, progressFill, progressLabel, certificateLink);
}

async function reconcileServerWithLocal(courseId, messageBox, progressFill, progressLabel, certificateLink) {
  const learner = readIdentity();
  const lessonChecks = Array.from(document.querySelectorAll(".lesson-check"));

  const completedByLessonId = lessonChecks.reduce((result, checkbox) => {
    result[checkbox.dataset.lessonId] = checkbox.checked;
    return result;
  }, {});

  try {
    const response = await fetchJson("/api/sync", {
      courseId,
      completedByLessonId
    });

    syncProgressUI(response.progressPercent, progressFill, progressLabel, certificateLink);
    markEnrolled(learner, courseId, true);
  } catch (error) {
    const message = String(error?.message || "").toLowerCase();
    if (message.includes("enroll first")) {
      setMessage(messageBox, "Enroll to enable server-side progress sync.", false);
      return;
    }
    setMessage(messageBox, "Using local progress while server sync is unavailable.", true);
  }
}

function calculatePercent(lessonChecks) {
  if (lessonChecks.length === 0) {
    return 0;
  }
  const completedCount = lessonChecks.filter((checkbox) => checkbox.checked).length;
  return Math.round((completedCount * 100) / lessonChecks.length);
}

function syncProgressUI(percent, progressFill, progressLabel, certificateLink) {
  if (!progressFill || !progressLabel) {
    return;
  }

  progressFill.style.width = `${percent}%`;
  progressLabel.textContent = `Progress: ${percent}%`;

  if (certificateLink) {
    certificateLink.style.display = percent === 100 ? "inline-flex" : "none";
  }
}

function setMessage(messageBox, text, isError) {
  if (!messageBox) {
    return;
  }
  if (!text) {
    messageBox.style.display = "none";
    messageBox.textContent = "";
    return;
  }

  messageBox.style.display = "block";
  messageBox.textContent = text;
  messageBox.classList.toggle("error", isError);
  messageBox.classList.toggle("success", !isError);
}

async function fetchJson(url, body) {
  const response = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });

  const payload = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(payload.message || "Request failed");
  }
  return payload;
}
