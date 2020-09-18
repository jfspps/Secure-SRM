CREATE VIEW `vw_Students_assignments_grades_min` AS
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