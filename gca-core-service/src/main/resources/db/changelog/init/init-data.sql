-- USERS
INSERT INTO users (first_name, last_name, username, password, is_active)
VALUES ('John', 'Doe', 'johndoe', '$2a$12$XKdr6eAnA0W4Vmmdl2/3fuawtfamivMm5g4ZFUbUjL2zoOiKUwSpG', TRUE);

INSERT INTO users (first_name, last_name, username, password, is_active)
VALUES ('Jane', 'Smith', 'janesmith', '$2a$12$333cFrq.pIiAqoV1y04WFum.PfK1GEMU75NhQ0fkS8CsLgFhS.lfm', TRUE);

-- TRAINING TYPES
INSERT INTO training_types (training_type_name)
VALUES ('Yoga');

INSERT INTO training_types (training_type_name)
VALUES ('Boxing');

INSERT INTO training_types (training_type_name)
VALUES ('Crossfit');

-- TRAINERS
INSERT INTO trainers (specialization_id, user_id)
VALUES (1, 1);

-- TRAINEES
INSERT INTO trainees (date_of_birth, address, user_id)
VALUES ('2000-01-01', '123 Main St', 2);

-- TRAININGS
INSERT INTO trainings (training_name, training_date, training_duration, trainee_id, trainer_id, type_id)
VALUES ('Morning Yoga', '2025-07-05', 60, 1, 1, 1);

-- TRAINEE_TRAINER LINK
INSERT INTO trainee_trainer (trainee_id, trainer_id)
VALUES (1, 1);
