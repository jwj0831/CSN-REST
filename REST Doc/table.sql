CREATE TABLE IF NOT EXISTS `csn_test`.`csn_options` (
  `option_id` INT NOT NULL AUTO_INCREMENT,
  `opt_name` VARCHAR(255) NULL,
  `opt_val` VARCHAR(255) NULL,
  PRIMARY KEY (`option_id`))
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `csn_test`.`csn_snsr_meta` (
  `snsr_id` VARCHAR(45) NOT NULL,
  `snsr_name` VARCHAR(20) NOT NULL,
  `snsr_measurement` VARCHAR(45) NOT NULL,
  `snsr_creation_time` DATETIME NOT NULL,
  UNIQUE INDEX `snsr_uri_UNIQUE` (`snsr_id` ASC),
  PRIMARY KEY (`snsr_id`))
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `csn_test`.`csn_snsr_options` (
  `snsr_id` VARCHAR(45) NOT NULL,
  `opt_name` VARCHAR(255) NOT NULL,
  `opt_val` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`snsr_id`, `opt_name`),
  CONSTRAINT `snsr_id_in_opt`
    FOREIGN KEY (`snsr_id`)
    REFERENCES `csn_test`.`csn_snsr_meta` (`snsr_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `csn_test`.`csn_snsr_tags` (
  `snsr_id` VARCHAR(45) NOT NULL,
  `snsr_tag` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`snsr_id`, `snsr_tag`),
  CONSTRAINT `snsr_id_in_tag`
    FOREIGN KEY (`snsr_id`)
    REFERENCES `csn_test`.`csn_snsr_meta` (`snsr_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `csn_test`.`csn_sn_meta` (
  `sn_id` VARCHAR(45) NOT NULL,
  `sn_name` VARCHAR(45) NOT NULL,
  `sn_creation_time` DATETIME NOT NULL,
  `sn_removal_time` DATETIME NULL,
  `sn_topic_name` VARCHAR(255) NOT NULL,
  `sn_alive` INT NULL DEFAULT 1,
  PRIMARY KEY (`sn_id`),
  UNIQUE INDEX `sn_id_UNIQUE` (`sn_id` ASC))
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `csn_test`.`csn_sn_options` (
  `sn_id` VARCHAR(45) NOT NULL,
  `opt_name` VARCHAR(255) NOT NULL,
  `opt_val` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`sn_id`, `opt_name`),
  CONSTRAINT `sn_id_in_opt`
    FOREIGN KEY (`sn_id`)
    REFERENCES `csn_test`.`csn_sn_meta` (`sn_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `csn_test`.`csn_sn_tags` (
  `sn_id` VARCHAR(45) NOT NULL,
  `sn_tag` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`sn_id`, `sn_tag`),
  CONSTRAINT `sn_id_in_tag`
    FOREIGN KEY (`sn_id`)
    REFERENCES `csn_test`.`csn_sn_meta` (`sn_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `csn_test`.`csn_sn_members` (
  `sn_id` VARCHAR(45) NOT NULL,
  `snsr_member` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`sn_id`, `snsr_member`),
  INDEX `snsr_id_in_mem_idx` (`snsr_member` ASC),
  CONSTRAINT `sn_id_in_mem`
    FOREIGN KEY (`sn_id`)
    REFERENCES `csn_test`.`csn_sn_meta` (`sn_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `snsr_id_in_mem`
    FOREIGN KEY (`snsr_member`)
    REFERENCES `csn_test`.`csn_snsr_meta` (`snsr_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `csn_test`.`csn_snsr_persistence` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `snsr_id` VARCHAR(255) NOT NULL,
  `snsr_time` DATETIME NOT NULL,
  `snsr_val` VARCHAR(20) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `csn_test`.`csn_user` (
  `user_id` INT NOT NULL,
  `user_name` VARCHAR(45) NULL,
  PRIMARY KEY (`user_id`))
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `csn_test`.`csn_auth_key` (
  `auth_key` VARCHAR(45) NOT NULL,
  `attr_flag` INT NULL COMMENT '0 - sensor' /* comment truncated */ /*1 - user*/,
  `snsr_id` VARCHAR(45) NULL DEFAULT 'null',
  `user_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`auth_key`),
  INDEX `snsr_id_auth_idx` (`snsr_id` ASC),
  INDEX `user_id_auth_idx` (`user_id` ASC),
  CONSTRAINT `snsr_id_auth`
    FOREIGN KEY (`snsr_id`)
    REFERENCES `csn_test`.`csn_snsr_meta` (`snsr_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `user_id_auth`
    FOREIGN KEY (`user_id`)
    REFERENCES `csn_test`.`csn_user` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;