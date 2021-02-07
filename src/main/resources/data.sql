DROP TABLE IF EXISTS topic;
DROP TABLE IF EXISTS feed;

CREATE TABLE feed (
  id IDENTITY PRIMARY KEY,
  execution_id VARCHAR(32) NOT NULL,
  title VARCHAR(256) NOT NULL,
  link VARCHAR(1000) NOT NULL
);

CREATE TABLE topic (
  id IDENTITY PRIMARY KEY,
  name VARCHAR(32) NOT NULL,
  feed_id INT,
  foreign key (feed_id) references feed(id)
);