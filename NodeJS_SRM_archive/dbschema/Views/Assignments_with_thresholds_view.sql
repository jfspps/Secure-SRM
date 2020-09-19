CREATE VIEW `vw_Assignments_with_thresholds` AS
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