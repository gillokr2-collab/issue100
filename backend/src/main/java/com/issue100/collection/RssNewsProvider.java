package com.issue100.collection;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
import org.w3c.dom.Element;

@Component
public class RssNewsProvider implements NewsProvider {
    private final List<Feed> feeds;
    private final HttpClient http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

    public RssNewsProvider(@Value("${news.rss.urls}") String urls,
            @Value("${community.rss.urls:}") String communityUrls) {
        this.feeds = List.of((urls + ";" + communityUrls).split(";")).stream().map(String::trim).filter(s -> !s.isBlank())
            .map(value -> {
                String[] parts = value.split("\\|", 2);
                return parts.length == 2 ? new Feed(parts[0].trim(), parts[1].trim()) : new Feed(host(value), value);
            }).toList();
    }

    @Override
    public List<CollectedArticle> collectNews(NewsCollectionRequest request) {
        List<CollectedArticle> collected = new ArrayList<>();
        RuntimeException lastFailure = null;
        for (Feed feed : feeds) {
            try {
                collected.addAll(readFeed(feed));
            } catch (Exception exception) {
                lastFailure = new IllegalStateException("RSS 수집 실패: " + feed.url(), exception);
            }
        }
        List<CollectedArticle> result = collected.stream()
            .filter(article -> !article.publishedAt().isBefore(request.since()))
            .distinct()
            .limit(request.limit())
            .toList();
        if (result.isEmpty() && lastFailure != null) throw lastFailure;
        if (result.isEmpty()) throw new IllegalStateException("RSS에서 수집된 기사가 없습니다.");
        return result;
    }

    private List<CollectedArticle> readFeed(Feed feed) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(feed.url()))
            .header("User-Agent", "Issue100/0.1 (+RSS reader)")
            .timeout(java.time.Duration.ofSeconds(8))
            .GET().build();
        HttpResponse<byte[]> response = http.send(request,
            HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() / 100 != 2) throw new IllegalStateException("HTTP " + response.statusCode());

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        var document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(response.body()));
        var items = document.getElementsByTagName("item");
        List<CollectedArticle> articles = new ArrayList<>();
        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            String title = HtmlUtils.htmlUnescape(text(item, "title"));
            String link = text(item, "link");
            if (title.isBlank() || link.isBlank()) continue;
            String description = HtmlUtils.htmlUnescape(text(item, "description"))
                .replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
            String imageUrl = image(item);
            OffsetDateTime publishedAt = parseDate(text(item, "pubDate"));
            String id = text(item, "guid");
            if (id.isBlank()) id = link;
            articles.add(new CollectedArticle(id, title, description, link, imageUrl, feed.publisher(),
                publishedAt, "문화", sha256(title + "|" + link)));
        }
        return articles;
    }

    private String text(Element element, String tag) {
        var nodes = element.getElementsByTagName(tag);
        return nodes.getLength() == 0 ? "" : nodes.item(0).getTextContent().trim();
    }

    private String image(Element item) {
        for (String tag : List.of("media:content", "media:thumbnail", "enclosure")) {
            var nodes = item.getElementsByTagName(tag);
            if (nodes.getLength() > 0 && nodes.item(0) instanceof Element element) {
                String url = element.getAttribute("url");
                String type = element.getAttribute("type");
                if (!url.isBlank() && (type.isBlank() || type.startsWith("image/"))) return url;
            }
        }
        String raw = text(item, "description");
        var matcher = java.util.regex.Pattern.compile("<img[^>]+src=[\\\"']([^\\\"']+)").matcher(raw);
        return matcher.find() ? HtmlUtils.htmlUnescape(matcher.group(1)) : "";
    }

    private static String host(String value) {
        try { return URI.create(value).getHost().replaceFirst("^www\\.", ""); }
        catch (Exception ignored) { return "RSS"; }
    }

    private record Feed(String publisher, String url) {}

    private OffsetDateTime parseDate(String value) {
        if (value == null || value.isBlank()) return OffsetDateTime.now();
        try { return ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME).toOffsetDateTime(); }
        catch (Exception ignored) {
            try { return OffsetDateTime.parse(value); }
            catch (Exception alsoIgnored) { return OffsetDateTime.now(); }
        }
    }

    private String sha256(String value) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
            .digest(value.getBytes(StandardCharsets.UTF_8)));
    }
}
