# Users schema

# --- !Ups

CREATE TABLE User (
  `id`       INT NOT NULL AUTO_INCREMENT,
  `email`    VARCHAR(250) NOT NULL,
  `password` VARCHAR(250) NOT NULL,
  `forename` VARCHAR(250) NOT NULL,
  `surname`  VARCHAR(250) NOT NULL,
  `phone`    VARCHAR(250) NOT NULL,
  `role`     ENUM('Admin', 'User') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC)
);

# --- !Downs

DROP TABLE User;