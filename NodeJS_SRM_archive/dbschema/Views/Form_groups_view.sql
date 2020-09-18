CREATE VIEW `vw_Form_group_list` AS
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