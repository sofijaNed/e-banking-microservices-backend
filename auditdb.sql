/*!40101 SET NAMES utf8mb4 */;
/*!40101 SET SQL_MODE=''*/;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE IF NOT EXISTS `auditdb`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `auditdb`;

DROP TABLE IF EXISTS `audit_event`;

CREATE TABLE `audit_event` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `ts` datetime NOT NULL DEFAULT current_timestamp(),
                               `service` varchar(50) NOT NULL,
                               `action` varchar(50) NOT NULL,
                               `principal` varchar(50) DEFAULT NULL,
                               `outcome` enum('SUCCESS','FAIL') NOT NULL,
                               `ip` varchar(45) DEFAULT NULL,
                               `user_agent` varchar(255) DEFAULT NULL,
                               `resource_type` varchar(50) DEFAULT NULL,
                               `resource_id` varchar(100) DEFAULT NULL,
                               `correlation_id` varchar(64) DEFAULT NULL,
                               `http_method` varchar(10) DEFAULT NULL,
                               `http_path` varchar(200) DEFAULT NULL,
                               `http_status` int DEFAULT NULL,
                               `duration_ms` int DEFAULT NULL,
                               `checks_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`checks_json`)),
                               `details_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`details_json`)),
                               `tags_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`tags_json`)),
                               PRIMARY KEY (`id`),
                               KEY `idx_audit_ts` (`ts`),
                               KEY `idx_audit_service_action_ts` (`service`,`action`,`ts`),
                               KEY `idx_audit_principal_ts` (`principal`,`ts`),
                               KEY `idx_audit_corr` (`correlation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- opcioni test record (možeš ostaviti prazno)
-- INSERT INTO audit_event(service,action,outcome) VALUES ('audit-service','BOOT','SUCCESS');
