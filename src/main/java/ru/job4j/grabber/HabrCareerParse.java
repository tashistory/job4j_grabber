package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private static void pars (String get) throws IOException {
        String url = String.format("%s%s", PAGE_LINK, get);
        Connection connection = Jsoup.connect(url);
        Document document = connection.get();
        Elements rows = document.select(".vacancy-card__inner");
        rows.forEach(row -> {
            Element titleElement = row.select(".vacancy-card__title").first();
            Element linkElement = titleElement.child(0);
            String vacancyName = titleElement.text();
            String date = row.select(".vacancy-card__date")
                    .first().child(0)
                    .attr("datetime");
            DateTimeFormatter aFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            DateTimeParser dataTime = new HabrCareerDateTimeParser();
            String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
            System.out.printf("%s %s %s%n", vacancyName, link, dataTime.parse(date).format(aFormatter));
        });
    }
    public static void main(String[] args) throws IOException {
        for (int i = 1; i < 6; i++) {
            String get = String.format("?page=%s", i);
            System.out.printf("\t\t Page %d%n", i);
            pars(get);
        }
    }
}
