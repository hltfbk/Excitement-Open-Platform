CREATE DATABASE  IF NOT EXISTS `wikipedialexicalrule` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `wikipedialexicalrule`;
-- MySQL dump 10.13  Distrib 5.5.16, for Win32 (x86)
--
-- Host: localhost    Database: wikipedialexicalrule
-- ------------------------------------------------------
-- Server version	5.5.27-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ruletypes`

DROP TABLE IF EXISTS `ruletypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ruletypes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ruleName` varchar(45) NOT NULL,
  `ruleDescrption` varchar(512) DEFAULT NULL,
  `defultRank` float NOT NULL DEFAULT '0.5',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ruleNameUNIQUE` (`ruleName`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rulepatterns`
--

DROP TABLE IF EXISTS `rulepatterns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rulepatterns` (
  `ruleId` int(11) NOT NULL,
  `POSPattern` varchar(4000) DEFAULT NULL,
  `wordsPattern` varchar(4000) DEFAULT NULL,
  `relationsPattern` varchar(4000) DEFAULT NULL,
  `POSrelationsPattern` varchar(4000) DEFAULT NULL,  
  `fullPattern` varchar(4000) DEFAULT NULL,
  PRIMARY KEY (`ruleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patterncounters`
--

DROP TABLE IF EXISTS `patterncounters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patterncounters` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pattern` varchar(4000) NOT NULL,
  `patternType` int(11) NOT NULL,
  `patternCount` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rules`
--

DROP TABLE IF EXISTS `rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rules` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `leftTermId` int(11) NOT NULL,
  `rightTermId` int(11) NOT NULL,
  `ruleResource` int(11) NOT NULL,
  `ruleType` int(11) NOT NULL,
  `ruleMetadata` varchar(2000) DEFAULT NULL,
  `ruleSourceId` int(11) DEFAULT NULL,
  `serialization` blob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3168379 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `terms`
--

DROP TABLE IF EXISTS `terms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `terms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tokenValUNIQUE` (`value`)
) ENGINE=InnoDB AUTO_INCREMENT=1085418 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patterntypes`
--

DROP TABLE IF EXISTS `patterntypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patterntypes` (
  `id` int(11) NOT NULL,
  `patternName` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rulesranks`
--

DROP TABLE IF EXISTS `rulesranks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rulesranks` (
  `ruleId` int(11) NOT NULL,
  `classifierId` int(11) NOT NULL,
  `rank` float NOT NULL,
  PRIMARY KEY (`classifierId`,`ruleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `classifiers`
--

DROP TABLE IF EXISTS `classifiers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `classifiers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ClassifierName_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ruleresources`
--

DROP TABLE IF EXISTS `ruleresources`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ruleresources` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reSource` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sourceUNIQUE` (`reSource`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Routines for database 
--

DROP FUNCTION IF EXISTS `getClassifierId`;

DELIMITER $$
CREATE FUNCTION `getClassifierId`(classifierName varchar(100) ) RETURNS int(11)
BEGIN

    DECLARE classifierId INT; 
    SELECT id into classifierId from classifiers where name = classifierName;
    if (classifierId IS NULL) then 
        insert into classifiers(name) values(classifierName);
        select LAST_INSERT_ID() into classifierId;
    end if;  
    
    RETURN(classifierId);
END$$

DELIMITER ;
DROP FUNCTION IF EXISTS `insertNewClassifier`;

DELIMITER $$
CREATE FUNCTION `insertNewClassifier`(classifierName varchar(100) ) RETURNS int(11)
BEGIN

    DECLARE classifierId INT; 
    SELECT id into classifierId from classifiers where name = classifierName;
    if (classifierId IS NULL) then 
        insert into classifiers(name) values(classifierName);
        select LAST_INSERT_ID() into classifierId;
    else
        RETURN (-1);
    end if;  
    
    RETURN(classifierId);
END$$

DELIMITER ;
DROP FUNCTION IF EXISTS `insertPatternRule`;

DELIMITER $$
CREATE PROCEDURE `insertPatternRule`(left_Term varchar(200),

right_Term varchar(200), 

rule_Resource varchar(45), 

rule_POS_Pattern varchar(4000),

rule_Words_Pattern varchar(4000),

rule_Relations_Pattern varchar(4000),

rule_POS_Relations_Pattern varchar(4000),

rule_Full_Pattern varchar(4000),

rule_Type varchar(512), rule_Metadata varchar(2000),

rule_Source_Id int, 

serialization_blob blob)
BEGIN

    DECLARE leftTokenId INT;

    DECLARE rightTokenId INT;

    DECLARE ruleResourceId INT;

    DECLARE ruleTypeId INT;

    DECLARE ruleDefultRank float;    

    DECLARE ruleId INT;



    DECLARE POSPatternId INT;

    DECLARE wordsPatternId INT;

    DECLARE relationsPatternId INT;

    DECLARE fullPatternId INT;

    DECLARE fuondPatterns INT;

    

    SET  fuondPatterns = 0;    

    



    SELECT id into leftTokenId from terms where value = left_Term;

    if (leftTokenId IS NULL) then 

        insert into terms(value) values(left_Term);

        select LAST_INSERT_ID() into leftTokenId;

    end if;

    



    SELECT id into rightTokenId from terms where value = right_Term;

    if (rightTokenId IS NULL ) then 

        insert into terms(value) values(right_Term);

        select LAST_INSERT_ID() into rightTokenId;

    end if;





    SELECT id into ruleResourceId from ruleresources where reSource = rule_Resource;

    if (ruleResourceId IS NULL) then 

        insert into ruleresources(reSource) values(rule_Resource);

        select LAST_INSERT_ID() into ruleResourceId;

    end if;







    SELECT id into ruleTypeId from ruletypes where ruleName = rule_type;

    if (ruleTypeId IS NULL) then 

        insert into ruletypes(ruleName) values(rule_type);

        select LAST_INSERT_ID() into ruleTypeId;

    end if;

    

    insert into rules(leftTermId, rightTermId, 

    ruleResource, ruleType, ruleMetadata,ruleSourceId , serialization)

    values(leftTokenId, rightTokenId,

    ruleResourceId,  ruleTypeId, rule_Metadata,rule_Source_Id,

    serialization_blob);

    

    

    /*Insert patterns*/

     select LAST_INSERT_ID() into ruleId;



     if (rule_POS_Pattern is not null) then

        SET  fuondPatterns = 1; 

     end if;



     if (rule_Words_Pattern is not null) then

        SET  fuondPatterns = 1; 

     end if;



     if (rule_Relations_Pattern is not null) then

        SET  fuondPatterns = 1; 

     end if;

     

     if (rule_Full_Pattern is not null) then

        SET  fuondPatterns = 1; 

     end if;



     if (fuondPatterns > 0) then

        insert into rulepatterns(ruleId, POSPattern, wordsPattern, relationsPattern, POSrelationsPattern, fullPattern)

        values (ruleId, rule_POS_Pattern, rule_Words_Pattern, rule_Relations_Pattern, rule_POS_Relations_Pattern, rule_Full_Pattern);

     end if;

    

    commit;

END$$

DELIMITER ;

INSERT INTO `patterntypes` (`id`,`patternName`) VALUES (1,'pos pattern');
INSERT INTO `patterntypes` (`id`,`patternName`) VALUES (2,'words pattern');
INSERT INTO `patterntypes` (`id`,`patternName`) VALUES (3,'relations pattern');
INSERT INTO `patterntypes` (`id`,`patternName`) VALUES (4,'pos_relations pattern');
INSERT INTO `patterntypes` (`id`,`patternName`) VALUES (5,'full pattern');
commit;

