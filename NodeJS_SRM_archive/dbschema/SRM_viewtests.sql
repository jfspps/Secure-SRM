-- these statements test the included views

SELECT 
    concat(student_fname, ' ', student_lname) AS "Student name",
    assignment_title AS 'Assignment title',
    max_raw_score AS 'Max raw score',
    raw_score AS "Student's raw score",
    Grade,
    type_of_assessment AS 'Assessment type'
FROM
    students_assignments_grades_min;