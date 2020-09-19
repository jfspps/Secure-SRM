-- this shows the percentage for each assignment, rounded to 1 d.p, for English.
SELECT 
    idStudents,
    Subject_class_name,
    student_fname,
    student_lname,
    student_email,
    Assignment_title,
    raw_score,
    CONCAT(ROUND(100 * (Raw_score / max_raw_score), 1), '%') AS 'percentage'
FROM
    students
        JOIN
    academic_class ON idStudents = academic_class.Student_id
        JOIN
    subjects_teachers_group ON Subjects_Teachers_group_id = idSubjects_Teachers_group
        JOIN
    student_assignments ON idStudents = student_assignments.Student_id
        JOIN
    assignments_info ON idAssignments_info = student_assignments.Assignment_id
WHERE
    Teacher_id = 1
order BY assignment_title;

-- this shows the all the scores for a particular assignment
SELECT 
    idStudents,
    Subject_class_name,
    student_fname,
    student_lname,
    student_email,
    Assignment_title,
    raw_score,
    CONCAT(ROUND(100 * (Raw_score / max_raw_score), 1), '%') AS 'percentage'
FROM
    students
        JOIN
    academic_class ON idStudents = academic_class.Student_id
        JOIN
    subjects_teachers_group ON Subjects_Teachers_group_id = idSubjects_Teachers_group
        JOIN
    student_assignments ON idStudents = student_assignments.Student_id
        JOIN
    assignments_info ON idAssignments_info = student_assignments.Assignment_id
WHERE
    idAssignments_info = 1
order BY assignment_title;

-- this lists the number of assignments on record, with the percentage average
SELECT 
    idStudents,
    Subject_class_name,
    student_fname,
    student_lname,
    student_email,
    count(*) as 'No. of assignments',
    round(avg(100*(raw_score/max_raw_score)), 1) as "Average so far"
FROM
    students
        JOIN
    academic_class ON idStudents = academic_class.Student_id
        JOIN
    subjects_teachers_group ON Subjects_Teachers_group_id = idSubjects_Teachers_group
        JOIN
    student_assignments ON idStudents = student_assignments.Student_id
        JOIN
    assignments_info ON idAssignments_info = student_assignments.Assignment_id
group by idStudents;