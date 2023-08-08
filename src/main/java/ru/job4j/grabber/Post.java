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

        if (!link.equals(post.link)) {
            return false;
        }
        return created.equals(post.created);
    }

    @Override
    public int hashCode() {
        int result = link.hashCode();
        result = 31 * result + created.hashCode();
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
