package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HHCareerDateTimeParser;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HHCareerParse implements Parse {
    private final DateTimeParser dateTimeParser;

    public HHCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> result = new ArrayList<>();
        String get = "&page=";
        for (int i = 0; i < 3; i++) {
            String url = String.format("%s%s%d", link, get, i);
            System.out.println();
            System.out.println(url);
            System.out.println();
            result.addAll(pagelist(url));
        }
        return result;
    }

    private DescroptionDate getDescription(String url) throws IOException, ParseException {
        Connection connection = Jsoup.connect(url);
        Document document = connection.get();
        String description = document.select(".g-user-content").text();
        String dateStr = document.select(".vacancy-creation-time-redesigned").select("span").text();
        return new DescroptionDate(description, dateTimeParser.parse(dateStr));

    }

    private List<Post> pagelist(String url) throws IOException {
        List<Post> result = new ArrayList<>();
        Connection connection = Jsoup.connect(url);
        Document document = connection.get();
        Elements rows = document.select(".serp-item");
        rows.forEach(row -> {
            result.add(getPost(row));
        });
        return result;
    }

    private Post getPost(Element row) {
        Element titleElement = row.select(".serp-item__title").first();
        String link = titleElement.select("a").first().attr("href");
        DescroptionDate descroptionDate;
        try {
            descroptionDate = getDescription(link);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println(titleElement.text());
        System.out.println(link);
        System.out.println(descroptionDate.description);
        System.out.println(descroptionDate.date);
        return new Post(titleElement.text(), link, descroptionDate.description, descroptionDate.date);
    }

    public static void main(String[] args) throws IOException {
        Parse parse = new HHCareerParse(new HHCareerDateTimeParser());
        parse.list("https://hh.ru/search/vacancy?text=Java-разработчик").forEach(System.out::println);

    }

    record DescroptionDate(String description, LocalDateTime date) {

        public String description() {
            return description;
        }

        public LocalDateTime date() {
            return date;
        }
    }

}
