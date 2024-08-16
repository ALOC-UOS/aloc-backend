CREATE TABLE user_profile (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE,
    CONSTRAINT fk_user_profile_user FOREIGN KEY (user_id) REFERENCES study_user(id)
);
