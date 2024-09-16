DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'user_profile'
        AND column_name = 'profile_image_file_name'
    ) THEN
ALTER TABLE user_profile ADD COLUMN profile_image_file_name VARCHAR(255);
END IF;
END $$;
