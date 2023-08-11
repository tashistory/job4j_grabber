CREATE TABLE post
(
  id serial primary key,
  name varchar NOT NULL,
  text varchar NOT NULL,
  link varchar NOT NULL,
  created timestamp,
 
  CONSTRAINT post_name UNIQUE (name, text, link, created)
);