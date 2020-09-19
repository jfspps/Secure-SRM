CREATE VIEW `vw_Assignments_with_scores_and_grades` AS
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