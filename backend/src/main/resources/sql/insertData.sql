-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data

DELETE FROM horse where id < 0;
DELETE FROM owner where id < 0;
DELETE FROM horse_parents WHERE horse_id < 0 OR parent_id < 0;


INSERT INTO horse (id, name, description, date_of_birth, sex) VALUES (-1, 'Wendy', 'The famous one!', '2012-12-12', 'FEMALE')
;

-- Insert demo horses (negative IDs)
INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id) VALUES
(-1, 'Henry', 'Bkacl with brown sports', '2012-12-12', 'MALE', -1)
(-2, 'Rocky', 'A strong brown horse', '2015-08-30', 'MALE', -8),
(-3, 'Bella', 'A gentle mare', '2016-11-08', 'FEMALE', -2),
(-4, 'Storm', 'A gray horse with a spirited personality', '2017-05-10', 'MALE', -9),
(-5, 'Coco', 'A sweet black mare', '2018-03-22', 'FEMALE', -8),
(-6, 'Grace', 'A graceful chestnut mare', '2017-12-03', 'FEMALE', -10),
(-7, 'Midnight', 'A sleek black stallion', '2015-11-20', 'MALE', -5),
(-8, 'Princess', 'An elegant white horse', '2017-02-14', 'FEMALE', -9),
(-9, 'Daisy', 'A sweet bay mare', '2016-04-12', 'FEMALE', -6),
(-10, 'Spirit', 'A wild white stallion', '2015-12-05', 'MALE', -9),
(-11, 'Misty', 'A gentle gray mare', '2016-08-14', 'FEMALE', -2),
(-12, 'Lily', 'A delicate palomino mare', '2018-07-19', 'FEMALE', -7),
(-13, 'Comet', 'A fast gray stallion', '2014-10 -15', 'MALE', -3),
(-14, 'Rosie', 'A sweet bay mare', '2016-03-10', 'FEMALE', -8),
(-15, 'Bolt', 'A gray horse with a spirited personality', '2017-05-10', 'MALE', -9)
;

-- Insert demo parent-child relationships (negative IDs)
INSERT INTO horse_parents (horse_id, parent_id) VALUES
(-2, -1),   -- Rocky's father is Henry
(-3, -1),   -- Bella's father is Henry 
(-5, -3),   -- Coco's mother is Bella
(-7, -6),   -- Midnight's mother is Grace
(-9, -10),  -- Daisy's father is Spirit
(-10, -1),  -- Spirit's father is Henry
(-11, -4),  -- Misty's father is Storm
(-12, -5),  -- Lily's mother is Coco
(-13, -4),  -- Comet's father is Storm
(-14, -3),  -- Rosie's mother is Bella   

-- Insert demo owners (negative IDs)
INSERT INTO owner (first_name, last_name, email) VALUES
(-1, 'Sarah', 'Williams', 'sarah.williams@gmail.com'),
(-2, 'John', 'Smitt', 'john.smitt@gmail.com'),
(-3, 'Daniel', 'Anderson', 'daniel.anderson@gmail.com'),
(-4, 'William', 'Thomas', 'william.thomas@gmail.com'),
(-5, 'Robert', 'Martinez', 'robert.martinez@gmail.com'),
(-6, 'Jessica', 'Taylor', 'jessica.taylor@gmail.com'),
(-7, 'Lisa', 'Miller', 'lisa.miller@gmail.com'),
(-8, 'James', 'Davis', 'james.davis@gmail.com'),
(-9, 'Michael', 'Brown', 'michael.brown@gmail.com'),
(-10, 'Olivia', 'Moore', 'olivia.moore@gmail.com'),
;
