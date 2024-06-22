-- MySQL dump 10.13  Distrib 8.0.37, for Linux (x86_64)
--
-- Host: localhost    Database: pus
-- ------------------------------------------------------
-- Server version	8.0.37-0ubuntu0.22.04.3

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Email_Template_Config`
--

DROP TABLE IF EXISTS `Email_Template_Config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Email_Template_Config` (
  `id` varchar(255) NOT NULL,
  `applicationName` varchar(255) DEFAULT NULL,
  `dynamic_Mail_Body` text NOT NULL,
  `templateName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Email_Template_Config`
--

LOCK TABLES `Email_Template_Config` WRITE;
/*!40000 ALTER TABLE `Email_Template_Config` DISABLE KEYS */;
/*!40000 ALTER TABLE `Email_Template_Config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookmark`
--

DROP TABLE IF EXISTS `bookmark`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookmark` (
  `user_id` bigint NOT NULL,
  `post_id` bigint NOT NULL,
  `saved_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`post_id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`,`saved_at`),
  CONSTRAINT `bookmark_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `bookmark_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookmark`
--

LOCK TABLES `bookmark` WRITE;
/*!40000 ALTER TABLE `bookmark` DISABLE KEYS */;
INSERT INTO `bookmark` VALUES (1,12,'2024-06-22 20:21:29'),(1,13,'2024-06-22 20:38:49'),(1,14,'2024-06-22 20:42:17'),(2,14,'2024-06-22 20:49:16'),(2,13,'2024-06-22 20:49:18'),(2,26,'2024-06-22 21:09:33'),(2,20,'2024-06-22 21:09:34'),(2,12,'2024-06-22 21:09:42'),(2,23,'2024-06-22 21:11:35'),(3,23,'2024-06-22 21:09:46'),(3,14,'2024-06-22 21:09:56'),(3,12,'2024-06-22 21:10:18'),(3,25,'2024-06-22 21:10:19'),(3,20,'2024-06-22 21:10:20'),(3,29,'2024-06-22 21:10:30'),(3,26,'2024-06-22 21:10:31'),(3,17,'2024-06-22 21:10:36');
/*!40000 ALTER TABLE `bookmark` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `post_id` bigint DEFAULT NULL,
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `commented_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `replied_comment_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `post_id` (`post_id`,`commented_at`),
  KEY `replied_comment_id` (`replied_comment_id`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `comment_ibfk_3` FOREIGN KEY (`replied_comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (15,1,13,'wow this is soo coooool','2024-06-22 20:38:40',NULL),(16,1,13,'love this ','2024-06-22 20:38:43',NULL),(17,1,13,'just wow','2024-06-22 20:38:46',NULL),(18,1,13,'hehehe','2024-06-22 20:39:00',NULL),(19,1,13,'nice','2024-06-22 20:39:01',NULL),(20,1,12,'love this','2024-06-22 20:39:06',NULL),(21,2,24,'woww','2024-06-22 21:07:01',NULL),(22,2,24,'so ocoodajfldkjas','2024-06-22 21:07:03',NULL),(23,2,24,'kfdjsalfkj','2024-06-22 21:07:04',NULL),(24,2,24,'fasjlk','2024-06-22 21:07:04',NULL),(25,2,14,'ISN\'T THIS COOOOOL','2024-06-22 21:09:14',NULL),(26,2,14,'wowo','2024-06-22 21:09:18',NULL),(27,2,17,'yee','2024-06-22 21:09:22',NULL),(28,2,20,'not bad','2024-06-22 21:09:28',NULL),(29,3,23,'so cool','2024-06-22 21:09:50',NULL),(30,3,23,'wow','2024-06-22 21:09:51',NULL),(31,3,23,'wow','2024-06-22 21:09:52',NULL),(32,3,23,'wow','2024-06-22 21:09:52',NULL),(33,3,14,'ove this','2024-06-22 21:09:58',NULL),(34,3,14,'love this','2024-06-22 21:10:00',NULL),(35,3,12,'ove this ','2024-06-22 21:10:11',NULL),(36,3,12,'love this','2024-06-22 21:10:12',NULL),(37,3,12,'wow ','2024-06-22 21:10:13',NULL),(38,3,12,'so cool ','2024-06-22 21:10:15',NULL),(39,3,12,'yayyy','2024-06-22 21:10:17',NULL);
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment_like`
--

DROP TABLE IF EXISTS `comment_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_like` (
  `user_id` bigint NOT NULL,
  `comment_id` bigint NOT NULL,
  `liked_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `reaction` enum('LIKE','DISLIKE','LOVE','EMPHASIZE','QUESTION','HAHAHAHA') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`user_id`,`comment_id`),
  KEY `comment_id` (`comment_id`,`liked_at`),
  KEY `reaction` (`reaction`),
  CONSTRAINT `comment_like_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `comment_like_ibfk_2` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_like`
--

LOCK TABLES `comment_like` WRITE;
/*!40000 ALTER TABLE `comment_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `community`
--

DROP TABLE IF EXISTS `community`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `community` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` enum('ISLAMIC_UNIVERSITY_OF_GAZA','ALQUDS_OPEN_UNIVERSITY','BETHLEHEM_UNIVERSITY','ARAB_AMERICAN_UNIVERSITY','ANNAJAH_NATIONAL_UNIVERSITY','UNIVERSITY_OF_PALESTINE','ALQUDS_UNIVERSITY','ALAQSA_UNIVERSITY','PALESTINE_POLYTECHNIC_UNIVERSITY','BIRZEIT_UNIVERSITY','HEBRON_UNIVERSITY','ALAZHAR_UNIVERSITY','ALISTIQLAL_UNIVERSITY','PALESTINE_TECHNICAL_UNIVERSITY_KADOORIE','PALESTINE_TECHNICAL_COLLEGE','PALESTINE_AHLIYA_UNIVERSITY','DAR_ALKALIMA_UNIVERSITY') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `UK_ana370637n4xuuym3v9l6wgkv` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `community`
--

LOCK TABLES `community` WRITE;
/*!40000 ALTER TABLE `community` DISABLE KEYS */;
INSERT INTO `community` VALUES (1,'ISLAMIC_UNIVERSITY_OF_GAZA',NULL),(2,'BETHLEHEM_UNIVERSITY',NULL),(3,'ARAB_AMERICAN_UNIVERSITY',NULL),(4,'ANNAJAH_NATIONAL_UNIVERSITY',NULL),(5,'UNIVERSITY_OF_PALESTINE',NULL),(6,'ALQUDS_UNIVERSITY',NULL),(7,'ALAQSA_UNIVERSITY',NULL),(8,'PALESTINE_POLYTECHNIC_UNIVERSITY',NULL),(9,'BIRZEIT_UNIVERSITY',NULL),(10,'HEBRON_UNIVERSITY',NULL),(11,'ALAZHAR_UNIVERSITY',NULL),(12,'ALISTIQLAL_UNIVERSITY',NULL),(13,'PALESTINE_TECHNICAL_UNIVERSITY_KADOORIE',NULL),(14,'PALESTINE_TECHNICAL_COLLEGE',NULL),(15,'PALESTINE_AHLIYA_UNIVERSITY',NULL),(16,'DAR_ALKALIMA_UNIVERSITY',NULL);
/*!40000 ALTER TABLE `community` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file`
--

DROP TABLE IF EXISTS `file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file` (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `data` longblob,
  `post_id` bigint DEFAULT NULL,
  `story_id` bigint DEFAULT NULL,
  `message_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `community_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`),
  KEY `story_id` (`story_id`),
  KEY `message_id` (`message_id`),
  KEY `user_id` (`user_id`),
  KEY `community_id` (`community_id`),
  CONSTRAINT `file_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `file_ibfk_2` FOREIGN KEY (`story_id`) REFERENCES `story` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `file_ibfk_3` FOREIGN KEY (`message_id`) REFERENCES `message` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `file_ibfk_4` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `file_ibfk_5` FOREIGN KEY (`community_id`) REFERENCES `community` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file`
--

LOCK TABLES `file` WRITE;
/*!40000 ALTER TABLE `file` DISABLE KEYS */;
/*!40000 ALTER TABLE `file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `follower`
--

DROP TABLE IF EXISTS `follower`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follower` (
  `follower_id` bigint NOT NULL,
  `followed_id` bigint NOT NULL,
  `followed_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`follower_id`,`followed_id`),
  KEY `followed_id` (`followed_id`),
  KEY `follower_id` (`follower_id`),
  CONSTRAINT `follower_ibfk_1` FOREIGN KEY (`follower_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `follower_ibfk_2` FOREIGN KEY (`followed_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follower`
--

LOCK TABLES `follower` WRITE;
/*!40000 ALTER TABLE `follower` DISABLE KEYS */;
INSERT INTO `follower` VALUES (1,2,'2024-06-22 15:23:54'),(1,3,'2024-06-22 15:38:49'),(1,4,'2024-06-20 19:41:21'),(1,6,'2024-06-22 20:50:35'),(1,7,'2024-06-22 20:50:43'),(1,9,'2024-06-22 20:50:55'),(1,12,'2024-06-20 22:51:13'),(2,1,'2024-06-22 21:08:19'),(2,3,'2024-06-20 19:41:21'),(2,4,'2024-06-20 19:41:21'),(2,5,'2024-06-22 21:08:53'),(2,6,'2024-06-22 21:08:46'),(3,2,'2024-06-20 19:41:21'),(3,4,'2024-06-20 19:41:21'),(4,1,'2024-06-20 19:41:21');
/*!40000 ALTER TABLE `follower` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_chat`
--

DROP TABLE IF EXISTS `group_chat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_chat` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_chat`
--

LOCK TABLES `group_chat` WRITE;
/*!40000 ALTER TABLE `group_chat` DISABLE KEYS */;
INSERT INTO `group_chat` VALUES (1,'2024-06-20 19:41:21','The Gangggg');
/*!40000 ALTER TABLE `group_chat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_member`
--

DROP TABLE IF EXISTS `group_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_member` (
  `user_id` bigint NOT NULL,
  `group_id` bigint NOT NULL,
  `member_since` datetime NOT NULL,
  `role` enum('ADMIN','MEMBER') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`user_id`,`group_id`),
  KEY `group_id` (`group_id`,`role`),
  CONSTRAINT `group_member_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `group_member_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `group_chat` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_member`
--

LOCK TABLES `group_member` WRITE;
/*!40000 ALTER TABLE `group_member` DISABLE KEYS */;
INSERT INTO `group_member` VALUES (1,1,'2024-06-20 19:41:21','ADMIN'),(2,1,'2024-06-20 19:41:21','MEMBER'),(3,1,'2024-06-20 19:41:21','ADMIN'),(4,1,'2024-06-20 19:41:21','MEMBER');
/*!40000 ALTER TABLE `group_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `major_community`
--

DROP TABLE IF EXISTS `major_community`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `major_community` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `major` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `year` bigint NOT NULL,
  `major_group_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `major` (`major`,`year`),
  UNIQUE KEY `major_group_id` (`major_group_id`),
  CONSTRAINT `major_community_ibfk_1` FOREIGN KEY (`major_group_id`) REFERENCES `group_chat` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `major_community_chk_1` CHECK ((`year` >= 2014))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `major_community`
--

LOCK TABLES `major_community` WRITE;
/*!40000 ALTER TABLE `major_community` DISABLE KEYS */;
/*!40000 ALTER TABLE `major_community` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `message_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `sent_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_read` tinyint(1) NOT NULL,
  `receiver_id` bigint DEFAULT NULL,
  `sender_id` bigint DEFAULT NULL,
  `group_id` bigint DEFAULT NULL,
  `receiver_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `receiver_id` (`receiver_id`,`sent_at`),
  KEY `sender_id` (`sender_id`,`sent_at`),
  KEY `group_id` (`group_id`,`sent_at`),
  KEY `receiver_id_2` (`receiver_id`,`sender_id`),
  CONSTRAINT `message_ibfk_1` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `message_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `message_ibfk_3` FOREIGN KEY (`group_id`) REFERENCES `group_chat` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (33,'👋😊','2024-06-22 20:43:26',0,NULL,3,1,'GROUP_CHAT',NULL),(34,'hello everyone','2024-06-22 20:44:03',0,NULL,4,1,'GROUP_CHAT',NULL),(35,'hello','2024-06-22 20:44:08',0,NULL,1,1,'GROUP_CHAT',NULL),(36,'helllooooo','2024-06-22 20:44:18',0,NULL,3,1,'GROUP_CHAT',NULL),(37,'hiiiii','2024-06-22 20:44:23',0,NULL,2,1,'GROUP_CHAT',NULL),(38,'isn\'t this cooool','2024-06-22 20:44:39',0,NULL,2,1,'GROUP_CHAT',NULL),(39,'this is sooo cooool','2024-06-22 20:44:45',0,NULL,4,1,'GROUP_CHAT',NULL),(40,'I like thisss','2024-06-22 20:44:50',0,NULL,3,1,'GROUP_CHAT',NULL),(41,'woww this iss awesomeeee','2024-06-22 20:44:59',0,NULL,1,1,'GROUP_CHAT',NULL),(42,'is this working?','2024-06-22 20:45:29',0,NULL,3,1,'GROUP_CHAT',NULL),(43,'yes, aboslutely!!','2024-06-22 20:45:35',0,NULL,1,1,'GROUP_CHAT',NULL),(44,'I like thisss','2024-06-22 20:45:39',0,NULL,2,1,'GROUP_CHAT',NULL),(45,'👋😊','2024-06-22 20:46:39',1,3,1,NULL,'USER',NULL),(46,'hello ehab','2024-06-22 20:46:50',1,1,3,NULL,'USER',NULL),(47,'yoooo hasasn how are you ','2024-06-22 20:46:58',1,3,1,NULL,'USER',NULL),(48,'i\'m fine how about you','2024-06-22 20:47:03',1,1,3,NULL,'USER',NULL),(49,'all goood','2024-06-22 20:47:05',1,3,1,NULL,'USER',NULL),(50,'hehe','2024-06-22 20:47:07',1,1,3,NULL,'USER',NULL),(51,'app is working fine','2024-06-22 20:47:12',1,1,3,NULL,'USER',NULL),(52,'awesome init','2024-06-22 20:47:15',1,3,1,NULL,'USER',NULL),(53,'👋😊','2024-06-22 21:02:27',1,3,2,NULL,'USER',NULL),(54,'👋😊','2024-06-22 21:02:35',0,1,2,NULL,'USER',NULL),(55,'👋😊','2024-06-22 21:02:37',0,4,2,NULL,'USER',NULL),(56,'hi','2024-06-22 21:02:41',0,1,2,NULL,'USER',NULL),(57,'how are you','2024-06-22 21:02:43',0,1,2,NULL,'USER',NULL),(58,'let\'\'ssss gooo','2024-06-22 21:02:55',0,1,3,NULL,'USER',NULL),(59,'yooooo wasssuppp','2024-06-22 21:03:01',0,2,3,NULL,'USER',NULL),(60,'👋😊','2024-06-22 21:03:04',0,4,3,NULL,'USER',NULL),(61,'yooo wassup','2024-06-22 21:04:39',0,4,3,NULL,'USER',NULL),(62,'this is soo cool ','2024-06-22 21:04:43',0,4,3,NULL,'USER',NULL),(63,'wanna hangout','2024-06-22 21:04:57',0,2,3,NULL,'USER',NULL);
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `notification_type` enum('FOLLOW_REQUEST','POST_SHARE','POST_LIKE','STORY_LIKE','STORY','STORY_REPLY','COMMENT_REPLY','COMMENT','COMMENT_LIKE','MESSAGE','GROUP_MESSAGE') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `notified_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `notification_type` (`notification_type`),
  KEY `user_id` (`user_id`,`notified_at`),
  CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=96 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (41,'POST_LIKE','bsbs liked your post',1,'2024-06-22 20:21:28'),(42,'POST_LIKE','rawang liked your post',1,'2024-06-22 20:23:12'),(43,'COMMENT','bsbs commented on your post',4,'2024-06-22 20:38:40'),(44,'COMMENT','bsbs commented on your post',4,'2024-06-22 20:38:43'),(45,'COMMENT','bsbs commented on your post',4,'2024-06-22 20:38:46'),(46,'POST_LIKE','bsbs liked your post',4,'2024-06-22 20:38:48'),(47,'POST_LIKE','bsbs liked your post',4,'2024-06-22 20:38:52'),(48,'COMMENT','bsbs commented on your post',4,'2024-06-22 20:39:00'),(49,'COMMENT','bsbs commented on your post',4,'2024-06-22 20:39:01'),(50,'COMMENT','bsbs commented on your post',1,'2024-06-22 20:39:06'),(51,'POST_LIKE','bsbs liked your post',4,'2024-06-22 20:42:16'),(52,'POST_LIKE','nadineodeh liked your post',4,'2024-06-22 20:49:03'),(53,'POST_LIKE','nadineodeh liked your post',4,'2024-06-22 20:49:04'),(54,'FOLLOW_REQUEST','bsbs followed you',6,'2024-06-22 20:50:35'),(55,'FOLLOW_REQUEST','bsbs followed you',7,'2024-06-22 20:50:43'),(56,'FOLLOW_REQUEST','bsbs followed you',9,'2024-06-22 20:50:55'),(57,'POST_LIKE','nadineodeh liked your post',3,'2024-06-22 21:06:49'),(58,'POST_LIKE','nadineodeh liked your post',3,'2024-06-22 21:06:53'),(59,'POST_LIKE','nadineodeh liked your post',3,'2024-06-22 21:06:56'),(60,'POST_LIKE','nadineodeh liked your post',3,'2024-06-22 21:06:58'),(61,'COMMENT','nadineodeh commented on your post',3,'2024-06-22 21:07:01'),(62,'COMMENT','nadineodeh commented on your post',3,'2024-06-22 21:07:03'),(63,'COMMENT','nadineodeh commented on your post',3,'2024-06-22 21:07:04'),(64,'COMMENT','nadineodeh commented on your post',3,'2024-06-22 21:07:04'),(65,'FOLLOW_REQUEST','nadineodeh followed you',1,'2024-06-22 21:08:19'),(66,'FOLLOW_REQUEST','nadineodeh followed you',6,'2024-06-22 21:08:46'),(67,'FOLLOW_REQUEST','nadineodeh followed you',5,'2024-06-22 21:08:53'),(68,'COMMENT','nadineodeh commented on your post',4,'2024-06-22 21:09:14'),(69,'COMMENT','nadineodeh commented on your post',4,'2024-06-22 21:09:18'),(70,'COMMENT','nadineodeh commented on your post',3,'2024-06-22 21:09:22'),(71,'POST_LIKE','nadineodeh liked your post',1,'2024-06-22 21:09:25'),(72,'COMMENT','nadineodeh commented on your post',1,'2024-06-22 21:09:28'),(73,'POST_LIKE','nadineodeh liked your post',6,'2024-06-22 21:09:32'),(74,'POST_LIKE','nadineodeh liked your post',5,'2024-06-22 21:09:36'),(75,'POST_LIKE','nadineodeh liked your post',1,'2024-06-22 21:09:42'),(76,'POST_LIKE','hguts liked your post',2,'2024-06-22 21:09:47'),(77,'COMMENT','hguts commented on your post',2,'2024-06-22 21:09:50'),(78,'COMMENT','hguts commented on your post',2,'2024-06-22 21:09:51'),(79,'COMMENT','hguts commented on your post',2,'2024-06-22 21:09:52'),(80,'COMMENT','hguts commented on your post',2,'2024-06-22 21:09:52'),(81,'POST_LIKE','hguts liked your post',4,'2024-06-22 21:09:55'),(82,'COMMENT','hguts commented on your post',4,'2024-06-22 21:09:58'),(83,'COMMENT','hguts commented on your post',4,'2024-06-22 21:10:00'),(84,'POST_LIKE','hguts liked your post',1,'2024-06-22 21:10:06'),(85,'POST_LIKE','hguts liked your post',1,'2024-06-22 21:10:07'),(86,'POST_LIKE','hguts liked your post',1,'2024-06-22 21:10:09'),(87,'COMMENT','hguts commented on your post',1,'2024-06-22 21:10:11'),(88,'COMMENT','hguts commented on your post',1,'2024-06-22 21:10:12'),(89,'COMMENT','hguts commented on your post',1,'2024-06-22 21:10:13'),(90,'COMMENT','hguts commented on your post',1,'2024-06-22 21:10:15'),(91,'COMMENT','hguts commented on your post',1,'2024-06-22 21:10:17'),(92,'POST_LIKE','hguts liked your post',5,'2024-06-22 21:10:30'),(93,'POST_LIKE','hguts liked your post',6,'2024-06-22 21:10:32'),(94,'POST_LIKE','hguts liked your post',3,'2024-06-22 21:10:36'),(95,'POST_LIKE','nadineodeh liked your post',2,'2024-06-22 21:11:36');
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post`
--

DROP TABLE IF EXISTS `post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `caption` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `view` enum('COMMUNITY','USER','PUBLIC') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `community_id` bigint DEFAULT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `view` (`view`,`created_at`),
  KEY `user_id` (`user_id`,`created_at`),
  KEY `community_id` (`community_id`,`created_at`),
  CONSTRAINT `post_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `post_ibfk_2` FOREIGN KEY (`community_id`) REFERENCES `community` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post`
--

LOCK TABLES `post` WRITE;
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
INSERT INTO `post` VALUES (12,'2024-06-22 20:21:22',NULL,'cool house admist water','PUBLIC',1,NULL,'http://localhost:8080/files/wallpaperflare.com_wallpaper(2).jpg'),(13,'2024-06-22 20:22:15',NULL,'wowwwww','PUBLIC',4,NULL,'http://localhost:8080/files/sunrise-scenery-chill-coffee-bart-simpson-digital-art-hd-wallpaper-uhdpaper.com-783@0@g.jpg'),(14,'2024-06-22 20:40:06',NULL,'hello everybody','COMMUNITY',4,2,'http://localhost:8080/files/lofi-chillhop-hd-wallpaper-uhdpaper.com-12@0@i.jpg'),(15,'2024-06-22 20:21:22',NULL,'cool house admist water','PUBLIC',3,NULL,'http://localhost:8080/files/wallpaperflare.com_wallpaper(2).jpg'),(16,'2024-06-22 20:22:15',NULL,'wowwwww','PUBLIC',2,NULL,'http://localhost:8080/files/sunrise-scenery-chill-coffee-bart-simpson-digital-art-hd-wallpaper-uhdpaper.com-783@0@g.jpg'),(17,'2024-06-22 20:40:06',NULL,'hello everybody','COMMUNITY',3,2,'http://localhost:8080/files/lofi-chillhop-hd-wallpaper-uhdpaper.com-12@0@i.jpg'),(18,'2024-06-22 20:21:22',NULL,'cool house admist water','PUBLIC',2,NULL,'http://localhost:8080/files/wallpaperflare.com_wallpaper(2).jpg'),(19,'2024-06-22 20:22:15',NULL,'wowwwww','PUBLIC',3,NULL,'http://localhost:8080/files/sunrise-scenery-chill-coffee-bart-simpson-digital-art-hd-wallpaper-uhdpaper.com-783@0@g.jpg'),(20,'2024-06-22 20:40:06',NULL,'hello everybody','COMMUNITY',1,2,'http://localhost:8080/files/lofi-chillhop-hd-wallpaper-uhdpaper.com-12@0@i.jpg'),(21,'2024-06-22 20:21:22',NULL,'cool house admist water','PUBLIC',4,NULL,'http://localhost:8080/files/wallpaperflare.com_wallpaper(2).jpg'),(22,'2024-06-22 20:22:15',NULL,'wowwwww','PUBLIC',5,NULL,'http://localhost:8080/files/sunrise-scenery-chill-coffee-bart-simpson-digital-art-hd-wallpaper-uhdpaper.com-783@0@g.jpg'),(23,'2024-06-22 20:40:06',NULL,'hello everybody','COMMUNITY',2,2,'http://localhost:8080/files/lofi-chillhop-hd-wallpaper-uhdpaper.com-12@0@i.jpg'),(24,'2024-06-22 20:21:22',NULL,'cool house admist water','PUBLIC',3,NULL,'http://localhost:8080/files/wallpaperflare.com_wallpaper(2).jpg'),(25,'2024-06-22 20:22:15',NULL,'wowwwww','PUBLIC',1,NULL,'http://localhost:8080/files/sunrise-scenery-chill-coffee-bart-simpson-digital-art-hd-wallpaper-uhdpaper.com-783@0@g.jpg'),(26,'2024-06-22 20:40:06',NULL,'hello everybody','COMMUNITY',6,2,'http://localhost:8080/files/lofi-chillhop-hd-wallpaper-uhdpaper.com-12@0@i.jpg'),(27,'2024-06-22 20:21:22',NULL,'cool house admist water','PUBLIC',6,NULL,'http://localhost:8080/files/wallpaperflare.com_wallpaper(2).jpg'),(28,'2024-06-22 20:22:15',NULL,'wowwwww','PUBLIC',6,NULL,'http://localhost:8080/files/sunrise-scenery-chill-coffee-bart-simpson-digital-art-hd-wallpaper-uhdpaper.com-783@0@g.jpg'),(29,'2024-06-22 20:40:06',NULL,'hello everybody','COMMUNITY',5,2,'http://localhost:8080/files/lofi-chillhop-hd-wallpaper-uhdpaper.com-12@0@i.jpg'),(30,'2024-06-22 21:07:52',NULL,'WHO WANNA HANGOUT','PUBLIC',2,NULL,NULL);
/*!40000 ALTER TABLE `post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_like`
--

DROP TABLE IF EXISTS `post_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_like` (
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `liked_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `reaction` enum('LIKE','DISLIKE','LOVE','EMPHASIZE','QUESTION','HAHAHAHA') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`post_id`,`user_id`),
  KEY `user_id` (`user_id`),
  KEY `post_id` (`post_id`),
  KEY `reaction` (`reaction`),
  CONSTRAINT `post_like_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `post_like_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_like`
--

LOCK TABLES `post_like` WRITE;
/*!40000 ALTER TABLE `post_like` DISABLE KEYS */;
INSERT INTO `post_like` VALUES (12,1,'2024-06-22 20:21:28','LIKE'),(12,2,'2024-06-22 21:09:42','LIKE'),(12,3,'2024-06-22 21:10:09','LIKE'),(12,4,'2024-06-22 20:23:12','LIKE'),(13,1,'2024-06-22 20:38:52','LIKE'),(13,2,'2024-06-22 20:49:04','LIKE'),(14,1,'2024-06-22 20:42:16','LIKE'),(14,2,'2024-06-22 20:49:03','LIKE'),(14,3,'2024-06-22 21:09:55','LIKE'),(15,2,'2024-06-22 21:06:56','LIKE'),(17,2,'2024-06-22 21:06:49','LIKE'),(17,3,'2024-06-22 21:10:36','LIKE'),(19,2,'2024-06-22 21:06:53','LIKE'),(20,2,'2024-06-22 21:09:25','LIKE'),(20,3,'2024-06-22 21:10:06','LIKE'),(23,2,'2024-06-22 21:11:36','LIKE'),(23,3,'2024-06-22 21:09:47','LIKE'),(24,2,'2024-06-22 21:06:58','LIKE'),(25,3,'2024-06-22 21:10:07','LIKE'),(26,2,'2024-06-22 21:09:31','LIKE'),(26,3,'2024-06-22 21:10:32','LIKE'),(29,2,'2024-06-22 21:09:36','LIKE'),(29,3,'2024-06-22 21:10:29','LIKE');
/*!40000 ALTER TABLE `post_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reply_to_story`
--

DROP TABLE IF EXISTS `reply_to_story`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reply_to_story` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `reply_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `story_id` bigint NOT NULL,
  `replied_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `message_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `message_id` (`message_id`),
  KEY `user_id` (`user_id`),
  KEY `story_id` (`story_id`,`replied_at`),
  CONSTRAINT `reply_to_story_ibfk_1` FOREIGN KEY (`message_id`) REFERENCES `message` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `reply_to_story_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `reply_to_story_ibfk_3` FOREIGN KEY (`story_id`) REFERENCES `story` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reply_to_story`
--

LOCK TABLES `reply_to_story` WRITE;
/*!40000 ALTER TABLE `reply_to_story` DISABLE KEYS */;
/*!40000 ALTER TABLE `reply_to_story` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `section_community`
--

DROP TABLE IF EXISTS `section_community`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `section_community` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `division` bigint NOT NULL,
  `year` bigint NOT NULL,
  `semester` bigint NOT NULL,
  `section_group_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `course` (`course`,`division`,`year`,`semester`),
  KEY `section_group_id` (`section_group_id`),
  CONSTRAINT `section_community_ibfk_1` FOREIGN KEY (`section_group_id`) REFERENCES `group_chat` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `section_community_chk_1` CHECK ((`year` >= 2024)),
  CONSTRAINT `section_community_chk_2` CHECK ((`semester` between 1 and 2))
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `section_community`
--

LOCK TABLES `section_community` WRITE;
/*!40000 ALTER TABLE `section_community` DISABLE KEYS */;
/*!40000 ALTER TABLE `section_community` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `share_post`
--

DROP TABLE IF EXISTS `share_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `share_post` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `post_id` bigint NOT NULL,
  `shared_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `caption` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`,`shared_at`),
  CONSTRAINT `share_post_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `share_post_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `share_post`
--

LOCK TABLES `share_post` WRITE;
/*!40000 ALTER TABLE `share_post` DISABLE KEYS */;
/*!40000 ALTER TABLE `share_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `story`
--

DROP TABLE IF EXISTS `story`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `story` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `caption` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `view` enum('COMMUNITY','USER','PUBLIC') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `community_id` bigint DEFAULT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `view` (`view`,`created_at`),
  KEY `user_id` (`user_id`,`created_at`),
  KEY `community_id` (`community_id`,`created_at`),
  CONSTRAINT `story_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `story_ibfk_2` FOREIGN KEY (`community_id`) REFERENCES `community` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `story`
--

LOCK TABLES `story` WRITE;
/*!40000 ALTER TABLE `story` DISABLE KEYS */;
INSERT INTO `story` VALUES (20,'2024-06-22 20:21:03',NULL,NULL,'PUBLIC',1,NULL,'http://localhost:8080/files/kobby-mendez-d0oYF8hm4GI-unsplash.jpg'),(21,'2024-06-22 20:23:39',NULL,NULL,'PUBLIC',4,NULL,'http://localhost:8080/files/wallpaperflare.com_wallpaper(1).jpg'),(22,'2024-06-22 21:05:23',NULL,NULL,'PUBLIC',3,NULL,'http://localhost:8080/files/wallpaperflare.com_wallpaper(8).jpg'),(23,'2024-06-22 21:05:46',NULL,NULL,'PUBLIC',3,NULL,'http://localhost:8080/files/shovel-knight-bonfire-pixel-art-hd-wallpaper-uhdpaper.com-458@0@h.jpg'),(24,'2024-06-22 21:06:00',NULL,NULL,'PUBLIC',3,NULL,'http://localhost:8080/files/astronaut-space-moon-digital-art-hd-wallpaper-uhdpaper.com-282@0@g.jpg'),(25,'2024-06-22 21:06:17',NULL,NULL,'PUBLIC',2,NULL,'http://localhost:8080/files/wallpaperflare.com_wallpaper(9).jpg'),(26,'2024-06-22 21:06:32',NULL,NULL,'PUBLIC',2,NULL,'http://localhost:8080/files/sunrise-scenery-chill-coffee-bart-simpson-digital-art-hd-wallpaper-uhdpaper.com-783@0@g.jpg'),(27,'2024-06-22 21:06:41',NULL,NULL,'PUBLIC',2,NULL,'http://localhost:8080/files/lofi-raccoon-digital-art-hd-wallpaper-uhdpaper.com-448@0@h.jpg');
/*!40000 ALTER TABLE `story` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `story_like`
--

DROP TABLE IF EXISTS `story_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `story_like` (
  `user_id` bigint NOT NULL,
  `story_id` bigint NOT NULL,
  `reaction` enum('LIKE','DISLIKE','LOVE','EMPHASIZE','QUESTION','HAHAHAHA') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `liked_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`story_id`),
  KEY `story_id` (`story_id`),
  CONSTRAINT `story_like_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `story_like_ibfk_2` FOREIGN KEY (`story_id`) REFERENCES `story` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `story_like`
--

LOCK TABLES `story_like` WRITE;
/*!40000 ALTER TABLE `story_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `story_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `fullname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '/assets/profile-placeholder.svg',
  `gender` enum('MALE','FEMALE') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `major` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `is_active` enum('OFFLINE','ONLINE','SLEEP','WORKING') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `bio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `community_id` bigint DEFAULT NULL,
  `is_verified` bit(1) NOT NULL,
  `verificationCode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `id` (`id`),
  KEY `username_2` (`username`),
  KEY `community_id` (`community_id`),
  KEY `email_2` (`email`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`community_id`) REFERENCES `community` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'bsbs','Ehab Maali','/assets/profile-placeholder.svg','MALE','BACHELOR_IN_SOFTWARE_ENGINEERING','2024-06-20','ehab1.maali@gmail.com','$2a$10$ksb8pFn5GMnFB5v6hDdp1OECS7qdYAAZNVvW6QSUfYQHenfBtCoKW','ONLINE','Lorem ipsum dolor sit amet, consectetur adipiscing elit.',2,_binary '',NULL,''),(2,'nadineodeh','Nadine Abu Odeh','/assets/profile-placeholder.svg','FEMALE','BACHELOR_IN_SOFTWARE_ENGINEERING','2024-06-20','user2@bethlehem.edu','$2a$10$gTpjLPd.VA8nqojX9JTTNuKp59L7Q4ZtkY6TSrmG7qFtNuqlUTiXS','ONLINE','Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.',2,_binary '',NULL,''),(3,'hguts','Hasan Zawahra','/assets/profile-placeholder.svg','MALE','BACHELOR_IN_SOFTWARE_ENGINEERING','2024-06-20','user3@bethlehem.edu','$2a$10$KBA.5eYPlds2KjbnuqVnX.usRc/2rE7YVqXMfQIMR8/gwyFVCS6F2','OFFLINE','Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',2,_binary '',NULL,''),(4,'rawang','Rawan Gedeon','/assets/profile-placeholder.svg','FEMALE','BACHELOR_IN_SOFTWARE_ENGINEERING','2024-06-20','user4@bethlehem.edu','$2a$10$eOvMRVMXMu1RLLbQ6ow2sejpEzovHhoWWA8GoGBEXtMGYcWkFZzoy','SLEEP','Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.',2,_binary '',NULL,''),(5,'sanas','Anas Samara','/assets/profile-placeholder.svg','MALE','BACHELOR_IN_SOFTWARE_ENGINEERING','2024-06-20','user5@bethlehem.edu','$2a$10$3gj1Gy2j.1c0lYaEw2JJPejb5WvujQYa8iL.PgZ2x2AZ4n2v6X48W','WORKING','Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',2,_binary '',NULL,''),(6,'user6','Michael Davis','/assets/profile-placeholder.svg','MALE','BACHELOR_IN_SOFTWARE_ENGINEERING','2024-06-20','user6@bethlehem.edu','$2a$10$CygRqGr1TDYZBXGAQP5QG.gW/FfWFWyjoNy5bV7glku4QzxeqScge','ONLINE','Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi.',2,_binary '',NULL,'');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `viewer`
--

DROP TABLE IF EXISTS `viewer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `viewer` (
  `user_id` bigint NOT NULL,
  `story_id` bigint NOT NULL,
  `viewed_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `viewedAt` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`story_id`),
  KEY `story_id` (`story_id`,`viewed_at`),
  CONSTRAINT `viewer_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `viewer_ibfk_2` FOREIGN KEY (`story_id`) REFERENCES `story` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `viewer`
--

LOCK TABLES `viewer` WRITE;
/*!40000 ALTER TABLE `viewer` DISABLE KEYS */;
INSERT INTO `viewer` VALUES (1,20,'2024-06-22 20:23:31','2024-06-22 17:23:31.839000'),(1,21,'2024-06-22 20:23:31','2024-06-22 17:23:31.839000'),(2,20,'2024-06-22 20:23:31','2024-06-22 17:23:31.839000'),(2,21,'2024-06-22 20:23:31','2024-06-22 17:23:31.839000'),(3,20,'2024-06-22 20:23:31','2024-06-22 17:23:31.839000'),(3,21,'2024-06-22 20:23:31','2024-06-22 17:23:31.839000'),(4,20,'2024-06-22 20:23:31','2024-06-22 17:23:31.839000'),(4,21,'2024-06-22 20:23:31','2024-06-22 17:23:31.839000');
/*!40000 ALTER TABLE `viewer` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-06-22 21:13:51
