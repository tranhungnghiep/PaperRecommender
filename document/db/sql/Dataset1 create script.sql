SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';


-- -----------------------------------------------------
-- Table `Dataset1`.`AuthorType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Dataset1`.`AuthorType` ;

CREATE  TABLE IF NOT EXISTS `Dataset1`.`AuthorType` (
  `idAuthorType` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `description` VARCHAR(100) NULL ,
  PRIMARY KEY (`idAuthorType`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `Dataset1`.`Author`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Dataset1`.`Author` ;

CREATE  TABLE IF NOT EXISTS `Dataset1`.`Author` (
  `idAuthor` VARCHAR(20) NOT NULL ,
  `idAuthorType` INT UNSIGNED NOT NULL ,
  PRIMARY KEY (`idAuthor`) ,
  INDEX `fk_Author_AuthorType1` (`idAuthorType` ASC) ,
  CONSTRAINT `fk_Author_AuthorType1`
    FOREIGN KEY (`idAuthorType` )
    REFERENCES `Dataset1`.`AuthorType` (`idAuthorType` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `Dataset1`.`Vocabulary`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Dataset1`.`Vocabulary` ;

CREATE  TABLE IF NOT EXISTS `Dataset1`.`Vocabulary` (
  `idKeyword` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `keyword` VARCHAR(50) NULL ,
  PRIMARY KEY (`idKeyword`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `Dataset1`.`PaperType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Dataset1`.`PaperType` ;

CREATE  TABLE IF NOT EXISTS `Dataset1`.`PaperType` (
  `idPaperType` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `description` VARCHAR(100) NULL ,
  PRIMARY KEY (`idPaperType`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `Dataset1`.`Paper`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Dataset1`.`Paper` ;

CREATE  TABLE IF NOT EXISTS `Dataset1`.`Paper` (
  `idPaper` VARCHAR(20) NOT NULL ,
  `year` INT UNSIGNED NULL ,
  `idPaperType` INT UNSIGNED NOT NULL ,
  PRIMARY KEY (`idPaper`) ,
  INDEX `fk_Paper_PaperType1` (`idPaperType` ASC) ,
  CONSTRAINT `fk_Paper_PaperType1`
    FOREIGN KEY (`idPaperType` )
    REFERENCES `Dataset1`.`PaperType` (`idPaperType` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `Dataset1`.`Paper_Keyword`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Dataset1`.`Paper_Keyword` ;

CREATE  TABLE IF NOT EXISTS `Dataset1`.`Paper_Keyword` (
  `idPaper` VARCHAR(20) NOT NULL ,
  `idKeyword` INT UNSIGNED NOT NULL ,
  `TF-IDF` DOUBLE NULL ,
  PRIMARY KEY (`idPaper`, `idKeyword`) ,
  INDEX `fk_Paper_has_Keyword_Keyword1` (`idKeyword` ASC) ,
  INDEX `fk_Paper_has_Keyword_Paper1` (`idPaper` ASC) ,
  CONSTRAINT `fk_Paper_has_Keyword_Paper1`
    FOREIGN KEY (`idPaper` )
    REFERENCES `Dataset1`.`Paper` (`idPaper` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Paper_has_Keyword_Keyword1`
    FOREIGN KEY (`idKeyword` )
    REFERENCES `Dataset1`.`Vocabulary` (`idKeyword` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `Dataset1`.`Paper_Paper`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Dataset1`.`Paper_Paper` ;

CREATE  TABLE IF NOT EXISTS `Dataset1`.`Paper_Paper` (
  `idPaper` VARCHAR(20) NOT NULL ,
  `idPaperRef` VARCHAR(20) NOT NULL ,
  PRIMARY KEY (`idPaper`, `idPaperRef`) ,
  INDEX `fk_Paper_has_Paper_Paper2` (`idPaperRef` ASC) ,
  INDEX `fk_Paper_has_Paper_Paper1` (`idPaper` ASC) ,
  CONSTRAINT `fk_Paper_has_Paper_Paper1`
    FOREIGN KEY (`idPaper` )
    REFERENCES `Dataset1`.`Paper` (`idPaper` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Paper_has_Paper_Paper2`
    FOREIGN KEY (`idPaperRef` )
    REFERENCES `Dataset1`.`Paper` (`idPaper` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `Dataset1`.`Ground_Truth_Recommendation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Dataset1`.`Ground_Truth_Recommendation` ;

CREATE  TABLE IF NOT EXISTS `Dataset1`.`Ground_Truth_Recommendation` (
  `idAuthor` VARCHAR(20) NOT NULL ,
  `idPaper` VARCHAR(20) NOT NULL ,
  `Ranking` INT NULL ,
  PRIMARY KEY (`idAuthor`, `idPaper`) ,
  INDEX `fk_Author_has_Paper_Paper1` (`idPaper` ASC) ,
  INDEX `fk_Author_has_Paper_Author1` (`idAuthor` ASC) ,
  CONSTRAINT `fk_Author_has_Paper_Author1`
    FOREIGN KEY (`idAuthor` )
    REFERENCES `Dataset1`.`Author` (`idAuthor` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Author_has_Paper_Paper1`
    FOREIGN KEY (`idPaper` )
    REFERENCES `Dataset1`.`Paper` (`idPaper` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `Dataset1`.`Author_Paper`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Dataset1`.`Author_Paper` ;

CREATE  TABLE IF NOT EXISTS `Dataset1`.`Author_Paper` (
  `idAuthor` VARCHAR(20) NOT NULL ,
  `idPaper` VARCHAR(20) NOT NULL ,
  PRIMARY KEY (`idAuthor`, `idPaper`) ,
  INDEX `fk_Author_has_Paper_Paper2` (`idPaper` ASC) ,
  INDEX `fk_Author_has_Paper_Author2` (`idAuthor` ASC) ,
  CONSTRAINT `fk_Author_has_Paper_Author2`
    FOREIGN KEY (`idAuthor` )
    REFERENCES `Dataset1`.`Author` (`idAuthor` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Author_has_Paper_Paper2`
    FOREIGN KEY (`idPaper` )
    REFERENCES `Dataset1`.`Paper` (`idPaper` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
