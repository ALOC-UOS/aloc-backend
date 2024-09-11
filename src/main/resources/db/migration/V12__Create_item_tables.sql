-- Create table for Item
CREATE TABLE IF NOT EXISTS item (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    coin INT NOT NULL,
    item_type VARCHAR(255) NOT NULL,
    is_hidden BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create table for ItemImage
CREATE TABLE IF NOT EXISTS item_image (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    full_path VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_image_item FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE
);

-- Create table for UserItem
CREATE TABLE IF NOT EXISTS user_item (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_item_user FOREIGN KEY (user_id) REFERENCES study_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_item_item FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE
);

-- Optional: Indexes for performance
CREATE INDEX idx_item_item_type ON item(item_type);
CREATE INDEX idx_item_image_item_id ON item_image(item_id);
CREATE INDEX idx_user_item_user_id ON user_item(user_id);
CREATE INDEX idx_user_item_item_id ON user_item(item_id);
