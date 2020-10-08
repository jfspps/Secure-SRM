package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.academic.Report;
import com.secure_srm.model.academic.Subject;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.services.academicServices.ReportService;
import com.secure_srm.services.academicServices.SubjectService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.web.permissionAnnot.TeacherCreate;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import com.secure_srm.web.permissionAnnot.TeacherUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/studentReports")
public class StudentReportController {

    private final ReportService reportService;
    private final StudentService studentService;
    private final TeacherUserService teacherUserService;
    private final SubjectService subjectService;

    //prevent the HTTP form POST from editing listed properties
    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder){
        dataBinder.setDisallowedFields("id");
    }

    @TeacherRead
    @GetMapping({"/", "/index"})
    public String getReportIndex(Model model){
        model.addAttribute("reports", reportService.findAll());
        return "/SRM/studentReports/reportIndex";
    }

    @TeacherCreate
    @GetMapping("/new")
    public String getNewReport(Model model){
        model.addAttribute("report", Report.builder().build());
        model.addAttribute("teachers", teacherUserService.findAll());
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("subjects", subjectService.findAll());
        return "/SRM/studentReports/newReport";
    }

    @TeacherCreate
    @GetMapping("/new/search")
    public String getNewReport_refineOptions(Model model, @ModelAttribute("report") Report report, String TeacherLastName,
                                             String StudentLastName, String SubjectName){
        model.addAttribute("report", report);

        if (TeacherLastName == null || TeacherLastName.isEmpty()){
            model.addAttribute("teachers", teacherUserService.findAll());
        } else {
            Set<TeacherUser> teacherUsers = new HashSet<>(teacherUserService.findAllByLastNameContainingIgnoreCase(TeacherLastName));
            teacherUsers.add(report.getTeacher());
            model.addAttribute("teachers", teacherUsers);
        }

        if (StudentLastName == null || StudentLastName.isEmpty()){
            model.addAttribute("students", studentService.findAll());
        } else {
            Set<Student> students = new HashSet<>(studentService.findAllByLastNameContainingIgnoreCase(StudentLastName));
            students.add(report.getStudent());
            model.addAttribute("students", students);
        }

        if (SubjectName == null || SubjectName.isEmpty()){
            model.addAttribute("subjects", subjectService.findAll());
        } else {
            Set<Subject> subjects = new HashSet<>(subjectService.findBySubjectNameContainingIgnoreCase(SubjectName));
            subjects.add(report.getSubject());
            model.addAttribute("subjects", subjects);
        }

        return "/SRM/studentReports/newReport";
    }

    @TeacherCreate
    @PostMapping("/new")
    public String postNewReport(Model model, @ModelAttribute("report") Report report){
        //check that all three properties are populated
        if (report.getStudent() == null || report.getSubject() == null || report.getTeacher() == null){
            log.debug("Missing student, teacher and/or subject properties");
            model.addAttribute("teachers", teacherUserService.findAll());
            model.addAttribute("students", studentService.findAll());
            model.addAttribute("subjects", subjectService.findAll());
            model.addAttribute("report", report);
            model.addAttribute("uniqueId", "Please choose a teacher, student and subject");
            return "/SRM/studentReports/newReport";
        }

        if (reportService.findByStudentLastName(report.getStudent().getLastName()) != null &&
        reportService.findByTeacherLastName(report.getTeacher().getLastName()) != null &&
        reportService.findBySubject(report.getSubject().getSubjectName()) != null){
            //check the unique identifier
            if (reportService.findByUniqueIdentifier(report.getUniqueIdentifier()) != null){
                log.debug("Report with given unique identifier already exists");
                model.addAttribute("uniqueId", "Report with given unique identifier already exists");
                model.addAttribute("teachers", teacherUserService.findAll());
                model.addAttribute("students", studentService.findAll());
                model.addAttribute("subjects", subjectService.findAll());
                model.addAttribute("report", report);
                return "/SRM/studentReports/newReport";
            }
        }

        Report saved = reportService.save(report);
        log.debug("Student report saved");
        model.addAttribute("reportFeedback", "Student report saved");
        model.addAttribute("report", saved);

        return "/SRM/studentReports/viewReport";
    }

    @TeacherRead
    @GetMapping("/{reportID}")
    public String viewReport(Model model, @PathVariable("reportID") String reportID){
        if (reportService.findById(Long.valueOf(reportID)) == null){
            log.debug("Student report not found");
            throw new NotFoundException("Report not found");
        }

        model.addAttribute("report", reportService.findById(Long.valueOf(reportID)));
        return "/SRM/studentReports/viewReport";
    }

    @TeacherUpdate
    @GetMapping("/{reportID}/edit")
    public String getUpdateReport(Model model, @PathVariable("reportID") String reportID){
        if (reportService.findById(Long.valueOf(reportID)) == null){
            log.debug("Student report not found");
            throw new NotFoundException("Report not found");
        }

        model.addAttribute("teachers", teacherUserService.findAll());
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("subjects", subjectService.findAll());
        model.addAttribute("report", reportService.findById(Long.valueOf(reportID)));
        return "/SRM/studentReports/updateReport";
    }

    @TeacherUpdate
    @PostMapping("/{reportID}/edit")
    public String postUpdateReport(Model model, @ModelAttribute("report") Report report,
                                   @PathVariable("reportID") String reportID){
        Report onFile = reportService.findById(Long.valueOf(reportID));
        onFile.setComments(report.getComments());
        onFile.setStudent(report.getStudent());
        onFile.setSubject(report.getSubject());
        onFile.setTeacher(report.getTeacher());

        Report saved = reportService.save(onFile);
        log.debug("Student report updated");
        model.addAttribute("reportFeedback", "Student report updated");
        model.addAttribute("report", saved);

        return "/SRM/studentReports/viewReport";
    }
}
