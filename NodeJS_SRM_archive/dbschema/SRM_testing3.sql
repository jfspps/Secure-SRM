-- this statements lists the students with the assignments they have taken and their marks
SELECT 
    Student_fname,
    Student_lname,
    raw_score,
    Assignment_title,
    Max_raw_score
FROM
    students
        JOIN
    student_assignments ON student_id = idStudents
        JOIN
    assignments_info ON student_assignments.Assignment_id = idAssignments_info;

-- this statements lists the students with the assignments they have taken and their grades
SELECT 
    Student_fname,
    Student_lname,
    Assignment_title,
    raw_score AS 'Student\'s score',
    Max_raw_score AS 'Max score',
    CASE
        WHEN raw_score >= highest_raw_threshold THEN highest_char
        WHEN raw_score >= high1_raw THEN high1_char
        WHEN raw_score >= high2_raw THEN high2_char
        WHEN raw_score >= high3_raw THEN high3_char
        ELSE lowest_char
    END AS 'Grade'
FROM
    students
        JOIN
    student_assignments ON student_id = idStudents
        JOIN
    assignments_info ON student_assignments.Assignment_id = idAssignments_info
        JOIN
    grading_groups ON grading_groups.Assignment_id = idAssignments_info
        JOIN
    letter_grade_chars ON Letter_grade_chars_id = idLetter_grade_chars
        JOIN
    grade_thresholds ON grade_thresholds_id = idGrade_thresholds
ORDER BY student_id;