package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;

class HabrCareerDateTimeParserTest {
@Test
    void whendateTthenOk() throws ParseException {
    String date = "2023-08-01T11:50:16+03:00";
    String expected = "2023-08-01 11:50";
    DateTimeParser dateParser = new HabrCareerDateTimeParser();
    DateTimeFormatter aFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    assertThat(expected).isEqualTo(dateParser.parse(date).format(aFormatter));

}

}