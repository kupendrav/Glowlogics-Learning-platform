package com.glowlogics.learningplatform.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.glowlogics.learningplatform.model.LearnerBlog;
import com.glowlogics.learningplatform.repository.LearnerBlogRepository;

@Service
public class LearnerBlogService {
    private static final String DEFAULT_COVER_IMAGE = "/images/learning-path.svg";
    private static final int MAX_TITLE_LENGTH = 120;
    private static final int MAX_CONTENT_LENGTH = 12000;
    private static final Pattern IMAGE_PATTERN = Pattern.compile("^!\\[(.*?)]\\((https?://[^)\\s]+)\\)$");

    private final LearnerBlogRepository learnerBlogRepository;

    public LearnerBlogService(LearnerBlogRepository learnerBlogRepository) {
        this.learnerBlogRepository = learnerBlogRepository;
    }

    public List<LearnerBlog> latestBlogs(int limit) {
        if (limit <= 0) {
            return List.of();
        }
        return learnerBlogRepository.findAll().stream()
                .limit(limit)
                .toList();
    }

    public LearnerBlog getBySlug(String slug) {
        return learnerBlogRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found for slug: " + slug));
    }

    public LearnerBlog createBlog(String author,
                                  String title,
                                  String markdownContent,
                                  String coverImageUrl) {
        String safeAuthor = normalizeAuthor(author);
        String safeTitle = normalizeTitle(title);
        String safeContent = normalizeContent(markdownContent);
        String slug = ensureUniqueSlug(slugify(safeTitle));

        String contentHtml = renderContentAsHtml(safeContent);
        String summary = buildSummary(safeContent);
        String finalCoverImageUrl = resolveCoverImageUrl(coverImageUrl, safeContent);

        LearnerBlog blog = new LearnerBlog(
                learnerBlogRepository.nextId(),
                slug,
                safeTitle,
                summary,
                finalCoverImageUrl,
                safeContent,
                contentHtml,
                safeAuthor,
                LocalDateTime.now()
        );

        return learnerBlogRepository.save(blog);
    }

    private String normalizeAuthor(String author) {
        if (author == null || author.isBlank()) {
            return "learner";
        }
        return author.trim();
    }

    private String normalizeTitle(String title) {
        String safeTitle = title == null ? "" : title.trim();
        if (safeTitle.isBlank()) {
            throw new IllegalArgumentException("Blog title is required.");
        }
        if (safeTitle.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Blog title must be 120 characters or less.");
        }
        return safeTitle;
    }

    private String normalizeContent(String markdownContent) {
        String safeContent = markdownContent == null ? "" : markdownContent.trim();
        if (safeContent.isBlank()) {
            throw new IllegalArgumentException("Blog content is required.");
        }
        if (safeContent.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("Blog content must be 12000 characters or less.");
        }
        return safeContent;
    }

    private String resolveCoverImageUrl(String coverImageUrl, String markdownContent) {
        String directCoverUrl = normalizeImageUrl(coverImageUrl);
        if (!directCoverUrl.isBlank()) {
            return directCoverUrl;
        }

        String firstImage = extractFirstImageUrl(markdownContent);
        if (!firstImage.isBlank()) {
            return firstImage;
        }

        return DEFAULT_COVER_IMAGE;
    }

    private String normalizeImageUrl(String url) {
        if (url == null) {
            return "";
        }
        String trimmed = url.trim();
        if (trimmed.isBlank()) {
            return "";
        }
        if (trimmed.startsWith("https://") || trimmed.startsWith("http://") || trimmed.startsWith("/images/")) {
            return trimmed;
        }
        return "";
    }

    private String extractFirstImageUrl(String markdownContent) {
        for (String line : markdownContent.split("\\R")) {
            Matcher matcher = IMAGE_PATTERN.matcher(line.trim());
            if (matcher.matches()) {
                return matcher.group(2).trim();
            }
        }
        return "";
    }

    private String renderContentAsHtml(String markdownContent) {
        StringBuilder html = new StringBuilder();
        StringBuilder paragraphBuffer = new StringBuilder();

        for (String line : markdownContent.split("\\R")) {
            String trimmed = line.trim();

            if (trimmed.isBlank()) {
                flushParagraph(paragraphBuffer, html);
                continue;
            }

            Matcher imageMatcher = IMAGE_PATTERN.matcher(trimmed);
            if (imageMatcher.matches()) {
                flushParagraph(paragraphBuffer, html);
                String alt = escapeHtml(imageMatcher.group(1) == null ? "" : imageMatcher.group(1).trim());
                String src = escapeHtml(imageMatcher.group(2) == null ? "" : imageMatcher.group(2).trim());
                html.append("<figure class=\"blog-inline-image\"><img src=\"")
                        .append(src)
                        .append("\" alt=\"")
                        .append(alt)
                        .append("\" loading=\"lazy\"/></figure>");
                continue;
            }

            if (trimmed.startsWith("## ")) {
                flushParagraph(paragraphBuffer, html);
                html.append("<h2>")
                        .append(escapeHtml(trimmed.substring(3).trim()))
                        .append("</h2>");
                continue;
            }

            if (trimmed.startsWith("### ")) {
                flushParagraph(paragraphBuffer, html);
                html.append("<h3>")
                        .append(escapeHtml(trimmed.substring(4).trim()))
                        .append("</h3>");
                continue;
            }

            if (paragraphBuffer.length() > 0) {
                paragraphBuffer.append("<br/>");
            }
            paragraphBuffer.append(escapeHtml(trimmed));
        }

        flushParagraph(paragraphBuffer, html);
        return html.toString();
    }

    private void flushParagraph(StringBuilder paragraphBuffer, StringBuilder html) {
        if (paragraphBuffer.length() == 0) {
            return;
        }
        html.append("<p>").append(paragraphBuffer).append("</p>");
        paragraphBuffer.setLength(0);
    }

    private String escapeHtml(String value) {
        return HtmlUtils.htmlEscape(value == null ? "" : value);
    }

    private String buildSummary(String markdownContent) {
        String plain = Arrays.stream(markdownContent.split("\\R"))
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .map(this::toPlainTextLine)
                .filter(line -> !line.isBlank())
                .reduce((left, right) -> left + " " + right)
                .orElse("");

        if (plain.length() <= 180) {
            return plain;
        }
        return plain.substring(0, 177).trim() + "...";
    }

    private String toPlainTextLine(String line) {
        if (line.startsWith("### ")) {
            return line.substring(4).trim();
        }
        if (line.startsWith("## ")) {
            return line.substring(3).trim();
        }

        Matcher matcher = IMAGE_PATTERN.matcher(line);
        if (matcher.matches()) {
            String alt = matcher.group(1).trim();
            return alt.isBlank() ? "Image" : alt;
        }

        return line;
    }

    private String slugify(String raw) {
        String slug = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        slug = slug.replaceAll("[^a-z0-9]+", "-");
        slug = slug.replaceAll("^-+|-+$", "");
        if (slug.isBlank()) {
            return "learner-blog";
        }
        if ("write".equals(slug)) {
            return "write-blog";
        }
        return slug;
    }

    private String ensureUniqueSlug(String baseSlug) {
        String candidate = baseSlug;
        int suffix = 2;

        while (true) {
            Optional<LearnerBlog> found = learnerBlogRepository.findBySlug(candidate);
            if (found.isEmpty()) {
                return candidate;
            }
            candidate = baseSlug + "-" + suffix;
            suffix++;
        }
    }
}
