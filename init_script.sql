DROP TABLE IF EXISTS `leave_balance`;
DROP TABLE IF EXISTS `leave_request`;
DROP TABLE IF EXISTS `leave_categories`;
DROP TABLE IF EXISTS `status`;
DROP TABLE IF EXISTS `employee_supervisor`;
DROP TABLE IF EXISTS `employee`;
DROP TABLE IF EXISTS `authorities`;
DROP TABLE IF EXISTS `roles`;
DROP TABLE IF EXISTS `users`;

-- Table creations

CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `enabled` tinyint NOT NULL,
  PRIMARY KEY (`username`)
) ;

CREATE TABLE `roles` (
    `authority` varchar(50) NOT NULL,
    primary key (`authority`)
);

CREATE TABLE `authorities` (
  `username` varchar(50) NOT NULL,
  `authority` varchar(50) NOT NULL,
  UNIQUE KEY `authorities_idx_1` (`username`,`authority`),
  CONSTRAINT `authorities_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`) on delete cascade,
  CONSTRAINT `authorities_ibfk_2` FOREIGN KEY (`authority`) REFERENCES `roles` (`authority`)
);

CREATE TABLE `leave_categories`
(
	`id` integer (50) NOT NULL auto_increment,
    `title` varchar(50),
    PRIMARY KEY(`id`)
);

CREATE TABLE `employee` (
  `username` varchar(50) NOT NULL,
    FOREIGN KEY (`username`) REFERENCES `users` (`username`) on delete cascade,
  `first_name` char(20) ,
  `last_name` char(20) ,
  `email` char(20) ,
  `mobile_num` char(15),
  `address` varchar(15),
  `address_num`  integer(50),
  PRIMARY KEY (`username`)
);

CREATE TABLE `employee_supervisor` (
  `employee_username` varchar(50),
  `supervisor_username` varchar(50),
  PRIMARY KEY (`employee_username`, `supervisor_username`),
  FOREIGN KEY (`employee_username`) REFERENCES employee (`username`) on delete cascade,
  FOREIGN KEY (`supervisor_username`) REFERENCES users (`username`) on delete cascade
);

CREATE TABLE `status`(
  `id` integer(50) NOT NULL auto_increment,
  `state` varchar(50),
  PRIMARY KEY (`id`)
);

CREATE TABLE `leave_request`(
  `id` integer(50) NOT NULL auto_increment,
  `employee_username` varchar(50) NOT NULL ,
  FOREIGN KEY (`employee_username`) REFERENCES `employee` (`username`) on delete cascade,
  `leave_category_id` integer(50) ,
   FOREIGN KEY (`leave_category_id`) REFERENCES `leave_categories`(`id`),
  `submit_date` date,
  `start_date` date,
  `end_date` date,
  `duration` integer(10),
  `status_id` integer(50),
  FOREIGN KEY (`status_id`) REFERENCES `status` (`id`),
  PRIMARY KEY (`id`)
);

CREATE TABLE `leave_balance`(
  `id` integer(50) NOT NULL auto_increment,
  `employee_username` varchar(50) NOT NULL ,
  FOREIGN KEY (`employee_username`) REFERENCES `employee` (`username`) on delete cascade,
  `leave_category_id` integer(50),
  FOREIGN KEY (`leave_category_id`) REFERENCES `leave_categories`(`id`),
  `days`  integer(50),
  `days_taken`  integer(50),
  PRIMARY KEY (`id`)
);

-- Table populating

INSERT INTO `leave_categories`
VALUES
(1,'Normal'),
(2,'Sick'),
(3,'Parental');

INSERT INTO `status`
VALUES
(1,'PENDING'),
(2,'ACCEPTED'),
(3,'REJECTED');

INSERT INTO `users`
VALUES
('pamvrosiadis','{noop}123',1),
('sspirou','{noop}123',1),
('tvasiliadis','{noop}123',1),
('ntsiridis','{noop}123',1);

INSERT INTO `roles`
VALUES
('ROLE_EMPLOYEE'),
('ROLE_MANAGER'),
('ROLE_ADMIN');

INSERT INTO `authorities`
VALUES
('pamvrosiadis','ROLE_EMPLOYEE'),
('sspirou','ROLE_EMPLOYEE'),
('tvasiliadis','ROLE_EMPLOYEE'),
('ntsiridis','ROLE_EMPLOYEE'),
('ntsiridis','ROLE_MANAGER'),
('ntsiridis','ROLE_ADMIN');

INSERT INTO `employee`
VALUES
('ntsiridis', 'Nikos' ,'Tsiridis' , 'ntsiridis@ots.gr' , 695434546, 'Monasthrioy' , 62),
('sspirou', 'Simos' ,'Spirou' , 'sspirou@ots.gr' , 695434543, 'Monasthrioy' , 61),
('tvasiliadis', 'Theo' ,'Vasileiadis' , 'tvasiliadis@ots.gr' , 695434543, 'Monasthrioy' , 61),
('pamvrosiadis', 'Pavlos' ,'Amvrosiadis' , 'pamvrosiadis@ots.gr' , 695434544 , 'Monasthrioy' , 61);

INSERT INTO `employee_supervisor`
VALUES
('sspirou', 'ntsiridis'),
('tvasiliadis', 'ntsiridis'),
('pamvrosiadis', 'ntsiridis');

INSERT INTO `leave_request`
VALUES
(1, 'pamvrosiadis', 3, current_timestamp(),'2023-06-11','2023-06-14',1, 1),
(2, 'sspirou', 2, current_timestamp(),'2023-06-11','2023-06-12',1, 3),
(3, 'tvasiliadis', 1, current_timestamp(),'2023-06-11','2023-06-15',1, 1),
(4, 'ntsiridis', 2, current_timestamp(),'2023-05-11','2023-05-16',1, 2);


INSERT INTO `leave_balance`
VALUES
(1, 'sspirou', 1, 18, 0),
(2, 'sspirou', 2, 13, 1),
(3, 'pamvrosiadis', 1, 18, 0),
(4, 'pamvrosiadis', 2, 13, 0),
(5, 'pamvrosiadis', 3, 18, 3),
(6, 'tvasiliadis', 1, 18, 0),
(7, 'tvasiliadis', 2, 13, 0),
(8, 'ntsiridis', 1, 18, 0),
(9, 'ntsiridis', 2, 13, 5),
(10, 'ntsiridis', 3, 18, 0);