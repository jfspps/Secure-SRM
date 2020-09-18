/*
The following statements relate operations on the tables:

5. Assignments_info
6. (In no particular order) Assignments_sub_by_a_teacher, Grade_thresholds,
Letter_grade_chars and Student_Assignments
7. Grading Groups
*/

SELECT 
    idGrading_groups,
    assignment_id,
    Grade_thresholds_id,
    Letter_grade_chars_id,
    Highest_raw_threshold,
    Highest_char,
    High1_raw,
    High1_char,
    High2_raw,
    High2_char,
    High3_raw,
    High3_char,
    Lowest_raw,
    Lowest_char
FROM
    grading_groups
        JOIN
    grade_thresholds ON grade_thresholds_id = idGrade_thresholds
        JOIN
    letter_grade_chars ON letter_grade_chars_id = idLetter_grade_chars;
    
-- this lists the grades available for each assignment, with assignment details
SELECT 
    idGrading_groups,
    assignment_title,
    max_raw_score,
    Highest_raw_threshold,
    Highest_char,
    High1_raw,
    High1_char,
    High2_raw,
    High2_char,
    High3_raw,
    High3_char,
    Lowest_raw,
    Lowest_char
FROM
    grading_groups
        JOIN
    grade_thresholds ON grade_thresholds_id = idGrade_thresholds
        JOIN
    letter_grade_chars ON letter_grade_chars_id = idLetter_grade_chars
        JOIN
    assignments_info ON assignment_id = idAssignments_info;