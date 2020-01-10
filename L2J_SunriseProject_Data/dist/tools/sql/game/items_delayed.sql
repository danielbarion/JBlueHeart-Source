CREATE TABLE `items_delayed`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `itemId` int(11) NOT NULL,
  `itemCount` int(11) NOT NULL,
  `charId` int(11) NOT NULL,
  `delivered` int(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
);