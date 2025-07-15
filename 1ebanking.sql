/*
SQLyog Community v13.2.0 (64 bit)
MySQL - 10.4.32-MariaDB : Database - 1ebanking
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`1ebanking` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

USE `1ebanking`;

/*Table structure for table `account` */

DROP TABLE IF EXISTS `account`;

CREATE TABLE `account` (
  `id` varchar(20) NOT NULL,
  `type` varchar(20) DEFAULT NULL,
  `balance` double DEFAULT NULL,
  `currency` varchar(10) DEFAULT NULL,
  `opened` date DEFAULT NULL,
  `clientid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pk_client` (`clientid`),
  CONSTRAINT `pk_client` FOREIGN KEY (`clientid`) REFERENCES `client` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `account` */

insert  into `account`(`id`,`type`,`balance`,`currency`,`opened`,`clientid`) values 
('1','CHECKING',973678,'RSD','2024-07-08',1),
('2','SAVINGS',650937,'RSD','2024-07-09',2),
('3','CHECKING',5685,'EUR','2024-07-16',1);

/*Table structure for table `client` */

DROP TABLE IF EXISTS `client`;

CREATE TABLE `client` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `birthdate` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `username` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pk_user2` (`username`),
  CONSTRAINT `pk_user2` FOREIGN KEY (`username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `client` */

insert  into `client`(`id`,`firstname`,`lastname`,`birthdate`,`email`,`phone`,`address`,`username`) values 
(1,'Tara','Paunovic','2000-07-16','tara@gmail.com','0652306542','Adresa1','client1'),
(2,'Milan','Ivic','2000-07-08','milan@gmail.com','06523014598','Adresa2','client2');

/*Table structure for table `employee` */

DROP TABLE IF EXISTS `employee`;

CREATE TABLE `employee` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `username` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pk_user1` (`username`),
  CONSTRAINT `pk_user1` FOREIGN KEY (`username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `employee` */

insert  into `employee`(`id`,`firstname`,`lastname`,`username`) values 
(1,'Sofija','Nedeljkovic','employee1'),
(2,'Marija','Maric','employee2');

/*Table structure for table `loan` */

DROP TABLE IF EXISTS `loan`;

CREATE TABLE `loan` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `principal_amount` double DEFAULT NULL,
  `interest_rate` double DEFAULT NULL,
  `loan_term` date DEFAULT NULL,
  `monthly_payment` double DEFAULT NULL,
  `outstanding_balance` double DEFAULT NULL,
  `date_issued` date DEFAULT NULL,
  `clientid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pk_clientt` (`clientid`),
  CONSTRAINT `pk_clientt` FOREIGN KEY (`clientid`) REFERENCES `client` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `loan` */

insert  into `loan`(`id`,`principal_amount`,`interest_rate`,`loan_term`,`monthly_payment`,`outstanding_balance`,`date_issued`,`clientid`) values 
(2,2000,15,'2024-07-08',600,8956,'2024-07-24',1);

/*Table structure for table `token` */

DROP TABLE IF EXISTS `token`;

CREATE TABLE `token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `token` varchar(255) DEFAULT NULL,
  `type` enum('BEARER') DEFAULT NULL,
  `user` varchar(200) DEFAULT NULL,
  `expired` bit(1) NOT NULL,
  `revoked` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `pk_usern` (`user`),
  CONSTRAINT `pk_usern` FOREIGN KEY (`user`) REFERENCES `user` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `token` */

insert  into `token`(`id`,`token`,`type`,`user`,`expired`,`revoked`) values 
(1,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMjgwNDg2LCJleHAiOjE3MjM3MjA0ODZ9.I-4JErQNrHs_hxPSz89v83Ar5EA9nNmYOxSG5-9p85Q','BEARER','client1','',''),
(2,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMjk1NjI3LCJleHAiOjE3MjM3MzU2Mjd9.Rq_2cLslqQU15r2IU94i3tW6_nUnuf9YicrPKPf6Y_c','BEARER','client1','',''),
(3,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMjk1ODIxLCJleHAiOjE3MjM3MzU4MjF9.VsvuSzPyoNlGOA92o5cCIoTqznczIb3mvfpRC0k1a04','BEARER','client1','',''),
(4,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzYzMjEyLCJleHAiOjE3MjM4MDMyMTJ9.Bsakhl0olYQLFrnzzpa3WW7NTTed1ckviBKFHMoBZDQ','BEARER','client1','',''),
(5,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzY2MDE0LCJleHAiOjE3MjM4MDYwMTR9.JkMFC_Ce7tSoSxfA7SB1pfZoYzeqpkrYIkttClCBSZ0','BEARER','client1','',''),
(6,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzY3MjMwLCJleHAiOjE3MjM4MDcyMzB9.EtffC3fWsU_aHBRK9HrPTEndBn4ZTBRacMevIq27QYY','BEARER','client1','',''),
(7,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzY3MzU2LCJleHAiOjE3MjM4MDczNTZ9.N8LvWNt2h-93_K-LrGrdYNJaQXazJQTZAVEGDwcX0Es','BEARER','client1','',''),
(8,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzY3ODY3LCJleHAiOjE3MjM4MDc4Njd9.5rkOJ9QhXhhFIZHAkXTuxGofz_nMKmRE9j0JI7K74t0','BEARER','client1','',''),
(9,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzY4MDY0LCJleHAiOjE3MjM4MDgwNjR9.3gR5-aIkMbH359twrkS6XCWPh1FoQztRlEGOzMaLpg4','BEARER','client1','',''),
(10,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzY4ODM2LCJleHAiOjE3MjM4MDg4MzZ9.6Fc-FcGZGpEOzA2hm1TIqmuzLZauajNj9LoWorZrL2M','BEARER','client1','',''),
(11,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzY5Njg5LCJleHAiOjE3MjM4MDk2ODl9.BbWXsRoCmmLfUvWP2WA6HeCpvReGyaaVJcax-uMe58Q','BEARER','client1','',''),
(12,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzY5ODE3LCJleHAiOjE3MjM4MDk4MTd9.CmyQ6EQNDcxgoE5OUHcLMgYFsDqzlO0Te0pdd69891Q','BEARER','client1','',''),
(13,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzcxMTMwLCJleHAiOjE3MjM4MTExMzB9.ImJsgAO4WjAikVzO45F_iHH1vZWCDm-a-ddCj1tHNVI','BEARER','client1','',''),
(14,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzcxMjU3LCJleHAiOjE3MjM4MTEyNTd9.i__XVkFvfujCiEsWXpUdWHGyZkbKYkfnMhS7pPH6nrM','BEARER','client1','',''),
(15,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzcxNDM1LCJleHAiOjE3MjM4MTE0MzV9.3DFLRr3pM0ZZapbK0cvjK2Fxt8p1X5jH0Cn2McoM3Cs','BEARER','client1','',''),
(16,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzcxNDk1LCJleHAiOjE3MjM4MTE0OTV9.Ay4lnHKn3zGBweYLpJ4BXWxHtE7u1Q-zzohbyWPLFRI','BEARER','client1','',''),
(17,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzcxNzEwLCJleHAiOjE3MjM4MTE3MTB9.QS8YfuIxWUEXN323eF9jAILq1BmK9OgbKb4b1rhDn_s','BEARER','client1','',''),
(18,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzcyMTYzLCJleHAiOjE3MjM4MTIxNjN9.shj2gY8090azS0zR1Un4ewbzoDb2I7ICssvFbibVscs','BEARER','client1','',''),
(19,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzcyNDk1LCJleHAiOjE3MjM4MTI0OTV9.9YEY4b2vEutAkoehBEpQPCmSfIuksd7VL79HS1Qo7NY','BEARER','client1','',''),
(20,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzcyNjExLCJleHAiOjE3MjM4MTI2MTF9.bIuCDK--nsoTnPVrVQq4B7VBHGSi_61N9rNj2q-COco','BEARER','client1','',''),
(21,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzcyNjkyLCJleHAiOjE3MjM4MTI2OTJ9.SCeQpnSBlo8kOx6jK6LRtynBMNVXBrybLzaq8gb7yhc','BEARER','client1','',''),
(22,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzcyNzYzLCJleHAiOjE3MjM4MTI3NjN9.Fqaf__8PZ42PSn5HiPfpPpRzH3F5cO9t7dxge_lvIMg','BEARER','client1','',''),
(23,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzcyOTc3LCJleHAiOjE3MjM4MTI5Nzd9.IgNIFs4gv3BGmVF4uQ8hT_6tW5YneDtIFU9MywwslSk','BEARER','client1','',''),
(24,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzczMDk1LCJleHAiOjE3MjM4MTMwOTV9.C0gRc9CsbYc9LuQTTG2c45yDWa6ibG4QRwPIrQ9_9A0','BEARER','client1','',''),
(25,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzczMTQ3LCJleHAiOjE3MjM4MTMxNDd9.Sa8MzvywfAgnloYGeEazX4ubpNLWKwF7xPg7NTWzuTA','BEARER','client1','',''),
(26,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzczNjU0LCJleHAiOjE3MjM4MTM2NTR9.-ejINYMQ-EBG8FFKieOw4-dpJiDYe_6_MdmMaT6DlGM','BEARER','client1','',''),
(27,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzczODYxLCJleHAiOjE3MjM4MTM4NjF9.xJGM_a11FA5T_Jzu3W4fGwWQW1uChPw_WjZZRqP-0K0','BEARER','client1','',''),
(28,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyMzc0MzkxLCJleHAiOjE3MjM4MTQzOTF9.xbD0RydQYhpNGtRzyiyZyK9NExbQyaKza6YmawJKumU','BEARER','client1','',''),
(29,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyNDU5MTM4LCJleHAiOjE3MjM4OTkxMzh9.rtFFBg3291r5vjhdKbHSHF2SYewgGsjZb7iKMFdmwfk','BEARER','client1','',''),
(30,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyNDYwMDY4LCJleHAiOjE3MjM5MDAwNjh9.GCVBfHyTQzxKzF2PVTJ19EYplMiJFKwZhHaDOrnZBjA','BEARER','client1','',''),
(31,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzIyNTQxNzMxLCJleHAiOjE3MjM5ODE3MzF9.Lyg8K7cRBglxckxli56MbXwvdLnJ18mPVbXDE-w-l0A','BEARER','client1','',''),
(32,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzM4Nzg2NzYwLCJleHAiOjE3NDAyMjY3NjB9.VBR-JDeiEmk9oM8Xzb9unyhni_SBQp9b9XGHBEvRgZs','BEARER','client1','',''),
(33,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzM4Nzg2ODQ2LCJleHAiOjE3NDAyMjY4NDZ9.QjaWbaORGeH1-o3SfQNzozkZDyEIiN49wznd-r5v4Cw','BEARER','client1','',''),
(34,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzM4OTUwMzY2LCJleHAiOjE3NDAzOTAzNjZ9.RJxLL36kF83lbhDcJb5jCl4Y-jLTBV6kne0broNe5pc','BEARER','client1','',''),
(35,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzM4OTU3NDI0LCJleHAiOjE3NDAzOTc0MjR9.f5edAYRLOe3j0Owi_Bhd3lDrplRcxuYChv-G4gXqpIE','BEARER','client1','',''),
(36,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzM5MzgxMzk1LCJleHAiOjE3NDA4MjEzOTV9.7xolSFQ_0Sv-j8iyHlCty34RxfnvhQoei2_WAx8_nSc','BEARER','client1','',''),
(37,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzM5MzgxNDQzLCJleHAiOjE3NDA4MjE0NDN9.9OqrQh3qF16WX2IxRedqiLl_KQBwG7zM74ywPYaP1gY','BEARER','client1','',''),
(38,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzM5MzgzNDI4LCJleHAiOjE3NDA4MjM0Mjh9.FKravZAwRZgqXLwymR2RVRnlPU-e03nqq2yQ7PvJtc4','BEARER','client1','',''),
(39,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQwNDMyODkyLCJleHAiOjE3NDE4NzI4OTJ9.hBVBXtjU6h90Qv0et0CaxCM9Lb8crxgg2s5n72B4YVA','BEARER','client1','',''),
(40,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQwNDMzMDYxLCJleHAiOjE3NDE4NzMwNjF9.IfVPWiOzhKlq8lhw7iE8J_MmJbk9Okj4-N7-czJDLn0','BEARER','client1','',''),
(41,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQwNDM1OTkyLCJleHAiOjE3NDE4NzU5OTJ9.VsCWRgkPdtNMBsZmvDY2AyijKO8Vx3dxetrsq7DJmUc','BEARER','client1','',''),
(42,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQwNDQyOTQ0LCJleHAiOjE3NDE4ODI5NDR9.6CTEN8blZQe18Lxz8c0W7XFTS5rUMQlUo0cPb4kmWMA','BEARER','client1','',''),
(43,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQwNDQ0NTA4LCJleHAiOjE3NDE4ODQ1MDh9.WmgZiuvKpbCsP4pfdhNS6qxo00JYCYHmXaZyojHQhBU','BEARER','client1','',''),
(44,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQwODY2NTQwLCJleHAiOjE3NDIzMDY1NDB9.yV0Wpeae4f7rDKoiOwx4HN_KccLiL7QwLU1LzGI7SWc','BEARER','client1','',''),
(45,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQwODY3MTcxLCJleHAiOjE3NDIzMDcxNzF9.ZxCcR5Uo24msrUihEfnfuqd_hOVgQCvfOF4fwo2pVFA','BEARER','client1','',''),
(46,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQxMTk0NTI0LCJleHAiOjE3NDI2MzQ1MjR9.xbsavDBj-ncp9fLsr6nknEFf8suOBW8VedcfBBzAyL8','BEARER','client1','',''),
(47,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQxMTk4MTM2LCJleHAiOjE3NDI2MzgxMzZ9.iqf8xINozXIjtXibx1lhwpzkwM6nsUoM2eXthQ9atUc','BEARER','client1','',''),
(48,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQxNTM1Nzk2LCJleHAiOjE3NDI5NzU3OTZ9.Z2jy1-P27bGNetzQWOX8v6i1YMjou_lyv4-lwThBBfU','BEARER','client1','',''),
(49,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQxNTQwOTU4LCJleHAiOjE3NDI5ODA5NTh9.COlmg7hhjjamTQaMn8XYqV7bfHTwA0sCVWhQU39RWDc','BEARER','client1','',''),
(50,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQxNTQxMDc5LCJleHAiOjE3NDI5ODEwNzl9.c8EuUtggKvlMKwTG9b060-aUEtYO7rmWDHOh5ebjHMw','BEARER','client1','',''),
(51,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQxNTQxMTI0LCJleHAiOjE3NDI5ODExMjR9.lvmpriQCHLk0UPfIU7RNS3YNPTiIHMDBOw336AAt5Os','BEARER','client1','',''),
(52,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQxNTU0MjQ0LCJleHAiOjE3NDI5OTQyNDR9.cXn-I1AM1t0QvSIhQ4o0LQTGedLA83svoH2qzZ8hxtk','BEARER','client1','',''),
(53,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQ4MDM2NTM5LCJleHAiOjE3NDk0NzY1Mzl9.2mpHOAEXwBEINxR5gREYhLult7pbzzUVKCja_XHk1B4','BEARER','client1','',''),
(54,'eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9DTElFTlQiLCJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQ4MjA4MTEyLCJleHAiOjE3NDk2NDgxMTJ9.DORrLnewTfSHNllQmOVod8GNBfpdWD2NKq741Wpebr4','BEARER','client1','',''),
(55,'eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9DTElFTlQiLCJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQ4MjEyMzk4LCJleHAiOjE3NDk2NTIzOTh9.9SfVCDQ4TxtkfnqzQtZUyRLYvOIWkRa2nXNNoe3pFg0','BEARER','client1','',''),
(56,'eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9DTElFTlQiLCJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQ4MjEzMTUzLCJleHAiOjE3NDk2NTMxNTN9.ofl7_kv0NIzOiC-5F3ETNlez486XmG2D7DiC5Re1X-U','BEARER','client1','',''),
(57,'eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9DTElFTlQiLCJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQ4NTQ5MDcwLCJleHAiOjE3NDk5ODkwNzB9.rzysg2740viVCLOun-YpUDcuuPtyQnWvmZyWHSTAbNk','BEARER','client1','',''),
(58,'eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUk9MRV9DTElFTlQiLCJzdWIiOiJjbGllbnQxIiwiaWF0IjoxNzQ4ODE2NjEwLCJleHAiOjE3NTAyNTY2MTB9.huTUDOxFQ1TwHDrG1r2ZC2vpY-5_nG-JxxpUjb9IZIU','BEARER','client1','\0','\0');

/*Table structure for table `transaction` */

DROP TABLE IF EXISTS `transaction`;

CREATE TABLE `transaction` (
  `transactionid` int(11) NOT NULL AUTO_INCREMENT,
  `sender` varchar(20) NOT NULL,
  `receiver` varchar(20) NOT NULL,
  `amount` double DEFAULT NULL,
  `date` date DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `status` varchar(100) DEFAULT NULL,
  `model` varchar(100) DEFAULT NULL,
  `number` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`transactionid`,`sender`,`receiver`),
  KEY `pk_receiver` (`receiver`),
  KEY `pk_sender` (`sender`),
  CONSTRAINT `pk_receiver` FOREIGN KEY (`receiver`) REFERENCES `account` (`id`),
  CONSTRAINT `pk_sender` FOREIGN KEY (`sender`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `transaction` */

insert  into `transaction`(`transactionid`,`sender`,`receiver`,`amount`,`date`,`description`,`status`,`model`,`number`) values 
(-1,'1','2',5000,'2024-07-26','Opopop',NULL,'56','25725'),
(1,'1','2',5000,'2024-07-22','Placanje','FAILED','89','12345678'),
(3,'2','1',1000,'2024-07-22','Placanje','SUCCEESS','89','12345678'),
(4,'3','2',50,'2024-07-26','Placanje','SUCCEESS','89','12345678'),
(5,'1','2',5000,'2024-07-22','Placanje','FAILED','89','12345678'),
(6,'1','2',5000,'2024-07-26','juju',NULL,'56','036'),
(7,'1','2',555,'2024-07-27','Saljem paree','SUCCEEDED','25','66466'),
(8,'1','2',5,'2024-07-22','Placanjeee','FAILED','89','12345678'),
(9,'1','2',5,'2024-07-27','nhxsnxf','SUCCEEDED','1','2'),
(10,'1','2',100,'2024-07-27','Konacno vise','SUCCEEDED','1','2'),
(11,'1','2',200,'2024-07-27','Novoooo proba','SUCCEEDED','2','3'),
(12,'1','2',200,'2024-07-27','ftnsnf','SUCCEEDED','1','2'),
(13,'1','2',300,'2024-07-27','Nadam se poslednji put za ovo','SUCCEEDED','3','4'),
(14,'1','2',300,'2024-07-27','Finall countdown','SUCCEEDED','4','5'),
(15,'1','2',555,'2024-07-27','FINALAALALLA','SUCCEEDED','5','6'),
(16,'1','3',655,'2024-07-27','WTF','SUCCEEDED','9','8'),
(17,'1','2',10,'2024-07-28','ko','SUCCEEDED','1','2'),
(18,'1','2',89,'2024-07-28','slanje','SUCCEEDED','87','987286'),
(19,'1','2',20,'2024-07-28','ne pitam ni ko je','SUCCEEDED','3','4'),
(20,'1','2',5,'2024-07-28','Jaaa','SUCCEEDED','8','9'),
(21,'1','2',5,'2024-07-28','sfh','SUCCEEDED','5','6'),
(22,'1','2',5,'2024-07-28','gfnjc','SUCCEEDED','2','3'),
(23,'1','2',6,'2024-07-28','neobicno','SUCCEEDED','78','5632'),
(24,'1','2',5,'2024-07-28','dthd','SUCCEEDED','6','5'),
(25,'1','2',89,'2024-07-28','lklklkl','SUCCEEDED','54','7845621'),
(26,'1','2',5,'2024-07-28','f','SUCCEEDED','2','3'),
(27,'1','2',5,'2024-07-28','Mozda sam gresanm ljubavi zeljan','SUCCEEDED','241','465868'),
(28,'1','2',5,'2024-07-28','ASDFGHJKL;XCVBNM,ERTY','SUCCEEDED','0','0'),
(29,'1','2',8,'2024-07-28','HEEEEEEEEEEEEEEEEJ','SUCCEEDED','2','3'),
(30,'1','2',65,'2024-07-28','Bez muzike','SUCCEEDED','52','54546'),
(31,'1','2',8,'2024-07-28','Smile','SUCCEEDED','74','5'),
(32,'1','3',30,'2025-03-05','xfdb','SUCCEEDED','5','4'),
(33,'1','2',30,'2025-03-05','nocas pristajem ja','SUCCEEDED','12','32'),
(34,'1','2',60,'2025-03-09','Desc','SUCCEEDED','89','54'),
(35,'1','2',23568,'2025-03-09','Rezultat kurv','SUCCEEDED','21','36');

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `username` varchar(200) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` enum('ROLE_CLIENT','ROLE_EMPLOYEE') DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `user` */

insert  into `user`(`username`,`password`,`role`) values 
('client1','$2a$12$Nr4AydODOCTzU1b1XlSyXOye5UO75/2KTP2Pcd5X8g2eZqQgM6tjK','ROLE_CLIENT'),
('client2','$2a$12$qAbKs4MY/xVjVdHZ6I6JAeB4E.jZBymZys2B7MGd7zRAInkKj89n.','ROLE_CLIENT'),
('employee1','123','ROLE_EMPLOYEE'),
('employee2','345','ROLE_EMPLOYEE');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
