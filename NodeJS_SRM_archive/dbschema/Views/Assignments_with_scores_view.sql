CREATE VIEW `vw_Assignments_with_scores` AS
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
    teacher_phone
FROM
    tblStudents
        JOIN
    tblStudent_assignments ON idStudents = tblStudent_assignments.students_id
        JOIN
    tblAssignments_info ON idAssignments_info = tblStudent_assignments.assignments_info_id
        JOIN
    tblAssignments_teacher_info ON tblAssignments_teacher_info.assignments_info_id = idAssignments_info
        JOIN
    tblTeachers ON idTeachers = tblAssignments_teacher_info.teachers_id;