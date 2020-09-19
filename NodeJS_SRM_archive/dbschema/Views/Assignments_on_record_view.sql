CREATE VIEW `vw_Assignments_on_record` AS
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