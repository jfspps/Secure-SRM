-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema SRM
-- -----------------------------------------------------
-- Student Record Management SRM
DROP SCHEMA IF EXISTS `SRM` ;

-- -----------------------------------------------------
-- Schema SRM
--
-- Student Record Management SRM
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `SRM` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ;
USE `SRM` ;

-- -----------------------------------------------------
-- Table `SRM`.`tblStudents`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblStudents` (
  `idStudents` INT NOT NULL AUTO_INCREMENT COMMENT 'Students cannot be enrolled in a school without a legal Guardian, so the relationship is identifying (idStudents embeds idGuardians in its PK)',
  `Student_reg_number` VARCHAR(10) NULL COMMENT 'Allows for an school/college-based student ID\n\nThis field can also be used to determine the order in which records are viewed (if idStudents does not meet the requirements)',
  `Student_fname` VARCHAR(45) NOT NULL,
  `Student_lname` VARCHAR(45) NOT NULL,
  `Student_mid_initial` VARCHAR(10) NULL,
  `Student_email` VARCHAR(45) NULL,
  `Student_phone` VARCHAR(20) NULL COMMENT 'ITU recommendation of 15 digits',
  `Year_group` VARCHAR(5) NULL,
  PRIMARY KEY (`idStudents`),
  UNIQUE INDEX `Student_id_UNIQUE` (`idStudents` ASC) VISIBLE,
  UNIQUE INDEX `Student_number_UNIQUE` (`Student_reg_number` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblGuardians`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblGuardians` (
  `idGuardians` INT NOT NULL AUTO_INCREMENT,
  `Students_id` INT NOT NULL,
  `Guardian_fname` VARCHAR(45) NOT NULL,
  `Guardian_lname` VARCHAR(45) NOT NULL,
  `Guardian_phone` VARCHAR(15) NOT NULL,
  `Guardian_email` VARCHAR(45) NOT NULL,
  `Guardian_2nd_email` VARCHAR(45) NULL,
  `Gaurdian_2nd_phone` VARCHAR(15) NULL,
  PRIMARY KEY (`idGuardians`, `Students_id`),
  UNIQUE INDEX `idGuardians1_UNIQUE` (`idGuardians` ASC) INVISIBLE,
  INDEX `Student_id_idx` (`Students_id` ASC) VISIBLE,
  CONSTRAINT `Student_id`
    FOREIGN KEY (`Students_id`)
    REFERENCES `SRM`.`tblStudents` (`idStudents`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblSubjects`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblSubjects` (
  `idSubjects` INT NOT NULL AUTO_INCREMENT,
  `Subject_title` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idSubjects`),
  UNIQUE INDEX `idSubjects_UNIQUE` (`idSubjects` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblTeachers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblTeachers` (
  `idTeachers` INT NOT NULL AUTO_INCREMENT COMMENT 'Re. assignments\nAt the beginning of the year, there may be zero assignments set. However, the assignment, once submitted, can be accessed (shared e.g. year-wide exams) by at least one teacher and possible many more.\nEach assignment set requires only one teacher to set it. It is assumed that multiple teachers do not submit the same assignment.\n\nChecks on the identity of teacher by reading multiple fields are conducted before transactions are processed, ensuring teachers only update their own records',
  `Teacher_fname` VARCHAR(45) NOT NULL,
  `Teacher_lname` VARCHAR(45) NOT NULL,
  `Form_group_name` VARCHAR(45) NULL COMMENT 'Not all teachers are pastoral tutors; when they are this field names the group e.g. 10SB\nPlacing it in Teachers table instead of Students_JUNC_teachers table minimises repeated entries',
  `Teacher_work_email` VARCHAR(45) NOT NULL,
  `Teacher_phone` VARCHAR(45) NOT NULL,
  `Management_role` VARCHAR(25) NULL COMMENT 'This could include Head of Year or Head of Department',
  `Management_role2` VARCHAR(25) NULL COMMENT 'This could include Head of Year or Head of Department',
  `LINQ_messages` VARCHAR(100) NULL COMMENT 'Messages or notifications, and responses to/from teachers',
  PRIMARY KEY (`idTeachers`),
  UNIQUE INDEX `idTeachers_UNIQUE` (`idTeachers` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblSubjects_Teachers_groups`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblSubjects_Teachers_groups` (
  `idSubjects_Teachers_group` INT NOT NULL AUTO_INCREMENT,
  `Subject_class_name` VARCHAR(45) NULL COMMENT 'Labels the subjects taught by a given teacher with a name for that class e.g. Chem8B etc...',
  `Subjects_id` INT NOT NULL,
  `Teachers_id` INT NOT NULL,
  PRIMARY KEY (`idSubjects_Teachers_group`, `Subjects_id`, `Teachers_id`),
  UNIQUE INDEX `idSubjects_Teachers_group_UNIQUE` (`idSubjects_Teachers_group` ASC) VISIBLE,
  INDEX `Subject_id_idx` (`Subjects_id` ASC) VISIBLE,
  INDEX `Teacher_id_idx` (`Teachers_id` ASC) VISIBLE,
  CONSTRAINT `Subject_id`
    FOREIGN KEY (`Subjects_id`)
    REFERENCES `SRM`.`tblSubjects` (`idSubjects`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Teacher_id`
    FOREIGN KEY (`Teachers_id`)
    REFERENCES `SRM`.`tblTeachers` (`idTeachers`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblAcademic_classes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblAcademic_classes` (
  `idAcademic_classes` INT NOT NULL AUTO_INCREMENT,
  `Students_id` INT NOT NULL,
  `Subjects_Teachers_groups_id` INT NOT NULL,
  INDEX `fk_Subjects_Teachers_group_has_Students_Students1_idx` (`Students_id` ASC) VISIBLE,
  INDEX `fk_Subjects_Teachers_group_has_Students_Subjects_Teachers_g_idx` (`Subjects_Teachers_groups_id` ASC) VISIBLE,
  PRIMARY KEY (`idAcademic_classes`),
  UNIQUE INDEX `idAcademic_class_UNIQUE` (`idAcademic_classes` ASC) VISIBLE,
  CONSTRAINT `Subjects_Teachers_groups_id`
    FOREIGN KEY (`Subjects_Teachers_groups_id`)
    REFERENCES `SRM`.`tblSubjects_Teachers_groups` (`idSubjects_Teachers_group`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `AC_Students_id`
    FOREIGN KEY (`Students_id`)
    REFERENCES `SRM`.`tblStudents` (`idStudents`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblForm_groups`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblForm_groups` (
  `idForm_groups` INT NOT NULL AUTO_INCREMENT,
  `Students_id` INT NOT NULL,
  `Teachers_id` INT NOT NULL,
  INDEX `fk_Teachers_has_Students_Students1_idx` (`Students_id` ASC) VISIBLE,
  INDEX `fk_Teachers_has_Students_Teachers1_idx` (`Teachers_id` ASC) VISIBLE,
  PRIMARY KEY (`idForm_groups`),
  UNIQUE INDEX `idForm_group_UNIQUE` (`idForm_groups` ASC) VISIBLE,
  CONSTRAINT `Teacher_id_form_group`
    FOREIGN KEY (`Teachers_id`)
    REFERENCES `SRM`.`tblTeachers` (`idTeachers`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Student_id_form_group`
    FOREIGN KEY (`Students_id`)
    REFERENCES `SRM`.`tblStudents` (`idStudents`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblAssignments_info`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblAssignments_info` (
  `idAssignments_info` INT NOT NULL AUTO_INCREMENT COMMENT 'An assignment need not have a threshold set (Grading_group) and would normally only have one threshold (here, different thresholds are permitted). Conversely, for a threshold to exist, there must be at least one assignment ready, the same threshold could apply to different assignments. Hence Assignments_info is one-to-many with the child table Grading group, and identifying.\n\nIn relation to Student_Assignments (students\' scores), an assignment can exist but there need not be any student scores available. There are mutiple student scores for a given assignment. Conversely, for a student record to exist, there must be at least one assignment with details prepared.  Hence the relationship is identifying. It is assumed that the assignment score and instructions are unique to the assignment taken.',
  `Assignment_title` VARCHAR(45) NOT NULL COMMENT 'This field can be populated such that it controls the order in which records are viewed (if PKs do not serve adequate purpose)',
  `Assignment_detail` VARCHAR(100) NULL,
  `Max_raw_score` INT NOT NULL DEFAULT 100,
  `Type_of_assessment` CHAR NOT NULL,
  `Teachers_instruction` VARCHAR(200) NULL,
  PRIMARY KEY (`idAssignments_info`),
  UNIQUE INDEX `idAssignments_UNIQUE` (`idAssignments_info` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblAssignments_teacher_info`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblAssignments_teacher_info` (
  `idAssignments_teacher_info` INT NOT NULL AUTO_INCREMENT,
  `Assignment_entry_date` DATE NOT NULL,
  `Add_to_average` TINYINT NOT NULL DEFAULT 1 COMMENT 'Set to zero if this assignment should not contribute to the cumulative average but remain on the DB. All non-zero values are considered TRUE',
  `Assignments_info_id` INT NOT NULL,
  `Teachers_id` INT NULL COMMENT 'This FK can be NULL so that when a teacher is removed from the database, the assignment scores and other details are retained for review/evaluation purposes',
  PRIMARY KEY (`idAssignments_teacher_info`),
  UNIQUE INDEX `idTeacher_assignments_UNIQUE` (`idAssignments_teacher_info` ASC) VISIBLE,
  INDEX `Assignment_id_sub_idx` (`Assignments_info_id` ASC) VISIBLE,
  INDEX `Teacher_id_sub_idx` (`Teachers_id` ASC) VISIBLE,
  CONSTRAINT `Assignment_id_sub`
    FOREIGN KEY (`Assignments_info_id`)
    REFERENCES `SRM`.`tblAssignments_info` (`idAssignments_info`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Teacher_id_sub`
    FOREIGN KEY (`Teachers_id`)
    REFERENCES `SRM`.`tblTeachers` (`idTeachers`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblStudent_assignments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblStudent_assignments` (
  `idStudent_assignments` INT NOT NULL AUTO_INCREMENT,
  `Assignments_info_id` INT NOT NULL,
  `Comments_for_guardian` VARCHAR(300) NULL,
  `Comments_for_staff` VARCHAR(300) NULL,
  `Raw_score` INT NULL COMMENT 'Allow NULL for students who were absent (ignored when average is tallied)',
  `Students_id` INT NULL COMMENT 'This FK can be NULL so that when a student is removed from the database, the assignment scores and other details are retained for review/evaluation purposes',
  `Assignment_date` DATE NULL,
  PRIMARY KEY (`idStudent_assignments`, `Assignments_info_id`),
  UNIQUE INDEX `idStudent_assignments_UNIQUE` (`idStudent_assignments` ASC) VISIBLE,
  INDEX `Assignment_id_idx` (`Assignments_info_id` ASC) VISIBLE,
  INDEX `Student_id_idx` (`Students_id` ASC) VISIBLE,
  CONSTRAINT `Students_Assignment_id`
    FOREIGN KEY (`Assignments_info_id`)
    REFERENCES `SRM`.`tblAssignments_info` (`idAssignments_info`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Student_id_assignments`
    FOREIGN KEY (`Students_id`)
    REFERENCES `SRM`.`tblStudents` (`idStudents`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblGrade_thresholds`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblGrade_thresholds` (
  `idGrade_thresholds` INT NOT NULL AUTO_INCREMENT,
  `Threshold_note` VARCHAR(100) NULL,
  `Highest_raw` INT NOT NULL COMMENT 'This represents the boundary of the highest grade possible. Scores at or above this value are assigned the Highest_char (see Letter_grade_chars)',
  `High1_raw` INT NULL,
  `High2_raw` INT NULL,
  `High3_raw` INT NULL,
  `High4_raw` INT NULL,
  `High5_raw` INT NULL,
  `High6_raw` INT NULL,
  `High7_raw` INT NULL,
  `High8_raw` INT NULL,
  `High9_raw` INT NULL,
  `High10_raw` INT NULL,
  `High11_raw` INT NULL,
  `High12_raw` INT NULL,
  `High13_raw` INT NULL,
  `High14_raw` INT NULL,
  `High15_raw` INT NULL,
  `High16_raw` INT NULL,
  `High17_raw` INT NULL,
  `High18_raw` INT NULL,
  `Lowest_raw` INT NOT NULL DEFAULT 0 COMMENT 'This represents the boundary of the lowest grade possible. Scores at or above this value are assigned the Lowest_char (see Letter_grade_chars)\n\nAs per school policies, this can represent zero marks, thus, allowing all scores to be assigned a letter grade (U or ungraded for example)\n\nIf this field is set to the lowest non-zero boundary, then all letter grades for scores lower than this boundary are set to NULL',
  PRIMARY KEY (`idGrade_thresholds`),
  UNIQUE INDEX `idLetter_grade_thresholds_UNIQUE` (`idGrade_thresholds` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblLetter_grade_chars`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblLetter_grade_chars` (
  `idLetter_grade_chars` INT NOT NULL AUTO_INCREMENT COMMENT 'Letter grades (A, B+, D--, A*, MERIT, DISTINCTION, PASS etc.)\n\nIf grading chars is implemented then there must be at least one grading group present. The sequence of cahrs used might apply to multiple gradings with different numerical thresholds. Grading_chars need not exist for grading groups to exist, hence non-identifying.',
  `Letter_grade_note` VARCHAR(100) NULL,
  `Highest_char` VARCHAR(11) NOT NULL,
  `High1_char` VARCHAR(11) NULL,
  `High2_char` VARCHAR(11) NULL,
  `High3_char` VARCHAR(11) NULL,
  `High4_char` VARCHAR(11) NULL,
  `High5_char` VARCHAR(11) NULL,
  `High6_char` VARCHAR(11) NULL,
  `High7_char` VARCHAR(11) NULL,
  `High8_char` VARCHAR(11) NULL,
  `High9_char` VARCHAR(11) NULL,
  `High10_char` VARCHAR(11) NULL,
  `High11_char` VARCHAR(11) NULL,
  `High12_char` VARCHAR(11) NULL,
  `High13_char` VARCHAR(11) NULL,
  `High14_char` VARCHAR(11) NULL,
  `High15_char` VARCHAR(11) NULL,
  `High16_char` VARCHAR(11) NULL,
  `High17_char` VARCHAR(11) NULL,
  `High18_char` VARCHAR(11) NULL,
  `Lowest_char` VARCHAR(11) NOT NULL,
  PRIMARY KEY (`idLetter_grade_chars`),
  UNIQUE INDEX `idLetter_grade_chars_UNIQUE` (`idLetter_grade_chars` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblGrading_groups`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblGrading_groups` (
  `idGrading_groups` INT NOT NULL AUTO_INCREMENT,
  `Assignments_info_id` INT NOT NULL,
  `Grade_thresholds_id` INT NULL COMMENT 'Grade thresholds are numerical boundaries. These can be paired with different letter grade symbols (Letter_grade_chars)',
  `Letter_grade_chars_id` INT NULL COMMENT 'A school may use the same letter grades (A*, A, B, C,...) for different numerical thresholds, hence staff need only insert new thresholds and apply the same letter grade system',
  PRIMARY KEY (`idGrading_groups`, `Assignments_info_id`),
  UNIQUE INDEX `idLetter_grade_groups_UNIQUE` (`idGrading_groups` ASC) VISIBLE,
  INDEX `Assignment_id_threshold_idx` (`Assignments_info_id` ASC) VISIBLE,
  INDEX `Grade_thresholds_id_idx` (`Grade_thresholds_id` ASC) VISIBLE,
  INDEX `Letter_grade_chars_id_idx` (`Letter_grade_chars_id` ASC) VISIBLE,
  CONSTRAINT `Assignment_id_threshold`
    FOREIGN KEY (`Assignments_info_id`)
    REFERENCES `SRM`.`tblAssignments_info` (`idAssignments_info`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Grade_thresholds_id`
    FOREIGN KEY (`Grade_thresholds_id`)
    REFERENCES `SRM`.`tblGrade_thresholds` (`idGrade_thresholds`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Letter_grade_chars_id`
    FOREIGN KEY (`Letter_grade_chars_id`)
    REFERENCES `SRM`.`tblLetter_grade_chars` (`idLetter_grade_chars`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblStudents_Subjects`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblStudents_Subjects` (
  `idStudent_Subjects` INT NOT NULL AUTO_INCREMENT,
  `Students_id` INT NOT NULL,
  `Subjects_id` INT NOT NULL COMMENT 'Part of a composite PK',
  INDEX `fk_Students_has_Subjects_Subjects1_idx` (`Subjects_id` ASC) VISIBLE,
  INDEX `fk_Students_has_Subjects_Students1_idx` (`Students_id` ASC) VISIBLE,
  PRIMARY KEY (`idStudent_Subjects`),
  UNIQUE INDEX `idStudent_JUNC_Subjects_UNIQUE` (`idStudent_Subjects` ASC) VISIBLE,
  CONSTRAINT `SS_Students_id`
    FOREIGN KEY (`Students_id`)
    REFERENCES `SRM`.`tblStudents` (`idStudents`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `Subjects_id`
    FOREIGN KEY (`Subjects_id`)
    REFERENCES `SRM`.`tblSubjects` (`idSubjects`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblGuardians_addresses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblGuardians_addresses` (
  `idGuardians_addresses` INT NOT NULL AUTO_INCREMENT,
  `First_line` VARCHAR(45) NOT NULL,
  `Second_line` VARCHAR(45) NULL,
  `County_State` VARCHAR(45) NULL,
  `Postcode_ZIPcode` VARCHAR(10) NOT NULL,
  `Country` VARCHAR(45) NULL,
  `Guardians_id` INT NOT NULL,
  UNIQUE INDEX `idAddresses_UNIQUE` (`idGuardians_addresses` ASC) VISIBLE,
  PRIMARY KEY (`idGuardians_addresses`, `Guardians_id`),
  INDEX `Guardian1_address_idx` (`Guardians_id` ASC) VISIBLE,
  CONSTRAINT `Guardian1_address`
    FOREIGN KEY (`Guardians_id`)
    REFERENCES `SRM`.`tblGuardians` (`idGuardians`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblStudent_reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblStudent_reports` (
  `idStudent_reports` INT NOT NULL AUTO_INCREMENT,
  `Students_id` INT NOT NULL,
  `Teachers_id` INT NOT NULL,
  `Report_date` DATE NOT NULL,
  `Academic_comments` VARCHAR(1000) NULL,
  `Pastoral_comments` VARCHAR(1000) NULL,
  PRIMARY KEY (`idStudent_reports`, `Students_id`, `Teachers_id`),
  UNIQUE INDEX `idStudent_report_UNIQUE` (`idStudent_reports` ASC) VISIBLE,
  INDEX `Student_id_report_idx` (`Students_id` ASC) VISIBLE,
  INDEX `Teacher_id_report_idx` (`Teachers_id` ASC) VISIBLE,
  CONSTRAINT `SR_Student_id_report`
    FOREIGN KEY (`Students_id`)
    REFERENCES `SRM`.`tblStudents` (`idStudents`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `Teacher_id_report`
    FOREIGN KEY (`Teachers_id`)
    REFERENCES `SRM`.`tblTeachers` (`idTeachers`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SRM`.`tblLINQ_users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`tblLINQ_users` (
  `idLINQ_users` INT NOT NULL AUTO_INCREMENT,
  `user_fname` VARCHAR(45) NULL,
  `user_lname` VARCHAR(45) NULL,
  `LINQ_username` VARCHAR(45) NOT NULL,
  `LINQ_pw` VARCHAR(45) NOT NULL,
  `LINQ_useremail` VARCHAR(45) NOT NULL,
  `LINQ_lastlogin` DATETIME NULL,
  PRIMARY KEY (`idLINQ_users`),
  UNIQUE INDEX `idLINQ_users_UNIQUE` (`idLINQ_users` ASC) VISIBLE,
  UNIQUE INDEX `LINQ_username_UNIQUE` (`LINQ_username` ASC) VISIBLE,
  UNIQUE INDEX `LINQ_useremail_UNIQUE` (`LINQ_useremail` ASC) VISIBLE)
ENGINE = InnoDB;

USE `SRM` ;

-- -----------------------------------------------------
-- Placeholder table for view `SRM`.`vw_Form_group_list`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`vw_Form_group_list` (`idStudents` INT, `Student_reg_number` INT, `Student_fname` INT, `Student_lname` INT, `Student_mid_initial` INT, `Student_email` INT, `Student_phone` INT, `Year_group` INT, `idForm_groups` INT, `Students_id` INT, `Teachers_id` INT, `idTeachers` INT, `Teacher_fname` INT, `Teacher_lname` INT, `Form_group_name` INT, `Teacher_work_email` INT, `Teacher_phone` INT, `Management_role` INT, `Management_role2` INT, `LINQ_messages` INT);

-- -----------------------------------------------------
-- Placeholder table for view `SRM`.`vw_Academic_classes_with_students`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`vw_Academic_classes_with_students` (`idStudents` INT, `Student_reg_number` INT, `Student_fname` INT, `Student_lname` INT, `Student_mid_initial` INT, `Student_email` INT, `Student_phone` INT, `Year_group` INT, `idSubjects_Teachers_group` INT, `Subject_class_name` INT, `Subjects_id` INT, `Teachers_id` INT, `idTeachers` INT, `Teacher_fname` INT, `Teacher_lname` INT, `Form_group_name` INT, `Teacher_work_email` INT, `Teacher_phone` INT, `Management_role` INT, `Management_role2` INT, `LINQ_messages` INT, `idSubjects` INT, `Subject_title` INT);

-- -----------------------------------------------------
-- Placeholder table for view `SRM`.`vw_Subjects_available`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`vw_Subjects_available` (`idSubjects` INT, `Subject_title` INT);

-- -----------------------------------------------------
-- Placeholder table for view `SRM`.`vw_Subjects_available_with_students`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`vw_Subjects_available_with_students` (`idSubjects` INT, `Subject_title` INT, `idStudents` INT, `Student_reg_number` INT, `Student_fname` INT, `Student_lname` INT, `Student_mid_initial` INT, `Student_email` INT, `Student_phone` INT, `Year_group` INT);

-- -----------------------------------------------------
-- Placeholder table for view `SRM`.`vw_Academic_classes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`vw_Academic_classes` (`idSubjects_Teachers_group` INT, `Subject_class_name` INT, `Subjects_id` INT, `Teachers_id` INT, `idTeachers` INT, `Teacher_fname` INT, `Teacher_lname` INT, `Form_group_name` INT, `Teacher_work_email` INT, `Teacher_phone` INT, `Management_role` INT, `Management_role2` INT, `LINQ_messages` INT, `idSubjects` INT, `Subject_title` INT);

-- -----------------------------------------------------
-- Placeholder table for view `SRM`.`vw_Teachers_list`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`vw_Teachers_list` (`idTeachers` INT, `Teacher_fname` INT, `Teacher_lname` INT, `Form_group_name` INT, `Teacher_work_email` INT, `Teacher_phone` INT, `Management_role` INT, `Management_role2` INT, `LINQ_messages` INT);

-- -----------------------------------------------------
-- Placeholder table for view `SRM`.`vw_Students_personal_data`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`vw_Students_personal_data` (`idStudents` INT, `Student_reg_number` INT, `Student_fname` INT, `Student_lname` INT, `Student_mid_initial` INT, `Student_email` INT, `Student_phone` INT, `Year_group` INT, `idGuardians` INT, `Students_id` INT, `Guardian_fname` INT, `Guardian_lname` INT, `Guardian_phone` INT, `Guardian_email` INT, `Guardian_2nd_email` INT, `Gaurdian_2nd_phone` INT, `idGuardians_addresses` INT, `First_line` INT, `Second_line` INT, `County_State` INT, `Postcode_ZIPcode` INT, `Country` INT, `Guardians_id` INT);

-- -----------------------------------------------------
-- Placeholder table for view `SRM`.`vw_Assignments_on_record`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`vw_Assignments_on_record` (`idAssignments_info` INT, `assignment_title` INT, `assignment_detail` INT, `max_raw_score` INT, `type_of_assessment` INT, `teachers_instruction` INT, `idAssignments_teacher_info` INT, `assignment_entry_date` INT, `add_to_average` INT, `teachers_id` INT);

-- -----------------------------------------------------
-- Placeholder table for view `SRM`.`vw_Assignments_with_thresholds`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`vw_Assignments_with_thresholds` (`idAssignments_info` INT, `Assignment_title` INT, `assignment_detail` INT, `max_raw_score` INT, `type_of_assessment` INT, `teachers_instruction` INT, `assignment_entry_date` INT, `add_to_average` INT, `teachers_id` INT, `threshold_note` INT, `letter_grade_note` INT, `Highest_raw` INT, `highest_char` INT, `high1_raw` INT, `high1_char` INT, `high2_raw` INT, `high2_char` INT, `high3_raw` INT, `high3_char` INT, `high4_raw` INT, `high4_char` INT, `high5_raw` INT, `high5_char` INT, `high6_raw` INT, `high6_char` INT, `high7_raw` INT, `high7_char` INT, `high8_raw` INT, `high8_char` INT, `high9_raw` INT, `high9_char` INT, `high10_raw` INT, `high10_char` INT, `high11_raw` INT, `high11_char` INT, `high12_raw` INT, `high12_char` INT, `high13_raw` INT, `high13_char` INT, `high14_raw` INT, `high14_char` INT, `high15_raw` INT, `high15_char` INT, `high16_raw` INT, `high16_char` INT, `high17_raw` INT, `high17_char` INT, `lowest_raw` INT, `lowest_char` INT);

-- -----------------------------------------------------
-- Placeholder table for view `SRM`.`vw_Assignments_with_scores`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`vw_Assignments_with_scores` (`idStudents` INT, `Student_reg_number` INT, `student_fname` INT, `student_mid_initial` INT, `student_lname` INT, `student_email` INT, `student_phone` INT, `year_group` INT, `comments_for_guardian` INT, `comments_for_staff` INT, `raw_score` INT, `idAssignments_info` INT, `assignment_title` INT, `assignment_detail` INT, `max_raw_score` INT, `type_of_assessment` INT, `teachers_instruction` INT, `assignment_entry_date` INT, `add_to_average` INT, `idTeachers` INT, `teacher_fname` INT, `teacher_lname` INT, `form_group_name` INT, `teacher_work_email` INT, `teacher_phone` INT);

-- -----------------------------------------------------
-- Placeholder table for view `SRM`.`vw_Assignments_with_scores_and_grades`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`vw_Assignments_with_scores_and_grades` (`idStudents` INT, `Student_reg_number` INT, `student_fname` INT, `student_mid_initial` INT, `student_lname` INT, `student_email` INT, `student_phone` INT, `year_group` INT, `comments_for_guardian` INT, `comments_for_staff` INT, `raw_score` INT, `idAssignments_info` INT, `assignment_title` INT, `assignment_detail` INT, `max_raw_score` INT, `type_of_assessment` INT, `teachers_instruction` INT, `assignment_entry_date` INT, `add_to_average` INT, `idTeachers` INT, `teacher_fname` INT, `teacher_lname` INT, `form_group_name` INT, `teacher_work_email` INT, `teacher_phone` INT, `'Grade'` INT);

-- -----------------------------------------------------
-- Placeholder table for view `SRM`.`vw_Students_assignments_grades`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`vw_Students_assignments_grades` (`idStudents` INT, `Student_reg_number` INT, `student_fname` INT, `student_mid_initial` INT, `student_lname` INT, `student_email` INT, `student_phone` INT, `year_group` INT, `comments_for_guardian` INT, `comments_for_staff` INT, `raw_score` INT, `idAssignments_info` INT, `assignment_title` INT, `assignment_detail` INT, `max_raw_score` INT, `type_of_assessment` INT, `teachers_instruction` INT, `'Grade'` INT);

-- -----------------------------------------------------
-- Placeholder table for view `SRM`.`vw_Students_assignments_grades_min`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `SRM`.`vw_Students_assignments_grades_min` (`idStudents` INT, `Student_reg_number` INT, `student_fname` INT, `student_mid_initial` INT, `student_lname` INT, `student_email` INT, `assignment_title` INT, `max_raw_score` INT, `raw_score` INT, `'Grade'` INT, `type_of_assessment` INT, `comments_for_guardian` INT, `comments_for_staff` INT);

-- -----------------------------------------------------
-- View `SRM`.`vw_Form_group_list`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SRM`.`vw_Form_group_list`;
USE `SRM`;
CREATE  OR REPLACE VIEW `vw_Form_group_list` AS
    SELECT 
        *
    FROM
        tblStudents
            JOIN
        tblForm_groups ON tblForm_groups.students_id = idStudents
            JOIN
        tblTeachers ON tblForm_groups.teachers_id = idTeachers
    WHERE
        tblForm_groups.students_id = idStudents;

-- -----------------------------------------------------
-- View `SRM`.`vw_Academic_classes_with_students`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SRM`.`vw_Academic_classes_with_students`;
USE `SRM`;
CREATE  OR REPLACE VIEW `vw_Academic_classes_with_students` AS
    SELECT 
        *
    FROM
        tblStudents
            JOIN
        tblacademic_classes ON idStudents = tblacademic_classes.students_id
            JOIN
        tblSubjects_Teachers_groups ON subjects_teachers_groups_id = idSubjects_teachers_group
            JOIN
        tblTeachers ON tblSubjects_teachers_groups.teachers_id = idTeachers
            JOIN
        tblSubjects ON tblSubjects_teachers_groups.subjects_id = idSubjects;

-- -----------------------------------------------------
-- View `SRM`.`vw_Subjects_available`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SRM`.`vw_Subjects_available`;
USE `SRM`;
CREATE  OR REPLACE VIEW `vw_Subjects_available` AS
select * from tblSubjects left join tblstudents_subjects on idSubjects = tblStudents_Subjects.subjects_id;

-- -----------------------------------------------------
-- View `SRM`.`vw_Subjects_available_with_students`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SRM`.`vw_Subjects_available_with_students`;
USE `SRM`;
CREATE  OR REPLACE VIEW `vw_Subjects_available_with_students` AS
    SELECT 
        *
    FROM
        tblSubjects
            LEFT JOIN
        tblstudents_subjects ON idSubjects = tblStudents_Subjects.subjects_id
            LEFT JOIN
        tblStudents ON tblStudents_Subjects.students_id = idStudents;

-- -----------------------------------------------------
-- View `SRM`.`vw_Academic_classes`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SRM`.`vw_Academic_classes`;
USE `SRM`;
CREATE  OR REPLACE VIEW `vw_Academic_classes` AS
    SELECT 
        *
    FROM
        tblSubjects_Teachers_groups
            JOIN
        tblTeachers ON tblSubjects_teachers_groups.teachers_id = idTeachers
            RIGHT JOIN
        tblSubjects ON tblSubjects_teachers_groups.subjects_id = idSubjects;

-- -----------------------------------------------------
-- View `SRM`.`vw_Teachers_list`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SRM`.`vw_Teachers_list`;
USE `SRM`;
CREATE  OR REPLACE VIEW `vw_Teachers_list` AS
    SELECT 
        *
    FROM
        tblTeachers;

-- -----------------------------------------------------
-- View `SRM`.`vw_Students_personal_data`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SRM`.`vw_Students_personal_data`;
USE `SRM`;
CREATE  OR REPLACE VIEW `vw_Students_personal_data` AS
    SELECT 
        *
    FROM
        tblStudents
            JOIN
        tblGuardians ON idStudents = tblGuardians.students_id
            JOIN
        tblGuardians_addresses ON Guardians_id = idGuardians;

-- -----------------------------------------------------
-- View `SRM`.`vw_Assignments_on_record`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SRM`.`vw_Assignments_on_record`;
USE `SRM`;
CREATE  OR REPLACE VIEW `vw_Assignments_on_record` AS
    SELECT 
        idAssignments_info,
        assignment_title,
        assignment_detail,
        max_raw_score,
        type_of_assessment,
        teachers_instruction,
        idAssignments_teacher_info,
        assignment_entry_date,
        add_to_average,
        teachers_id
    FROM
        tblAssignments_info
            LEFT JOIN
        tblAssignments_teacher_info ON tblAssignments_teacher_info.assignments_info_id = idAssignments_info;

-- -----------------------------------------------------
-- View `SRM`.`vw_Assignments_with_thresholds`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SRM`.`vw_Assignments_with_thresholds`;
USE `SRM`;
CREATE  OR REPLACE VIEW `vw_Assignments_with_thresholds` AS
    SELECT 
        idAssignments_info,
        Assignment_title,
        assignment_detail,
        max_raw_score,
        type_of_assessment,
        teachers_instruction,
        assignment_entry_date,
        add_to_average,
        teachers_id,
        threshold_note,
        letter_grade_note,
        Highest_raw,
        highest_char,
        high1_raw,
        high1_char,
        high2_raw,
        high2_char,
        high3_raw,
        high3_char,
        high4_raw,
        high4_char,
        high5_raw,
        high5_char,
        high6_raw,
        high6_char,
        high7_raw,
        high7_char,
        high8_raw,
        high8_char,
        high9_raw,
        high9_char,
        high10_raw,
        high10_char,
        high11_raw,
        high11_char,
        high12_raw,
        high12_char,
        high13_raw,
        high13_char,
        high14_raw,
        high14_char,
        high15_raw,
        high15_char,
        high16_raw,
        high16_char,
        high17_raw,
        high17_char,
        lowest_raw,
        lowest_char
    FROM
        tblAssignments_info
            LEFT JOIN
        tblAssignments_teacher_info ON tblAssignments_teacher_info.assignments_info_id = idAssignments_info
            JOIN
        tblGrading_groups ON tblGrading_groups.assignments_info_id = idAssignments_info
            JOIN
        tblGrade_thresholds ON Grade_thresholds_id = idGrade_thresholds
            JOIN
        tblLetter_grade_chars ON Letter_grade_chars_id = idLetter_grade_chars;

-- -----------------------------------------------------
-- View `SRM`.`vw_Assignments_with_scores`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SRM`.`vw_Assignments_with_scores`;
USE `SRM`;
CREATE  OR REPLACE VIEW `vw_Assignments_with_scores` AS
SELECT 
    idStudents,
    Student_reg_number,
    student_fname,
    student_mid_initial,
    student_lname,
    student_email,
    student_phone,
    year_group,
    comments_for_guardian,
    comments_for_staff,
    raw_score,
    idAssignments_info,
    assignment_title,
    assignment_detail,
    max_raw_score,
    type_of_assessment,
    teachers_instruction,
    assignment_entry_date,
    add_to_average,
    idTeachers,
    teacher_fname,
    teacher_lname,
    form_group_name,
    teacher_work_email,
    teacher_phone
FROM
    tblStudents
        JOIN
    tblStudent_assignments ON idStudents = tblStudent_assignments.students_id
        JOIN
    tblAssignments_info ON idAssignments_info = tblStudent_assignments.assignments_info_id
        JOIN
    tblAssignments_teacher_info ON tblAssignments_teacher_info.assignments_info_id = idAssignments_info
        JOIN
    tblTeachers ON idTeachers = tblAssignments_teacher_info.teachers_id;

-- -----------------------------------------------------
-- View `SRM`.`vw_Assignments_with_scores_and_grades`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SRM`.`vw_Assignments_with_scores_and_grades`;
USE `SRM`;
CREATE  OR REPLACE VIEW `vw_Assignments_with_scores_and_grades` AS
    SELECT 
        idStudents,
        Student_reg_number,
        student_fname,
        student_mid_initial,
        student_lname,
        student_email,
        student_phone,
        year_group,
        comments_for_guardian,
        comments_for_staff,
        raw_score,
        idAssignments_info,
        assignment_title,
        assignment_detail,
        max_raw_score,
        type_of_assessment,
        teachers_instruction,
        assignment_entry_date,
        add_to_average,
        idTeachers,
        teacher_fname,
        teacher_lname,
        form_group_name,
        teacher_work_email,
        teacher_phone,
        CASE
            WHEN raw_score >= highest_raw THEN highest_char
            WHEN raw_score >= high1_raw THEN high1_char
            WHEN raw_score >= high2_raw THEN high2_char
            WHEN raw_score >= high3_raw THEN high3_char
            WHEN raw_score >= high4_raw THEN high4_char
            WHEN raw_score >= high5_raw THEN high5_char
            WHEN raw_score >= high6_raw THEN high6_char
            WHEN raw_score >= high7_raw THEN high7_char
            WHEN raw_score >= high8_raw THEN high8_char
            WHEN raw_score >= high9_raw THEN high9_char
            WHEN raw_score >= high10_raw THEN high10_char
            WHEN raw_score >= high11_raw THEN high11_char
            WHEN raw_score >= high12_raw THEN high12_char
            WHEN raw_score >= high13_raw THEN high13_char
            WHEN raw_score >= high14_raw THEN high14_char
            WHEN raw_score >= high15_raw THEN high15_char
            WHEN raw_score >= high16_raw THEN high16_char
            WHEN raw_score >= high17_raw THEN high17_char
            WHEN raw_score >= high18_raw THEN high18_char
            WHEN raw_score >= lowest_raw THEN lowest_char
            ELSE NULL
        END AS 'Grade'
    FROM
        tblStudents
            JOIN
        tblStudent_assignments ON idStudents = tblStudent_assignments.students_id
            JOIN
        tblAssignments_info ON idAssignments_info = tblStudent_assignments.assignments_info_id
            JOIN
        tblAssignments_teacher_info ON tblAssignments_teacher_info.assignments_info_id = idAssignments_info
            JOIN
        tblTeachers ON idTeachers = tblAssignments_teacher_info.teachers_id
            JOIN
        tblGrading_groups ON tblGrading_groups.assignments_info_id = idAssignments_info
            JOIN
        tblGrade_thresholds ON grade_thresholds_id = idGrade_thresholds
            JOIN
        tblLetter_grade_chars ON letter_grade_chars_id = idLetter_grade_chars;

-- -----------------------------------------------------
-- View `SRM`.`vw_Students_assignments_grades`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SRM`.`vw_Students_assignments_grades`;
USE `SRM`;
CREATE  OR REPLACE VIEW `vw_Students_assignments_grades` AS
SELECT 
        idStudents,
        Student_reg_number,
        student_fname,
        student_mid_initial,
        student_lname,
        student_email,
        student_phone,
        year_group,
        comments_for_guardian,
        comments_for_staff,
        raw_score,
        idAssignments_info,
        assignment_title,
        assignment_detail,
        max_raw_score,
        type_of_assessment,
        teachers_instruction,
        CASE
            WHEN raw_score >= highest_raw THEN highest_char
            WHEN raw_score >= high1_raw THEN high1_char
            WHEN raw_score >= high2_raw THEN high2_char
            WHEN raw_score >= high3_raw THEN high3_char
            WHEN raw_score >= high4_raw THEN high4_char
            WHEN raw_score >= high5_raw THEN high5_char
            WHEN raw_score >= high6_raw THEN high6_char
            WHEN raw_score >= high7_raw THEN high7_char
            WHEN raw_score >= high8_raw THEN high8_char
            WHEN raw_score >= high9_raw THEN high9_char
            WHEN raw_score >= high10_raw THEN high10_char
            WHEN raw_score >= high11_raw THEN high11_char
            WHEN raw_score >= high12_raw THEN high12_char
            WHEN raw_score >= high13_raw THEN high13_char
            WHEN raw_score >= high14_raw THEN high14_char
            WHEN raw_score >= high15_raw THEN high15_char
            WHEN raw_score >= high16_raw THEN high16_char
            WHEN raw_score >= high17_raw THEN high17_char
            WHEN raw_score >= high18_raw THEN high18_char
            WHEN raw_score >= lowest_raw THEN lowest_char
            ELSE NULL
        END AS 'Grade'
    FROM
        tblStudents
            JOIN
        tblStudent_assignments ON idStudents = tblStudent_assignments.students_id
            JOIN
        tblAssignments_info ON idAssignments_info = tblStudent_assignments.assignments_info_id
            JOIN
        tblGrading_groups ON tblGrading_groups.assignments_info_id = idAssignments_info
            JOIN
        tblGrade_thresholds ON grade_thresholds_id = idGrade_thresholds
            JOIN
        tblLetter_grade_chars ON letter_grade_chars_id = idLetter_grade_chars;

-- -----------------------------------------------------
-- View `SRM`.`vw_Students_assignments_grades_min`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SRM`.`vw_Students_assignments_grades_min`;
USE `SRM`;
CREATE  OR REPLACE VIEW `vw_Students_assignments_grades_min` AS
    SELECT 
        idStudents,
        Student_reg_number,
        student_fname,
        student_mid_initial,
        student_lname,
        student_email,
        assignment_title,
        max_raw_score,
        raw_score,
        CASE
            WHEN raw_score >= highest_raw THEN highest_char
            WHEN raw_score >= high1_raw THEN high1_char
            WHEN raw_score >= high2_raw THEN high2_char
            WHEN raw_score >= high3_raw THEN high3_char
            WHEN raw_score >= high4_raw THEN high4_char
            WHEN raw_score >= high5_raw THEN high5_char
            WHEN raw_score >= high6_raw THEN high6_char
            WHEN raw_score >= high7_raw THEN high7_char
            WHEN raw_score >= high8_raw THEN high8_char
            WHEN raw_score >= high9_raw THEN high9_char
            WHEN raw_score >= high10_raw THEN high10_char
            WHEN raw_score >= high11_raw THEN high11_char
            WHEN raw_score >= high12_raw THEN high12_char
            WHEN raw_score >= high13_raw THEN high13_char
            WHEN raw_score >= high14_raw THEN high14_char
            WHEN raw_score >= high15_raw THEN high15_char
            WHEN raw_score >= high16_raw THEN high16_char
            WHEN raw_score >= high17_raw THEN high17_char
            WHEN raw_score >= high18_raw THEN high18_char
            WHEN raw_score >= lowest_raw THEN lowest_char
            ELSE NULL
        END AS 'Grade',
        type_of_assessment,
        comments_for_guardian,
        comments_for_staff
    FROM
        tblStudents
            JOIN
        tblStudent_assignments ON idStudents = tblStudent_assignments.students_id
            JOIN
        tblAssignments_info ON idAssignments_info = tblStudent_assignments.assignments_info_id
            JOIN
        tblGrading_groups ON tblGrading_groups.assignments_info_id = idAssignments_info
            JOIN
        tblGrade_thresholds ON grade_thresholds_id = idGrade_thresholds
            JOIN
        tblLetter_grade_chars ON letter_grade_chars_id = idLetter_grade_chars;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `SRM`.`tblLINQ_users`
-- -----------------------------------------------------
START TRANSACTION;
USE `SRM`;
INSERT INTO `SRM`.`tblLINQ_users` (`idLINQ_users`, `user_fname`, `user_lname`, `LINQ_username`, `LINQ_pw`, `LINQ_useremail`, `LINQ_lastlogin`) VALUES (1, 'James', 'Apps', 'japps', 'japps', 'japps@somewhere.com', '2020-04-20');

COMMIT;

-- begin attached script 'script'
-- this may need removing on first run since an error is given if DROP fails
DROP USER 'SRM_admin'@'localhost';

USE SRM;
-- CREATE USER username IDENTIFIED BY password

-- add the SRM-server admin (change the password as desired)
-- it is strongly advised to specifiy the host, either locally with @localhost or remotely, with @ip_address_of_server

CREATE USER 'SRM_admin'@'localhost' IDENTIFIED BY 'adminpassword';

-- grant privileges to the administrator (perform queries, and edit records) and allow the administrator to grant the same privileges to other users

GRANT SELECT, INSERT, UPDATE, DELETE ON linq.* TO 'SRM_admin'@'localhost' WITH GRANT OPTION;
-- end attached script 'script'
-- begin attached script 'script1'
USE SRM;

/*
This script sets up the database with random, fake entries
Note the order that these scripts are processed matters since foreign keys must be set after primary keys have been set.

Due to foreign key constraints, the order of INSERTing 
data should be as follows:

1. Students
2. (In no particular order) Guardians, Subjects and Teachers
3. (In no particular order) Guardians_addresses, Subject_Subjects, 
Form_groups, Subject_Teachers_groups and Student_reports
4. Academic_classes

Once the basic school admin is entered, teachers can then begin entering 
assignments related data:

5. Assignments_info
6. (In no particular order) Assignments_teacher_info, Grade_thresholds,
Letter_grade_chars and Student_Assignments
7. Grading Groups
*/

-- PART 1 -------------------------------------------------------------------------------------------------------------------
INSERT INTO tblStudents(Student_fname, Student_lname, Student_mid_initial, Student_email, Student_phone)
VALUES('James', 'Bob', 'T', 'jamesbob@email.com', '02973432'),
('Jane', 'Bob', 'L M', 'jane12432@email.com', '534534'),
('Tim', 'Tom', '', 'timetomtam@tim.co.uk', '2039402934'),
('Jake', 'Josh', 'P S', 'whizbang@pop.com', '353453453'),
('Chris', 'Smith', 'O', 'chris@someplace.com', '029347823'),
('Jill', 'Jungle', 'E', 'Jill93@email.net', '230493894'),
('Sam', 'Dodds', 'A', 'samdodds21@threo.net', '123097234');

-- PART 2 -------------------------------------------------------------------------------------------------------------------
INSERT INTO tblGuardians(guardian_fname, guardian_lname, guardian_phone, guardian_email, guardian_2nd_email, students_id) 
VALUES('Frank', 'Bob', '2342523', 'fbob245@email.com', 'paosih@apsoih', 1),
('Frank', 'Bob', '2342523', 'fbob245@email.com', 'paosih@apsoih', 2),
('Amy', 'Josh', '343534345', 'ajosh22@email.com', 'oaihsd@asfh', 4),
('Joyce', 'Tom', '23454323245', 'joyce33ok@email.com', 'opasih@aspoidh', 3),
('Samuel', 'Smith', '23452354', 'samsmith@torr.net', 'oiashd@aspodih', 5),
('David', 'Jungle', '2346435', 'DaveJ39595@email.com', 'opaish@aopsih', 6),
('Don', 'Dodds', '234523423', 'quack2345@somewhere.com', 'aosidh@asopid', 7);

INSERT INTO tblTeachers(Teacher_fname, Teacher_lname, form_group_name, Teacher_work_email, Teacher_phone) VALUES
('Edward', 'Jones', 'EJ7', 'edwardj@someschool.com', '2342345'),
('Emily', 'Ford', 'EF8', 'emilyf@someschool.com', '2354234');

INSERT INTO tblSubjects(subject_title) VALUES
('Math'), ('English'), ('Science');

-- PART 3 -------------------------------------------------------------------------------------------------------------------
INSERT INTO tblGuardians_addresses(first_line, county_state, postcode_zipcode, country, guardians_id) 
VALUES('11 Hope St', 'London', 'EJ6', 'UK', 1),
('11 Hope St', 'London', 'EJ6', 'UK', 2),
('104 Canal avenue', 'Lancs', 'dunno', 'UK', 3),
('Flat 8A Block 3, somewhere', 'Hunts', 'tbc', 'UK', 4),
('Cheddar, Alders Grove, Inverness', 'Inverness', 'dunno2', 'UK', 5),
('No. 88, Embers Way, Leamington', 'Warwickshire', 'FS33', 'UK', 6),
('All Saints, Torquay Way, Torquay', 'Devonshire', 'DV73', 'UK', 7);

INSERT INTO tblStudents_subjects(students_id, subjects_id) VALUES
(1, 1), (2, 1), (3, 1), (4, 2), (5, 2), (6, 2), (7, 2);

INSERT INTO tblForm_groups(teachers_id, students_id) VALUES
(1, 1), (1, 3), (1, 5), (1, 7),
(2, 2), (2, 4), (2, 6);

INSERT INTO tblSubjects_teachers_groups(subject_class_name, subjects_id, teachers_id) VALUES
('Mathtastic', 1, 2), ('Englooosh', 2, 1);

-- PART 4 -------------------------------------------------------------------------------------------------------------------
INSERT INTO tblAcademic_classes(subjects_teachers_groups_id, students_id)
VALUES(1, 1), (1, 2), (1, 3), (2, 4), (2, 5), (2, 6), (2, 7);

-- PART 5 -------------------------------------------------------------------------------------------------------------------

INSERT INTO tblAssignments_info(assignment_title, assignment_detail, max_raw_score, type_of_assessment) 
VALUES('The makings of King Arthur', 'An essay focusing on something', 25, 'C'),
('Taming the shrew', 'Comparing film and book', 32, 'C'),
('Prep test for Calc 1', 'prep test', 70, 'T'),
('Calc 1 exam', 'end of term exam', 70, 'E');

-- PART 6 -------------------------------------------------------------------------------------------------------------------
INSERT INTO tblAssignments_teacher_info(assignment_entry_date, add_to_average, assignments_info_id, teachers_id)
VALUES('2020-07-27', 1, 1, 1),
('2020-07-28', 1, 2, 1),
('2020-07-27', 1, 3, 2),
('2020-07-28', 1, 4, 2);

INSERT INTO tblGrade_thresholds(Highest_raw, High1_raw, High2_raw, High3_raw, Lowest_raw)
VALUES (23, 20, 18, 15, 13), (30, 28, 26, 24, 20), (65, 60, 55, 50, 45), (65, 60, 55, 50, 45);

INSERT INTO tblLetter_grade_chars(Highest_char, High1_char, High2_char, High3_char, Lowest_char)
VALUES ('A', 'B', 'C', 'D', 'E'), ('a', 'b', 'c', 'd', 'e');

INSERT INTO tblStudent_assignments(assignments_info_id, raw_score, students_id) 
VALUES(1, 20, 4), (1, 21, 5), (1, 18, 6), (1, 19, 7),
(2, 25, 4), (2, 27, 5), (2, 20, 6), (2, 30, 7),
(3, 55, 1), (3, 40, 2), (3, 60, 3),
(4, 60, 1), (4, 45, 2), (4, 66, 3);

-- PART 7 -------------------------------------------------------------------------------------------------------------------
INSERT INTO tblGrading_groups(assignments_info_id, grade_thresholds_id, letter_grade_chars_id) VALUES(1, 1, 1), (2, 2, 1), (3, 3, 2), (4, 4, 2);


-- end attached script 'script1'
