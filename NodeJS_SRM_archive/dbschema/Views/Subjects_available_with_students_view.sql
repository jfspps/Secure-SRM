CREATE VIEW `vw_Subjects_available_with_students` AS
    SELECT 
        *
    FROM
        tblSubjects
            LEFT JOIN
        tblStudents_Subjects ON idSubjects = tblStudents_Subjects.Subjects_id
            LEFT JOIN
        tblStudents ON tblStudents_Subjects.Students_id = idStudents;