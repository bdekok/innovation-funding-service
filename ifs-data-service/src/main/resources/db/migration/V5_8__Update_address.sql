ALTER TABLE `address`
DROP COLUMN `po_box`,
DROP COLUMN `country`,
DROP COLUMN `care_of`,
CHANGE COLUMN `locality` `town` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `region` `county` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `postal_code` `postcode` VARCHAR(255) NULL DEFAULT NULL;
