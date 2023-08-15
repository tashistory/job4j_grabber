package ru.job4j.grabber.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class HHCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");
        Date date = dateFormat.parse(parse);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
