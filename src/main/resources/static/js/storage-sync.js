const ENROLLMENTS_KEY = "gl-enrollments";
const PROGRESS_KEY = "gl-progress";

export function getEnrollmentState() {
  return parseObject(readStorage(ENROLLMENTS_KEY));
}

export function setEnrollmentState(value) {
  writeStorage(ENROLLMENTS_KEY, value);
}

export function getProgressState() {
  return parseObject(readStorage(PROGRESS_KEY));
}

export function setProgressState(value) {
  writeStorage(PROGRESS_KEY, value);
}

export function markEnrolled(learnerId, courseId, enrolled = true) {
  const state = getEnrollmentState();
  const learner = normalizeLearner(learnerId);
  state[learner] = state[learner] || {};
  state[learner][courseId] = enrolled;
  setEnrollmentState(state);
}

export function isEnrolled(learnerId, courseId) {
  const learner = normalizeLearner(learnerId);
  const state = getEnrollmentState();
  return Boolean(state[learner]?.[courseId]);
}

export function setLessonCompletion(learnerId, courseId, lessonId, completed) {
  const state = getProgressState();
  const learner = normalizeLearner(learnerId);
  state[learner] = state[learner] || {};
  state[learner][courseId] = state[learner][courseId] || {};
  state[learner][courseId][lessonId] = Boolean(completed);
  setProgressState(state);
}

export function getCourseProgressMap(learnerId, courseId) {
  const learner = normalizeLearner(learnerId);
  const state = getProgressState();
  return state[learner]?.[courseId] || {};
}

function normalizeLearner(learnerId) {
  return String(learnerId || "guest").trim().toLowerCase();
}

function readStorage(key) {
  try {
    return window.localStorage.getItem(key);
  } catch (error) {
    return null;
  }
}

function writeStorage(key, value) {
  try {
    window.localStorage.setItem(key, JSON.stringify(value));
  } catch (error) {
    // Browser storage can fail in private mode or due to quota limits.
  }
}

function parseObject(raw) {
  if (!raw) {
    return {};
  }
  try {
    const value = JSON.parse(raw);
    return typeof value === "object" && value !== null ? value : {};
  } catch (error) {
    return {};
  }
}
