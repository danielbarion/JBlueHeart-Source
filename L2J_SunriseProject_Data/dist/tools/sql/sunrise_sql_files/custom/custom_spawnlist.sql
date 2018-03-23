/*
Navicat MySQL Data Transfer

Source Server         : ServerConnection
Source Server Version : 50528
Source Host           : localhost:3306
Source Database       : l2jsunrisegs

Target Server Type    : MYSQL
Target Server Version : 50528
File Encoding         : 65001

Date: 2013-07-25 22:20:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `custom_spawnlist`
-- ----------------------------
DROP TABLE IF EXISTS `custom_spawnlist`;
CREATE TABLE `custom_spawnlist` (
  `location` varchar(40) NOT NULL DEFAULT '',
  `spawn_name` varchar(100) NOT NULL DEFAULT '',
  `count` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `npc_templateid` mediumint(7) unsigned NOT NULL DEFAULT '0',
  `locx` mediumint(6) NOT NULL DEFAULT '0',
  `locy` mediumint(6) NOT NULL DEFAULT '0',
  `locz` mediumint(6) NOT NULL DEFAULT '0',
  `randomx` mediumint(6) NOT NULL DEFAULT '0',
  `randomy` mediumint(6) NOT NULL DEFAULT '0',
  `heading` mediumint(6) NOT NULL DEFAULT '0',
  `respawn_delay` mediumint(5) NOT NULL DEFAULT '0',
  `respawn_random` mediumint(5) NOT NULL DEFAULT '0',
  `loc_id` int(9) NOT NULL DEFAULT '0',
  `periodOfDay` tinyint(1) unsigned NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of custom_spawnlist
-- ----------------------------
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '50007', '-113229', '-245204', '-15537', '0', '0', '17055', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '31271', '-113085', '-245205', '-15537', '0', '0', '16383', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '30627', '-112726', '-245204', '-15537', '0', '0', '15673', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '30627', '-112726', '-244031', '-15537', '0', '0', '15673', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '30627', '-114197', '-244031', '-15537', '0', '0', '55863', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '30627', '-114197', '-245204', '-15537', '0', '0', '11796', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '32238', '-114163', '-245278', '-15537', '0', '0', '39866', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '32238', '-114281', '-245253', '-15537', '0', '0', '30590', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '32238', '-114262', '-245139', '-15537', '0', '0', '14661', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '32238', '-114230', '-244076', '-15537', '0', '0', '17860', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '32238', '-114258', '-243955', '-15537', '0', '0', '11351', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '32238', '-114149', '-243960', '-15537', '0', '0', '65057', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '32238', '-112775', '-243975', '-15537', '0', '0', '62119', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '32238', '-112677', '-243987', '-15537', '0', '0', '64265', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '32238', '-112645', '-244081', '-15537', '0', '0', '52574', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '32238', '-112692', '-245307', '-15537', '0', '0', '39635', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '32238', '-112787', '-245257', '-15537', '0', '0', '27714', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '31340', '-112812', '-245125', '-15537', '0', '0', '24575', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '32238', '-112649', '-245159', '-15537', '0', '0', '43240', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '32238', '-112619', '-245251', '-15537', '0', '0', '38901', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '557', '-112723', '-244991', '-15537', '0', '0', '27478', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '31772', '-112722', '-244631', '-15537', '0', '0', '37919', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '31688', '-112723', '-244706', '-15537', '0', '0', '34826', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '558', '-112722', '-244446', '-15537', '0', '0', '32767', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '30120', '-112723', '-244300', '-15537', '0', '0', '32333', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '31341', '-112797', '-244104', '-15537', '0', '0', '41394', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '30086', '-112722', '-244193', '-15537', '0', '0', '43018', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '30771', '-112986', '-244030', '-15537', '0', '0', '49151', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '31340', '-114114', '-244075', '-15537', '0', '0', '58824', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '31341', '-114103', '-245144', '-15537', '0', '0', '6673', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '554', '-113494', '-245203', '-15537', '0', '0', '18548', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '560', '-113615', '-245202', '-15537', '0', '0', '16383', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '553', '-113708', '-245203', '-15538', '0', '0', '12859', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '559', '-113358', '-245203', '-15537', '0', '0', '13028', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '10000', '-114108', '-249873', '-2998', '0', '0', '22908', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '10000', '-114981', '-248854', '-2992', '0', '0', '54747', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '541', '-113824', '-245203', '-15537', '0', '0', '13828', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '542', '-114103', '-245023', '-15540', '0', '0', '0', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '540', '-113940', '-245203', '-15537', '0', '0', '14661', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '539', '-114103', '-244929', '-15537', '0', '0', '0', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '4306', '82885', '149385', '-3469', '0', '0', '44995', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '50007', '83296', '149269', '-3408', '0', '0', '39805', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '30080', '83395', '147898', '-3405', '0', '0', '11547', '60', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '570', '-113322', '-243833', '-15537', '0', '0', '48678', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '571', '-113441', '-243819', '-15537', '0', '0', '50053', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '572', '-113557', '-243819', '-15537', '0', '0', '40216', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '573', '-113690', '-243817', '-15537', '0', '0', '49586', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '560', '19439', '145361', '-3108', '0', '0', '33548', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '542', '18039', '145285', '-3050', '0', '0', '1066', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '576', '-12181', '122771', '-3097', '0', '0', '27931', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '22829', '92785', '111697', '-3020', '0', '0', '51061', '60', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '22829', '91641', '112499', '-3014', '0', '0', '42822', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '22829', '93640', '112833', '-3064', '0', '0', '35070', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '22829', '92875', '115210', '-3199', '0', '0', '19432', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '22829', '92049', '115524', '-3167', '0', '0', '56014', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '22829', '91543', '113336', '-3075', '0', '0', '14191', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '22829', '92528', '112404', '-3005', '0', '0', '41851', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '22829', '93844', '114875', '-3171', '0', '0', '22159', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '22829', '94722', '112245', '-2979', '0', '0', '50229', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '22829', '90184', '112980', '-3008', '0', '0', '63344', '0', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '556', '-112980', '-245203', '-15537', '0', '0', '16383', '10', '0', '0', '0');
INSERT INTO `custom_spawnlist` VALUES ('', '', '1', '555', '-112722', '-244863', '-15537', '0', '0', '32767', '10', '0', '0', '0');
