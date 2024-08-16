CREATE TABLE IF NOT EXISTS color (
    id VARCHAR(255) PRIMARY KEY,
    color1 VARCHAR(255) NOT NULL,
    color2 VARCHAR(255),
    color3 VARCHAR(255),
    color4 VARCHAR(255),
    color5 VARCHAR(255),
    category VARCHAR(255) NOT NULL,
    degree INTEGER
    );

-- 인덱스 추가 (선택적)
CREATE INDEX idx_color_category ON color(category);
