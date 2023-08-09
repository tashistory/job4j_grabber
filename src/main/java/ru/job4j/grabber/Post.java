package ru.job4j.grabber;

import java.time.LocalDateTime;

public class Post {
    int id;
    String title;
    String link;
    String description;
    LocalDateTime created;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        if (id != post.id) {
            return false;
        }
        return link.equals(post.link);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + link.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Post{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", link='" + link + '\''
                +  ", description='" + description + '\''
                + ", created=" + created
                + '}';
    }
}
