CREATE DATABASE IF NOT EXISTS thymeleaf_category CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE thymeleaf_category;
CREATE TABLE IF NOT EXISTS categories (
    category_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    PRIMARY KEY (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
