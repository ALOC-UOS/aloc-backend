CREATE TABLE IF NOT EXISTS aloc_request (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    request_type VARCHAR(255) NOT NULL,
    is_resolved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES study_user (id)
    );

-- 인덱스 추가 (선택적)
CREATE INDEX idx_aloc_request_user_id ON aloc_request(user_id);
CREATE INDEX idx_aloc_request_request_type ON aloc_request(request_type);
CREATE INDEX idx_aloc_request_is_resolved ON aloc_request(is_resolved);
