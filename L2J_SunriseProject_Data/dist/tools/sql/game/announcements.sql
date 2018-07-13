CREATE TABLE IF NOT EXISTS `announcements` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL,
  `initial` bigint(20) NOT NULL DEFAULT 0,
  `delay` bigint(20) NOT NULL DEFAULT 0,
  `repeat` int(11) NOT NULL DEFAULT 0,
  `author` text NOT NULL,
  `content` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO announcements (`type`, `author`, `content`) VALUES 
(1, 'Admin', '------------------------------------'),
(1, 'Admin', 'Welcome to Lineage 2.'),
(1, 'Admin', 'Report us bugs and problems to fix.'),
(1, 'Admin', 'Premium services NPC only in Dion.'),
(1, 'Admin', 'De Level NPC only in Dion.'),
(1, 'Admin', 'Buy premium items in Prime Shop.'),
(1, 'Admin', 'Buy jobs quest items in NPC only in Gludio.'),
(1, 'Admin', 'Have a great game adventurer!.'),
(1, 'Admin', '------------------------------------');
