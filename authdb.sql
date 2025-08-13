/*
SQLyog Community v13.2.0 (64 bit)
MySQL - 10.4.32-MariaDB : Database - authdb
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`authdb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

USE `authdb`;

/*Table structure for table `token` */

DROP TABLE IF EXISTS `token`;

CREATE TABLE `token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varchar(500) NOT NULL,
  `token_type` varchar(50) NOT NULL,
  `revoked` tinyint(1) NOT NULL,
  `expired` tinyint(1) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_token_username` (`username`),
  CONSTRAINT `fk_token_user` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `token` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(50) NOT NULL,
  `two_factor_enabled` tinyint(1) NOT NULL DEFAULT 0,
  `two_factor_method` varchar(20) DEFAULT 'EMAIL',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `user` */

insert  into `user`(`username`,`password`,`role`,`two_factor_enabled`,`two_factor_method`) values 
('client1','$2a$12$Nr4AydODOCTzU1b1XlSyXOye5UO75/2KTP2Pcd5X8g2eZqQgM6tjK','ROLE_CLIENT',1,'EMAIL'),
('client2','$2a$12$Nr4AydODOCTzU1b1XlSyXOye5UO75/2KTP2Pcd5X8g2eZqQgM6tjK','ROLE_CLIENT',0,'EMAIL'),
('client3','$2a$12$Nr4AydODOCTzU1b1XlSyXOye5UO75/2KTP2Pcd5X8g2eZqQgM6tjK','ROLE_CLIENT',0,'EMAIL'),
('employee1','$2a$12$Nr4AydODOCTzU1b1XlSyXOye5UO75/2KTP2Pcd5X8g2eZqQgM6tjK','ROLE_EMPLOYEE',0,'EMAIL'),
('employee2','$2a$12$Nr4AydODOCTzU1b1XlSyXOye5UO75/2KTP2Pcd5X8g2eZqQgM6tjK','ROLE_EMPLOYEE',0,'EMAIL');

/*Table structure for table `user_otp` */

DROP TABLE IF EXISTS `user_otp`;

CREATE TABLE `user_otp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `otp_hash` varchar(255) NOT NULL,
  `expires_at` datetime NOT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `used` tinyint(1) NOT NULL DEFAULT 0,
  `attempts` int(11) NOT NULL DEFAULT 0,
  `purpose` varchar(50) DEFAULT 'LOGIN_2FA',
  PRIMARY KEY (`id`),
  KEY `idx_user_otp_username` (`username`),
  CONSTRAINT `fk_userotp_user` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `user_otp` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
