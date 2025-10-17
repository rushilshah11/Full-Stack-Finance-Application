

DROP TABLE IF EXISTS `Portfolio`;

CREATE TABLE `Portfolio` (
  `trade_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `ticker` varchar(45) DEFAULT NULL,
  `numStock` int DEFAULT NULL,
  `price` float DEFAULT NULL,
  PRIMARY KEY (`trade_id`)
); 


INSERT INTO `Portfolio` VALUES (1,1,'COIN',6,213.78),(2,1,'COIN',6,213.78),(3,1,'MSFT',3,404.27),(4,1,'COIN',-1,225.86),(5,1,'COIN',-1,225.86),(6,1,'COIN',-1,225.86),(7,1,'COIN',-1,225.86),(8,1,'COIN',-1,225.86),(9,1,'COIN',-1,225.86),(10,1,'COIN',-1,225.86),(11,1,'COIN',-1,225.86),(12,1,'COIN',-1,225.86),(13,1,'MSFT',1,400.96),(14,1,'MSFT',1,400.96),(15,1,'COIN',1,225.86),(16,1,'MSFT',4,400.96),(17,2,'AAPL',3,165.84),(18,2,'TSLA',3,142.05),(19,2,'NVDA',3,795.18),(20,3,'AAPL',3,165.84),(21,3,'NVDA',3,795.18),(22,3,'TSLA',3,142.05),(23,3,'NVDA',-2,795.18),(24,3,'AAPL',3,165.84),(25,3,'TSLA',3,142.05),(26,3,'TSLA',3,142.05),(27,3,'AAPL',4,165.84),(28,3,'TSLA',-3,142.05),(29,3,'TSLA',-3,142.05),(30,3,'AAPL',-6,165.84),(31,3,'TSLA',-3,142.05),(32,3,'AAPL',-1,165.84),(33,3,'AAPL',3,165.84),(34,14,'COIN',5,225.86),(35,2,'TSLA',-3,145.42),(36,16,'COIN',2,236.394),(37,16,'NVDA',6,823.42),(38,16,'COIN',-2,236.895),(39,16,'NVDA',-6,823.705),(40,7,'AAPL',3,166.825),(41,7,'TSLA',3,144.614),(42,7,'NVDA',3,824.93),(43,7,'NVDA',-2,824.915),(44,7,'AAPL',3,166.79),(45,7,'TSLA',-3,144.82),(46,18,'COIN',5,235.79),(47,17,'AAPL',3,166.86),(48,17,'TSLA',3,144.81),(49,17,'NVDA',3,823.982),(50,17,'NVDA',-2,824),(51,17,'AAPL',3,166.986),(52,17,'TSLA',-3,144.93),(53,19,'COIN',5,235.39),(54,17,'AAPL',3,166.79),(55,17,'AAPL',8,166.79),(56,17,'NVDA',1,822.87);


DROP TABLE IF EXISTS `Users`;

CREATE TABLE `Users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `balance` double DEFAULT NULL,
  PRIMARY KEY (`user_id`)
); 


LOCK TABLES `Users` WRITE;

INSERT INTO `Users` VALUES (1,'rushil','rushil','rushil@gmail.com',45623),(2,'kenny','kenny','kenny@gmail.com',47126),(3,'j','j','johnson@gmail.com',48208),(4,'harsh','h','harsh@gmail.com',50000),(5,'andrew','a','andrew@gmail.com',50000),(6,'andre','an','andre@gmail.com',50000),(7,'user','user','user@gmail.com',48174.81388291016),(8,'u','uu','u@gmail.com',50000),(9,'v','v','v@gmail.com',50000),(10,'c','c','c@gmail.com',50000),(11,'','','s',50000),(12,'rushil123','rushil123','f@gmail.com',50000),(13,'test','test','test@gmail.com',50000),(14,'test2','test2','test2@gmail.com',48871),(15,'test3','test3','test3@gmail.com',50000),(16,'newUser','newUser','newUser@gmail.com',50002.711989013675),(17,'video','video','video@gmail.com',45517.31574433593),(18,'test5','test5','test5@gmail.com',48821.05),(19,'newVideoUser','newVideoUser','newVideoUser@gmail.com',48823.05);

UNLOCK TABLES;

