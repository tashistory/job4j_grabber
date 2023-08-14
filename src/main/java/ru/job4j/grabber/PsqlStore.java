package ru.job4j.grabber;

import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private Connection cnn;

    public PsqlStore(Properties cfg) throws SQLException {
        try {
            Class.forName(cfg.getProperty("connection.driver_class"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        String url = cfg.getProperty("connection.url");
        String login = cfg.getProperty("connection.username");
        String password = cfg.getProperty("connection.password");
        cnn = DriverManager.getConnection(url, login, password);
    }

    private Post getPost(ResultSet rs) throws SQLException {
       return new Post(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getTimestamp(5).toLocalDateTime());
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement =
                     cnn.prepareStatement("INSERT INTO post(name, text, link, created) VALUES (?, ?, ?, ?) ON CONFLICT (link) DO NOTHING")) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Post> getAll() throws SQLException {
        List<Post> rslt = new LinkedList<>();
        try (PreparedStatement ps = cnn.prepareStatement("select * from post")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rslt.add(getPost(rs));
            }
        }
        return rslt;
    }

    @Override
    public Post findById(int id) throws SQLException {
        Post rslt = new Post();
        try (PreparedStatement ps = cnn.prepareStatement("select * from post where id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
             return getPost(rs);
            }
        }
        return rslt;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) throws SQLException, IOException {

        Properties config = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("aggregator.properties")) {
            config.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Store store = new PsqlStore(config);
        Parse parses = new HabrCareerParse(new HabrCareerDateTimeParser());
        List<Post> posts = new ArrayList<>(parses.list("https://career.habr.com"));
        posts.forEach(store::save);
        store.getAll().forEach(System.out::println);
    }
}