package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.ForbiddenException;
import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.academic.*;
import com.secure_srm.model.people.Student;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.services.academicServices.StudentResultService;
import com.secure_srm.services.academicServices.StudentTaskService;
import com.secure_srm.services.peopleServices.StudentService;
import com.secure_srm.services.securityServices.TeacherUserService;
import com.secure_srm.services.securityServices.UserService;
import com.secure_srm.web.permissionAnnot.TeacherCreate;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import com.secure_srm.web.permissionAnnot.TeacherUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/studentResult")
public class StudentResultController {

    private final StudentResultService studentResultService;
    private final StudentService studentService;
    private final TeacherUserService teacherUserService;
    private final StudentTaskService studentTaskService;
    private final AuxiliaryController auxiliaryController;
    private final UserService userService;

    //prevent the HTTP form POST from editing listed properties
    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder){
        dataBinder.setDisallowedFields("id");
    }

    @ModelAttribute("hasSubject")
    public Boolean teachesSubjects(){
        //determines if a User is a teacher and then if they teach anything (blocks New Student Task/Report/Result as appropriate)
        return auxiliaryController.teachesASubject();
    }

    @TeacherRead
    @GetMapping({"/", "/index"})
    public String getResultIndex(Model model){
        model.addAttribute("results", studentResultService.findAll());
        return "/SRM/studentResults/resultsIndex";
    }

    @TeacherCreate
    @GetMapping("/new")
    public String getNewResult(Model model){
        if (!auxiliaryController.teachesASubject()){
            log.debug("User does not have prerequisites");
            throw new ForbiddenException("User does not have prerequisites");
        }

        if (studentTaskService.findAll().isEmpty()){
            log.debug("Student task DB empty");
            model.addAttribute("message", "At least one student task must exist in order to save a result. Click Resolve to create a new task.");
            model.addAttribute("resolution", "/studentTask/new");
            return "/SRM/customMessage";
        }

        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();
        Set<Subject> subjectsTaught = currentTeacher.getSubjects();
        model.addAttribute("students", auxiliaryController.sortStudentSetByLastName(studentService.findAll()));

        //list tasks relevant to current teacher's subject(s)
        Set<StudentTask> tasks = new HashSet<>();
        for (Subject subject: subjectsTaught) {
            tasks.addAll(studentTaskService.findAllBySubject(subject.getSubjectName()));
        }
        model.addAttribute("studentTasks", tasks);

        StudentResult newResult = StudentResult.builder()
                .teacher(currentTeacher)
                .build();

        model.addAttribute("result", newResult);

        return "/SRM/studentResults/newResult";
    }

    @TeacherCreate
    @GetMapping("/new/search")
    public String getNewResult_Search(Model model, String StudentLastName, String TaskTitle){
        Set<Student> studentSet;
        if (StudentLastName == null || StudentLastName.isEmpty()){
            studentSet = studentService.findAll();
            model.addAttribute("students", auxiliaryController.sortStudentSetByLastName(studentSet));
        } else {
            studentSet = studentService.findAllByLastNameContainingIgnoreCase(StudentLastName);
            model.addAttribute("students", studentSet);
        }

        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();
        Set<Subject> subjectsTaught = currentTeacher.getSubjects();
        Set<StudentTask> tasks = new HashSet<>();
        for (Subject subject: subjectsTaught) {
            tasks.addAll(studentTaskService.findAllBySubject(subject.getSubjectName()));
        }

        if (TaskTitle.length() > 1){
            tasks.removeIf(studentTask -> !studentTask.getTitle().equals(TaskTitle));
        }

        model.addAttribute("studentTasks", tasks);

        StudentResult newResult = StudentResult.builder()
                .teacher(currentTeacher)
                .build();

        model.addAttribute("result", newResult);

        return "/SRM/studentResults/newResult";
    }

    @TeacherCreate
    @PostMapping("/new")
    public String postNewResult(@Valid @ModelAttribute("result") StudentResult studentResult, BindingResult bindingResult, Model model){
        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();

        if (bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));

            Set<Subject> subjectsTaught = currentTeacher.getSubjects();
            model.addAttribute("result", studentResult);
            model.addAttribute("students", auxiliaryController.sortStudentSetByLastName(studentService.findAll()));

            //list tasks relevant to current teacher's subject(s)
            Set<StudentTask> tasks = new HashSet<>();
            for (Subject subject: subjectsTaught) {
                tasks.addAll(studentTaskService.findAllBySubject(subject.getSubjectName()));
            }
            model.addAttribute("studentTasks", tasks);
            return "/SRM/studentResults/newResult";
        }

        //set result with current user's details
        studentResult.setTeacher(currentTeacher);

        //update StudentTask tally
        StudentTask taskOnFile = studentResult.getStudentTask();
        taskOnFile.getStudentResults().add(studentResult);
        StudentTask savedTask = studentTaskService.save(taskOnFile);
        log.debug("Task " + savedTask.getTitle() + " updated with new result");

        StudentResult savedResult = studentResultService.save(studentResult);
        log.debug("Result from task, " + savedResult.getStudentTask().getTitle() + ", saved");

        try {
            if (Long.parseLong(studentResult.getScore()) > Long.parseLong(studentResult.getStudentTask().getMaxScore())){
                log.debug("Submitted score is greater than the maximum score");
                model.addAttribute("resultFeedback", "Note: submitted score is greater than the maximum score. Result saved.");
            } else {
                model.addAttribute("resultFeedback", "New result saved");
            }
        } catch (NumberFormatException exception){
            log.debug("Could not parse maxscore or score");
            model.addAttribute("resultFeedback", "New result saved");
        }

        model.addAttribute("result", savedResult);
        return "/SRM/studentResults/viewResult";
    }

    @TeacherRead
    @GetMapping("/{resultId}")
    public String getViewResult(Model model, @PathVariable("resultId") String resultID){
        if (studentResultService.findById(Long.valueOf(resultID)) == null){
            log.debug("Student result not found");
            throw new NotFoundException("Student result not found");
        }

        StudentResult found = studentResultService.findById(Long.valueOf(resultID));
        model.addAttribute("result", found);

        // pass the grade based on thresholds set (this may need optimising in future)
        model.addAttribute("thresholdListsAndGrades", getResultAndThresholdLists(found));

        return "/SRM/studentResults/viewResult";
    }

    /**
     * @param found StudentResult
     * @return Returns a set of Result-threshold pairings; returns an empty HashSet if no
     * threshold lists are associated with the student task
     */
    protected Set<ResultAndThresholdList> getResultAndThresholdLists(StudentResult found) {
        Set<ThresholdList> thresholdLists = found.getStudentTask().getThresholdListSet();
        Set<ResultAndThresholdList> resultAndThresholdLists = new HashSet<>();

        if (!thresholdLists.isEmpty()) {
            for (ThresholdList list : thresholdLists) {
                if (list.getThresholds().isEmpty()) {
                    continue;
                }
                String grade = "No grade available";
                int max = 0;
                int score;

                // allow for blank entries (student absence etc.)
                if (found.getScore().isBlank()){
                    score = 0;
                } else {
                    score = Integer.parseInt(found.getScore());
                }

                // go through each Threshold in list;
                // threshold represent the lowest score needed to secure a given grade
                for (Threshold threshold : list.getThresholds()) {
                    if (score >= threshold.getNumerical() && threshold.getNumerical() >= max) {
                        max = threshold.getNumerical();
                        grade = threshold.getAlphabetical();
                    }
                }
                resultAndThresholdLists.add(new ResultAndThresholdList(grade, list));
            }
        }
        return resultAndThresholdLists;
    }

    @TeacherUpdate
    @GetMapping("/{resultId}/edit")
    public String getUpdateResult(Model model, @PathVariable("resultId") String resultID){
        if (studentResultService.findById(Long.valueOf(resultID)) == null){
            log.debug("Student result not found");
            throw new NotFoundException("Student result not found");
        }
        StudentResult found = studentResultService.findById(Long.valueOf(resultID));
        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();

        //check that the current teacher is the result's marker
        if (found.getTeacher() != currentTeacher){
            log.debug("Current teacher cannot edit results of other teachers");
            throw new ForbiddenException("Current teacher cannot edit results of other teachers");
        }

        Set<Subject> subjectsTaught = currentTeacher.getSubjects();
        model.addAttribute("students", auxiliaryController.sortStudentSetByLastName(studentService.findAll()));

        //list tasks relevant to current teacher's subject(s)
        Set<StudentTask> tasks = new HashSet<>();
        for (Subject subject: subjectsTaught) {
            tasks.addAll(studentTaskService.findAllBySubject(subject.getSubjectName()));
        }
        model.addAttribute("studentTasks", tasks);
        model.addAttribute("result", found);
        return "/SRM/studentResults/updateResult";
    }

    @TeacherUpdate
    @GetMapping("/{resultId}/edit/search")
    public String getUpdateResult_search(Model model, @PathVariable("resultId") String resultID, String StudentLastName,
                                         String TaskTitle, @ModelAttribute("result") StudentResult studentResult){
        if (studentResultService.findById(Long.valueOf(resultID)) == null){
            log.debug("Student result not found");
            throw new NotFoundException("Student result not found");
        }
        StudentResult found = studentResultService.findById(Long.valueOf(resultID));
        TeacherUser currentTeacher = userService.findByUsername(auxiliaryController.getUsername()).getTeacherUser();
        Set<Subject> subjectsTaught = currentTeacher.getSubjects();

        if (StudentLastName == null || StudentLastName.isEmpty()){
            model.addAttribute("students", studentService.findAll());
        } else {
            model.addAttribute("students",
                    studentService.findAllByLastNameContainingIgnoreCase(StudentLastName));
        }

        Set<StudentTask> tasks = new HashSet<>();
        for (Subject subject: subjectsTaught) {
            tasks.addAll(studentTaskService.findAllBySubject(subject.getSubjectName()));
        }

        if (TaskTitle != null && !TaskTitle.isBlank()){
            tasks = studentTaskService.findAllByTitleIgnoreCase(TaskTitle);
        }

        model.addAttribute("studentTasks", tasks);
        model.addAttribute("result", found);
        return "/SRM/studentResults/updateResult";
    }

    @TeacherUpdate
    @PostMapping("{resultId}/edit")
    public String postUpdateResult(@Valid @ModelAttribute("result") StudentResult studentResult, BindingResult bindingResult,
                                   Model model, @PathVariable("resultId") String resultID){
        if (studentResultService.findById(Long.valueOf(resultID)) == null){
            log.debug("Student result not found");
            throw new NotFoundException("Student result not found");
        }
        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();
        StudentResult resultOnFile = studentResultService.findById(Long.valueOf(resultID));

        //check that the current teacher is the result's marker
        if (resultOnFile.getTeacher() != currentTeacher){
            log.debug("Current teacher cannot edit results of other teachers");
            throw new ForbiddenException("Current teacher cannot edit results of other teachers");
        }

        if (bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> log.debug(objectError.toString()));

            Set<Subject> subjectsTaught = currentTeacher.getSubjects();
            model.addAttribute("result", studentResult);
            model.addAttribute("students", auxiliaryController.sortStudentSetByLastName(studentService.findAll()));

            //list tasks relevant to current teacher's subject(s)
            Set<StudentTask> tasks = new HashSet<>();
            for (Subject subject: subjectsTaught) {
                tasks.addAll(studentTaskService.findAllBySubject(subject.getSubjectName()));
            }
            model.addAttribute("studentTasks", tasks);
            return "/SRM/studentResults/newResult";
        }

        StudentTask taskOnFile = resultOnFile.getStudentTask();

        StudentTask taskSubmitted = studentResult.getStudentTask();

        //update StudentTasks tally
        taskOnFile.getStudentResults().remove(resultOnFile);
        StudentTask onFileSaved = studentTaskService.save(taskOnFile);
        taskSubmitted.getStudentResults().add(resultOnFile);
        StudentTask submittedSaved = studentTaskService.save(taskSubmitted);

        log.debug("Result removed from " + onFileSaved.getTitle());
        log.debug("Result saved to " + submittedSaved.getTitle());

        //update resultOnFile (no need to change teacher)
        resultOnFile.setStudent(studentResult.getStudent());
        resultOnFile.setStudentTask(studentResult.getStudentTask());
        resultOnFile.setComments(studentResult.getComments());
        resultOnFile.setScore(studentResult.getScore());

        StudentResult savedResult = studentResultService.save(resultOnFile);
        log.debug("Result from task, " + savedResult.getStudentTask().getTitle() + ", saved");

        try {
            if (Long.parseLong(studentResult.getScore()) > Long.parseLong(studentResult.getStudentTask().getMaxScore())){
                log.debug("Submitted score is greater than the maximum score");
                model.addAttribute("resultFeedback", "Note: submitted score is greater than the maximum score. Result updated.");
            } else {
                model.addAttribute("resultFeedback", "Result updated");
            }
        } catch (NumberFormatException exception){
            log.debug("Could not parse maxscore or score");
            model.addAttribute("resultFeedback", "Result updated");
        }

        model.addAttribute("result", savedResult);
        return "/SRM/studentResults/viewResult";
    }
}