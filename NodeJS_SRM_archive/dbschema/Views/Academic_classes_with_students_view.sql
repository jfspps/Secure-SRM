CREATE VIEW `vw_Academic_classes_with_students` AS
    SELECT 
        *
    FROM
        tblStudents
            JOIN
        tblAcademic_classes ON idStudents = tblAcademic_classes.Students_id
            JOIN
        tblSubjects_Teachers_groups ON Subjects_Teachers_groups_id = idSubjects_teachers_group
            JOIN
        tblTeachers ON tblSubjects_Teachers_groups.Teachers_id = idTeachers
            JOIN
        tblSubjects ON tblSubjects_Teachers_groups.Subjects_id = idSubjects;