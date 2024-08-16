CREATE TABLE IF NOT EXISTS algorithm (
    algorithm_id INTEGER,
    season INTEGER,
    week INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    hidden BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (algorithm_id, season)
    );

-- 인덱스 추가 (선택적)
CREATE INDEX idx_algorithm_week ON algorithm(week);

-- ENUM 타입 생성 (PostgreSQL 특화)
CREATE TYPE routine_enum AS ENUM ('DAILY', 'WEEKLY');  -- 예시 값들, 실제 Routine enum 값에 맞게 수정 필요
CREATE TYPE course_enum AS ENUM ('FULL', 'HALF');  -- 예시 값들, 실제 Course enum 값에 맞게 수정 필요

-- Problem Type 테이블 생성
CREATE TABLE IF NOT EXISTS problem_type (
    id BIGSERIAL PRIMARY KEY,
    routine VARCHAR(50) NOT NULL,
    course VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- 인덱스 추가 (선택적)
CREATE INDEX idx_problem_type_routine ON problem_type(routine);
CREATE INDEX idx_problem_type_course ON problem_type(course);

-- Problem 테이블 생성
CREATE TABLE IF NOT EXISTS problem (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    difficulty INTEGER NOT NULL,
    algorithm_id INTEGER,
    algorithm_season INTEGER,
    hidden BOOLEAN DEFAULT TRUE,
    problem_id INTEGER,
    problem_type_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (algorithm_id, algorithm_season) REFERENCES algorithm (algorithm_id, season),
    FOREIGN KEY (problem_type_id) REFERENCES problem_type (id)
    );

-- UserProblem 테이블 생성
CREATE TABLE IF NOT EXISTS user_problem (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    problem_id BIGINT,
    season INTEGER,
    is_solved BOOLEAN DEFAULT FALSE,
    solved_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES study_user (id),
    FOREIGN KEY (problem_id) REFERENCES problem (id)
    );

-- 인덱스 추가 (선택적)
CREATE INDEX idx_problem_title ON problem(title);
CREATE INDEX idx_problem_difficulty ON problem(difficulty);
CREATE INDEX idx_problem_algorithm ON problem(algorithm_id, algorithm_season);
CREATE INDEX idx_user_problem_user ON user_problem(user_id);
CREATE INDEX idx_user_problem_problem ON user_problem(problem_id);
CREATE INDEX idx_user_problem_season ON user_problem(season);

CREATE TABLE IF NOT EXISTS tag (
    id BIGSERIAL PRIMARY KEY,
    korean_name VARCHAR(255) NOT NULL,
    english_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
    );

-- 인덱스 추가 (선택적)
CREATE INDEX idx_tag_korean_name ON tag(korean_name);
CREATE INDEX idx_tag_english_name ON tag(english_name);

CREATE TABLE IF NOT EXISTS problem_tag (
    id BIGSERIAL PRIMARY KEY,
    problem_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (problem_id) REFERENCES problem (id),
    FOREIGN KEY (tag_id) REFERENCES tag (id)
    );

-- 인덱스 추가 (선택적)
CREATE INDEX idx_problem_tag_problem_id ON problem_tag(problem_id);
CREATE INDEX idx_problem_tag_tag_id ON problem_tag(tag_id);
