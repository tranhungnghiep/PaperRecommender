SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `CSPublicationCrawler` ;
CREATE SCHEMA IF NOT EXISTS `CSPublicationCrawler` DEFAULT CHARACTER SET utf8 ;
USE `CSPublicationCrawler` ;

-- -----------------------------------------------------
-- Table `CSPublicationCrawler`.`AuthorType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CSPublicationCrawler`.`AuthorType` ;

CREATE  TABLE IF NOT EXISTS `CSPublicationCrawler`.`AuthorType` (
  `idAuthorType` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `description` VARCHAR(100) NULL ,
  PRIMARY KEY (`idAuthorType`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `CSPublicationCrawler`.`Author`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CSPublicationCrawler`.`Author` ;

CREATE  TABLE IF NOT EXISTS `CSPublicationCrawler`.`Author` (
  `idAuthor` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `idAuthorType` INT(10) UNSIGNED NOT NULL ,
  PRIMARY KEY (`idAuthor`) ,
  INDEX `fk_Author_AuthorType1` (`idAuthorType` ASC) ,
  CONSTRAINT `fk_Author_AuthorType1`
    FOREIGN KEY (`idAuthorType` )
    REFERENCES `CSPublicationCrawler`.`AuthorType` (`idAuthorType` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `CSPublicationCrawler`.`Vocabulary`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CSPublicationCrawler`.`Vocabulary` ;

CREATE  TABLE IF NOT EXISTS `CSPublicationCrawler`.`Vocabulary` (
  `idKeyword` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `keyword` VARCHAR(50) NULL ,
  PRIMARY KEY (`idKeyword`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `CSPublicationCrawler`.`PaperType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CSPublicationCrawler`.`PaperType` ;

CREATE  TABLE IF NOT EXISTS `CSPublicationCrawler`.`PaperType` (
  `idPaperType` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `description` VARCHAR(100) NULL ,
  PRIMARY KEY (`idPaperType`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `CSPublicationCrawler`.`Paper`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CSPublicationCrawler`.`Paper` ;

CREATE  TABLE IF NOT EXISTS `CSPublicationCrawler`.`Paper` (
  `idPaper` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
  `year` INT(10) UNSIGNED NULL DEFAULT NULL ,
  `idPaperType` INT(10) UNSIGNED NOT NULL ,
  PRIMARY KEY (`idPaper`) ,
  INDEX `fk_Paper_PaperType1` (`idPaperType` ASC) ,
  CONSTRAINT `fk_Paper_PaperType1`
    FOREIGN KEY (`idPaperType` )
    REFERENCES `CSPublicationCrawler`.`PaperType` (`idPaperType` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `CSPublicationCrawler`.`Paper_Keyword`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CSPublicationCrawler`.`Paper_Keyword` ;

CREATE  TABLE IF NOT EXISTS `CSPublicationCrawler`.`Paper_Keyword` (
  `idPaper` INT(10) UNSIGNED NOT NULL ,
  `idKeyword` INT(10) UNSIGNED NOT NULL ,
  `TF-IDF` DOUBLE NULL ,
  PRIMARY KEY (`idPaper`, `idKeyword`) ,
  INDEX `fk_Paper_has_Keyword_Keyword1` (`idKeyword` ASC) ,
  INDEX `fk_Paper_has_Keyword_Paper1` (`idPaper` ASC) ,
  CONSTRAINT `fk_Paper_has_Keyword_Paper1`
    FOREIGN KEY (`idPaper` )
    REFERENCES `CSPublicationCrawler`.`Paper` (`idPaper` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Paper_has_Keyword_Keyword1`
    FOREIGN KEY (`idKeyword` )
    REFERENCES `CSPublicationCrawler`.`Vocabulary` (`idKeyword` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `CSPublicationCrawler`.`Paper_Paper`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CSPublicationCrawler`.`Paper_Paper` ;

CREATE  TABLE IF NOT EXISTS `CSPublicationCrawler`.`Paper_Paper` (
  `idPaper` INT(10) UNSIGNED NOT NULL ,
  `idPaperRef` INT(10) UNSIGNED NOT NULL ,
  PRIMARY KEY (`idPaper`, `idPaperRef`) ,
  INDEX `fk_Paper_has_Paper_Paper2` (`idPaperRef` ASC) ,
  INDEX `fk_Paper_has_Paper_Paper1` (`idPaper` ASC) ,
  CONSTRAINT `fk_Paper_has_Paper_Paper1`
    FOREIGN KEY (`idPaper` )
    REFERENCES `CSPublicationCrawler`.`Paper` (`idPaper` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Paper_has_Paper_Paper2`
    FOREIGN KEY (`idPaperRef` )
    REFERENCES `CSPublicationCrawler`.`Paper` (`idPaper` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `CSPublicationCrawler`.`Ground_Truth_Recommendation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CSPublicationCrawler`.`Ground_Truth_Recommendation` ;

CREATE  TABLE IF NOT EXISTS `CSPublicationCrawler`.`Ground_Truth_Recommendation` (
  `idAuthor` INT(10) UNSIGNED NOT NULL ,
  `idPaper` INT(10) UNSIGNED NOT NULL ,
  `Ranking` INT NULL ,
  PRIMARY KEY (`idAuthor`, `idPaper`) ,
  INDEX `fk_Author_has_Paper_Paper1` (`idPaper` ASC) ,
  INDEX `fk_Author_has_Paper_Author1` (`idAuthor` ASC) ,
  CONSTRAINT `fk_Author_has_Paper_Author1`
    FOREIGN KEY (`idAuthor` )
    REFERENCES `CSPublicationCrawler`.`Author` (`idAuthor` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Author_has_Paper_Paper1`
    FOREIGN KEY (`idPaper` )
    REFERENCES `CSPublicationCrawler`.`Paper` (`idPaper` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `CSPublicationCrawler`.`Author_Paper`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CSPublicationCrawler`.`Author_Paper` ;

CREATE  TABLE IF NOT EXISTS `CSPublicationCrawler`.`Author_Paper` (
  `idAuthor` INT(10) UNSIGNED NOT NULL ,
  `idPaper` INT(10) UNSIGNED NOT NULL ,
  PRIMARY KEY (`idAuthor`, `idPaper`) ,
  INDEX `fk_Author_has_Paper_Paper2` (`idPaper` ASC) ,
  INDEX `fk_Author_has_Paper_Author2` (`idAuthor` ASC) ,
  CONSTRAINT `fk_Author_has_Paper_Author2`
    FOREIGN KEY (`idAuthor` )
    REFERENCES `CSPublicationCrawler`.`Author` (`idAuthor` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Author_has_Paper_Paper2`
    FOREIGN KEY (`idPaper` )
    REFERENCES `CSPublicationCrawler`.`Paper` (`idPaper` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
