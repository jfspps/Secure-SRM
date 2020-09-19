CREATE VIEW `vw_Academic_classes` AS
    SELECT 
        *
    FROM
        tblSubjects_Teachers_groups
            JOIN
        tblTeachers ON tblSubjects_Teachers_groups.Teachers_id = idTeachers
            RIGHT JOIN
        tblSubjects ON tblSubjects_Teachers_groups.Subjects_id = idSubjects;