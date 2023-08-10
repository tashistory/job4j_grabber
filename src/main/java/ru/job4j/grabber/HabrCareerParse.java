package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private final DateTimeParser dateTimeParser;
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private static String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        return document.select(".style-ugc").first().text();
    }

     private Post getPost(Element row) {
         Element titleElement = row.select(".vacancy-card__title").first();
         Element linkElement = titleElement.child(0);
         String vacancyName = titleElement.text();
         String date = row.select(".vacancy-card__date")
                 .first().child(0)
                 .attr("datetime");
         String linkUrl = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
         String description = "";
         try {
             description = retrieveDescription(linkUrl);
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
         return new Post(vacancyName, linkUrl, description, dateTimeParser.parse(date));
     }

    public List<Post> pagelist(String get) throws IOException {
        List<Post> result = new ArrayList<>();
        Connection connection = Jsoup.connect(get);
        Document document = connection.get();
        Elements rows = document.select(".vacancy-card__inner");
        rows.forEach(row -> {
            result.add(getPost(row));
        });
        return result;
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> result = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            String get = String.format("%s%s%s", PAGE_LINK, "?page=%s", i);
            result.addAll(pagelist(get));
        }
        return result;
    }
}

