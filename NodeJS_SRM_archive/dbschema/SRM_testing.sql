/*
The following statements relate operations on the tables:

1. Students
2. (In no particular order) Guardians, Subjects and Teachers
3. (In no particular order) Guardians_addresses, Subject_JUNC_Subjects, 
Form_group, Subject_Teachers_group and Student_report
4. Academic_class
*/

SELECT 
    guardian_fname,
    guardian_lname,
    guardian_email,
    student_id,
    student_fname,
    student_lname
FROM
    guardians
        JOIN
    students ON student_id = idStudents;

-- this lists the address of each Guardian
SELECT 
    guardian_fname, guardian_lname, first_line, country
FROM
    guardians_addresses
        JOIN
    guardians ON Guardian_id = idGuardians;

-- this lists students and the subjects they study
SELECT 
    student_fname, student_lname, subject_title
FROM
    students
        JOIN
    students_junc_subjects ON idStudents = student_id
        JOIN
    subjects ON subject_id = idsubjects;
    
-- This lists students by form group:
SELECT 
    student_fname, student_lname, teacher_lname, form_group_name
FROM
    students
        JOIN
    form_group ON student_id = idStudents
        JOIN
    teachers ON teacher_id = idteachers
ORDER BY teacher_id , student_lname;

-- this lists teachers and the subjects they teach:
SELECT 
	idSubjects_Teachers_group,
    subject_title,
    subject_class_name,
    teacher_fname,
    teacher_lname
FROM
    subjects
        JOIN
    subjects_teachers_group ON idSubjects = subject_id
        JOIN
    teachers ON teacher_id = idTeachers;

-- this lists the students' name by academic class group
SELECT 
    Student_fname,
    Student_lname,
    Subjects_Teachers_group_id,
    Subject_class_name
FROM
    students
        JOIN
    academic_class ON student_id = idStudents
        JOIN
    subjects_teachers_group ON Subjects_Teachers_group_id = idSubjects_Teachers_group;