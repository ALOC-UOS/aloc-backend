-- Add 'coin' column if not exists
ALTER TABLE user_profile ADD COLUMN coin INTEGER DEFAULT 0;

-- Add 'profile_color' column if not exists
ALTER TABLE user_profile ADD COLUMN profile_color VARCHAR(50) DEFAULT 'Blue';

-- Add 'student_id' column if not exists
ALTER TABLE user_profile ADD COLUMN student_id VARCHAR(255);

-- Add 'notion_email' column if not exists
ALTER TABLE user_profile ADD COLUMN notion_email VARCHAR(255);

-- Add 'discord_id' column if not exists
ALTER TABLE user_profile ADD COLUMN discord_id VARCHAR(255);

INSERT INTO user_profile (user_id, coin, profile_color, student_id, notion_email, discord_id)
SELECT
    su.id AS user_id,
    su.coin,
    su.profile_color,
    su.student_id,
    su.notion_email,
    su.discord_id
FROM study_user su
         LEFT JOIN user_profile up ON su.id = up.user_id
WHERE up.user_id IS NULL;
