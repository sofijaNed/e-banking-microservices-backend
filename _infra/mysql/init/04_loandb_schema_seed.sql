/*!40101 SET NAMES utf8mb4 */;
/*!40101 SET SQL_MODE=''*/;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE IF NOT EXISTS `loandb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `loandb`;

DROP TABLE IF EXISTS `loan`;
CREATE TABLE `loan` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `principal_amount` decimal(19,4) DEFAULT NULL,
                        `interest_rate` decimal(6,4) DEFAULT NULL,
                        `term_months` int DEFAULT NULL,
                        `currency` varchar(10) DEFAULT NULL,
                        `monthly_payment` decimal(19,4) DEFAULT NULL,
                        `outstanding_balance` decimal(19,4) DEFAULT NULL,
                        `date_issued` date DEFAULT NULL,
                        `status` enum('PENDING','APPROVED','REJECTED','DISBURSED','PAID_OFF') NOT NULL DEFAULT 'PENDING',
                        `approved_by` bigint DEFAULT NULL,
                        `approved_at` date DEFAULT NULL,
                        `note` varchar(500) DEFAULT NULL,
                        `created_at` timestamp NULL DEFAULT NULL,
                        `updated_at` timestamp NULL DEFAULT NULL,
                        `created_by` varchar(200) DEFAULT NULL,
                        `updated_by` varchar(200) DEFAULT NULL,
                        `account_id` bigint DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        KEY `idx_loan_account` (`account_id`),
                        KEY `idx_loan_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `loan_payment`;
CREATE TABLE `loan_payment` (
                                `loan_id` bigint NOT NULL,
                                `installment_no` int NOT NULL,
                                `due_date` date DEFAULT NULL,
                                `amount` decimal(19,4) NOT NULL,
                                `currency` varchar(10) DEFAULT NULL,
                                `paid` tinyint(1) DEFAULT 0,
                                `paid_at` date DEFAULT NULL,
                                `principal_amount` decimal(19,4) DEFAULT NULL,
                                `interest_amount` decimal(19,4) DEFAULT NULL,
                                `created_at` timestamp NULL DEFAULT NULL,
                                `updated_at` timestamp NULL DEFAULT NULL,
                                `created_by` varchar(200) DEFAULT NULL,
                                `updated_by` varchar(200) DEFAULT NULL,
                                `payment_date` date DEFAULT NULL,
                                `note` varchar(255) DEFAULT NULL,
                                PRIMARY KEY (`loan_id`,`installment_no`),
                                KEY `idx_lp_loan_paid_inst` (`loan_id`,`paid`,`installment_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- seed: nema unapred kreiranih kredita (čist start)
