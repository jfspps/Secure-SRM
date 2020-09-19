USE linq;

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

