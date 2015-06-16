CREATE TABLE THREAD (
  ID BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  TITLE VARCHAR(200) NOT NULL,
  TAGS VARCHAR(600) NOT NULL,
  CREATED_ACCOUNT_ID BIGINT NOT NULL,
  CREATED_TIME TIMESTAMP NOT NULL,
  UPDATED_TIME TIMESTAMP NOT NULL
);

INSERT INTO THREAD (
  TITLE,
  TAGS,
  CREATED_ACCOUNT_ID,
  CREATED_TIME,
  UPDATED_TIME
) VALUES (
  'Today''s dinner',
  'food,cooking',
  1,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
);
