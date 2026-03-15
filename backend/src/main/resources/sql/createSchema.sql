CREATE TABLE IF NOT EXISTS owner
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  email VARCHAR(255)
);


CREATE TABLE IF NOT EXISTS horse
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(4095),
  date_of_birth DATE NOT NULL,
  sex ENUM('MALE', 'FEMALE') NOT NULL,
  owner_id BIGINT,
  image LONGBLOB,
  image_media_type VARCHAR(100),
  FOREIGN KEY (owner_id) REFERENCES owner(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS horse_parents
(
  horse_id BIGINT NOT NULL,
  parent_id BIGINT NOT NULL,
  PRIMARY KEY (horse_id, parent_id),
  FOREIGN KEY (horse_id) REFERENCES horse(id) ON DELETE CASCADE,
  FOREIGN KEY (parent_id) REFERENCES horse(id) ON DELETE CASCADE,
  CHECK (horse_id != parent_id)
);
