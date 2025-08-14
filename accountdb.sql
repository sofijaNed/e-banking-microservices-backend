-- accountdb.sql
CREATE DATABASE IF NOT EXISTS accountdb CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE accountdb;

DROP TABLE IF EXISTS account;

CREATE TABLE `account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_number` varchar(50) NOT NULL,
  `type` varchar(20) DEFAULT NULL,
  `iban` varchar(34) DEFAULT NULL,
  `balance` decimal(19,4) NOT NULL DEFAULT 0.0000,
  `available_balance` decimal(19,4) NOT NULL DEFAULT 0.0000,
  `currency` varchar(10) NOT NULL,
  `opened` date DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `version` bigint(20) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `created_by` varchar(200) DEFAULT NULL,
  `updated_by` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_number` (`account_number`),
  KEY `idx_account_client_id` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `account`
(`id`,`account_number`,`type`,`iban`,`balance`,`available_balance`,`currency`,`opened`,`client_id`,`version`,`created_at`,`updated_at`,`created_by`,`updated_by`) VALUES
(1,'RS35123456789012345678','CHECKING','RS35123456789012345678',617395.5600,615395.5600,'RSD','2020-01-01',1,1,'2025-08-10 19:27:09','2025-08-10 19:27:09','system','system'),
(2,'RS35123456789012345679','CHECKING','RS35123456789012345679',122870.0000,117870.0000,'RSD','2020-01-01',2,1,'2025-08-10 19:27:09','2025-08-10 19:27:09','system','system'),
(3,'RS35123456789012345680','CHECKING','RS35123456789012345680',75000.0000,73000.0000,'RSD','2020-01-01',3,1,'2025-08-10 19:27:09','2025-08-10 19:27:09','system','system'),
(4,'999-0000000001','BANK','RS35999000000000001',24867.2200,10000000024867.2200,'RSD','2025-08-11',NULL,0,'2025-08-11 20:35:40','2025-08-11 20:35:40','system','system');
