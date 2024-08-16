CREATE TABLE IF NOT EXISTS history (
                                       id BIGSERIAL PRIMARY KEY,
                                       user_id BIGINT,
                                       username VARCHAR(255) NOT NULL,
    icon VARCHAR(255) NOT NULL,
    rank INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES study_user (id)
    );

-- 인덱스 추가 (선택적)
CREATE INDEX idx_history_user_id ON history(user_id);
CREATE INDEX idx_history_username ON history(username);
CREATE INDEX idx_history_created_at ON history(created_at);

CREATE TABLE IF NOT EXISTS coin_history (
                                            id BIGSERIAL PRIMARY KEY,
                                            user_id BIGINT,
                                            coin INTEGER NOT NULL,
                                            coin_type VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES study_user (id)
    );

-- 열거형 타입 생성 (PostgreSQL 특화)
CREATE TYPE coin_type AS ENUM ('EARN', 'USE');

-- 인덱스 추가 (선택적)
CREATE INDEX idx_coin_history_user_id ON coin_history(user_id);
CREATE INDEX idx_coin_history_coin_type ON coin_history(coin_type);
CREATE INDEX idx_coin_history_created_at ON coin_history(created_at);
