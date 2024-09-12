CREATE TABLE IF NOT EXISTS users (
   id       INT             NOT NULL AUTO_INCREMENT,
   nickname VARCHAR(128)    NOT NULL,
   email VARCHAR(128)    NOT NULL UNIQUE,
   password VARCHAR(512)    NOT NULL,
   PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tweets (
    id INT NOT NULL AUTO_INCREMENT,
    text VARCHAR(256) NOT NULL,
    image VARCHAR(256) NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
  id       INT          NOT NULL AUTO_INCREMENT,
  text     VARCHAR(256) NOT NULL,
  user_id  INT          NOT NULL,
  tweet_id INT          NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id)  REFERENCES users(id),
  FOREIGN KEY (tweet_id) REFERENCES tweets(id)
);
