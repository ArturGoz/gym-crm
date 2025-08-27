CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    is_active  BOOLEAN      NOT NULL
);

CREATE TABLE training_types
(
    id                 BIGSERIAL PRIMARY KEY,
    training_type_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE trainers
(
    id                BIGSERIAL PRIMARY KEY,
    specialization_id BIGINT NOT NULL,
    user_id           BIGINT NOT NULL,
    CONSTRAINT fk_trainer_specialization FOREIGN KEY (specialization_id) REFERENCES training_types (id),
    CONSTRAINT fk_trainer_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE trainees
(
    id            BIGSERIAL PRIMARY KEY,
    date_of_birth DATE,
    address       VARCHAR(255),
    user_id       BIGINT NOT NULL,
    CONSTRAINT fk_trainee_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE trainings
(
    id                BIGSERIAL PRIMARY KEY,
    training_name     VARCHAR(100) NOT NULL,
    training_date     DATE         NOT NULL,
    training_duration BIGINT       NOT NULL,
    trainee_id        BIGINT       NOT NULL,
    trainer_id        BIGINT       NOT NULL,
    type_id           BIGINT       NOT NULL,
    CONSTRAINT fk_training_trainee FOREIGN KEY (trainee_id) REFERENCES trainees (id),
    CONSTRAINT fk_training_trainer FOREIGN KEY (trainer_id) REFERENCES trainers (id),
    CONSTRAINT fk_training_type FOREIGN KEY (type_id) REFERENCES training_types (id)
);

CREATE TABLE trainee_trainer
(
    trainee_id BIGINT NOT NULL,
    trainer_id BIGINT NOT NULL,
    PRIMARY KEY (trainee_id, trainer_id),
    CONSTRAINT fk_trainees_trainee_trainer FOREIGN KEY (trainee_id) REFERENCES trainees (id),
    CONSTRAINT fk_trainers_trainee_trainer FOREIGN KEY (trainer_id) REFERENCES trainers (id)
);