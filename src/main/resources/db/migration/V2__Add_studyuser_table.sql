CREATE TABLE IF NOT EXISTS study_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    baekjoon_id VARCHAR(255) NOT NULL,
    github_id VARCHAR(255) NOT NULL,
    student_id VARCHAR(255) NOT NULL,
    discord_id VARCHAR(255),
    notion_email VARCHAR(255),
    rank INTEGER,
    coin INTEGER DEFAULT 0,
    course VARCHAR(50),
    profile_color VARCHAR(50) NOT NULL DEFAULT 'Blue',
    password VARCHAR(255) NOT NULL DEFAULT 'password',
    authority VARCHAR(50),
    refresh_token VARCHAR(1000),
    solved_count INTEGER DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
    );

-- 인덱스 추가 (선택적)
CREATE INDEX idx_study_user_username ON study_user(username);
CREATE INDEX idx_study_user_baekjoon_id ON study_user(baekjoon_id);
CREATE INDEX idx_study_user_github_id ON study_user(github_id);
