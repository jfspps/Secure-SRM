CREATE VIEW `vw_Subjects_available` AS
select * from tblSubjects left join tblStudents_Subjects on idSubjects = tblStudents_Subjects.Subjects_id;