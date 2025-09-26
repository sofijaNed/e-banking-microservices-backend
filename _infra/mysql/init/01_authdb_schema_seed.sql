/*!40101 SET NAMES utf8mb4 */;
/*!40101 SET SQL_MODE=''*/;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE IF NOT EXISTS `authdb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `authdb`;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `username` varchar(50) NOT NULL,
                        `password` varchar(255) NOT NULL,
                        `role` varchar(50) NOT NULL,
                        `two_factor_enabled` tinyint(1) NOT NULL DEFAULT 0,
                        `two_factor_method` varchar(20) DEFAULT 'EMAIL',
                        PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `token`;
CREATE TABLE `token` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `token` varchar(500) NOT NULL,
                         `token_type` varchar(50) NOT NULL,
                         `revoked` tinyint(1) NOT NULL DEFAULT 0,
                         `expired` tinyint(1) NOT NULL DEFAULT 0,
                         `username` varchar(50) DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         KEY `idx_token_user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `user_otp`;
CREATE TABLE `user_otp` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `username` varchar(50) DEFAULT NULL,
                            `email` varchar(150) DEFAULT NULL,
                            `client_id` bigint DEFAULT NULL,
                            `ticket_id` varchar(64) DEFAULT NULL,
                            `otp_hash` varchar(255) NOT NULL,
                            `expires_at` datetime NOT NULL,
                            `created_at` datetime NOT NULL DEFAULT current_timestamp(),
                            `used` tinyint(1) NOT NULL DEFAULT 0,
                            `attempts` int NOT NULL DEFAULT 0,
                            `purpose` varchar(50) DEFAULT 'LOGIN_2FA',
                            `reserved_username` varchar(50) DEFAULT NULL,
                            `password_hash` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `idx_user_otp_purpose_ticket` (`purpose`,`ticket_id`),
                            KEY `idx_user_otp_email_purpose` (`email`,`purpose`,`used`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `user` (`username`,`password`,`role`,`two_factor_enabled`,`two_factor_method`) VALUES
                                                                                               ('klijenta','$2a$12$fMidd78c92SeNLOzuystROydtzTkIlZpZfbIAFGXHhRV2sLUNeiyu','ROLE_CLIENT',1,'EMAIL'),
                                                                                               ('klijentb','$2a$12$fMidd78c92SeNLOzuystROydtzTkIlZpZfbIAFGXHhRV2sLUNeiyu','ROLE_CLIENT',1,'EMAIL'),
                                                                                               ('klijentc','$2a$12$fMidd78c92SeNLOzuystROydtzTkIlZpZfbIAFGXHhRV2sLUNeiyu','ROLE_CLIENT',1,'EMAIL'),
                                                                                               ('klijentd','$2a$12$fMidd78c92SeNLOzuystROydtzTkIlZpZfbIAFGXHhRV2sLUNeiyu','ROLE_CLIENT',1,'EMAIL'),
                                                                                               ('zaposleni','$2a$12$fMidd78c92SeNLOzuystROydtzTkIlZpZfbIAFGXHhRV2sLUNeiyu','ROLE_EMPLOYEE',1,'EMAIL')
    ON DUPLICATE KEY UPDATE password=VALUES(password), role=VALUES(role), two_factor_enabled=VALUES(two_factor_enabled),
    two_factor_method=VALUES(two_factor_method);
