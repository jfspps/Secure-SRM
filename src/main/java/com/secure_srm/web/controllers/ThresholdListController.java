package com.secure_srm.web.controllers;

import com.secure_srm.exceptions.ForbiddenException;
import com.secure_srm.exceptions.NotFoundException;
import com.secure_srm.model.academic.StudentTask;
import com.secure_srm.model.academic.Threshold;
import com.secure_srm.model.academic.ThresholdList;
import com.secure_srm.model.security.TeacherUser;
import com.secure_srm.services.academicServices.StudentTaskService;
import com.secure_srm.services.academicServices.ThresholdListService;
import com.secure_srm.services.academicServices.ThresholdService;
import com.secure_srm.web.permissionAnnot.TeacherCreate;
import com.secure_srm.web.permissionAnnot.TeacherRead;
import com.secure_srm.web.permissionAnnot.TeacherUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/thresholdLists"})
public class ThresholdListController {

    private final AuxiliaryController auxiliaryController;
    private final ThresholdService thresholdService;
    private final ThresholdListService thresholdListService;
    private final StudentTaskService studentTaskService;

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
    @GetMapping({"", "/", "/index"})
    public String listThresholdLists(Model model, String uniqueID) {
        if (uniqueID == null || uniqueID.isBlank()){
            model.addAttribute("thresholdLists", auxiliaryController.sortThresholdListByUniqueID(thresholdListService.findAll()));
        } else {
            model.addAttribute("thresholdLists",
                    auxiliaryController.sortThresholdListByUniqueID(thresholdListService.findAllByUniqueIDContainingIgnoreCase(uniqueID)));
        }

        return "/SRM/thresholdList/thresholdListIndex";
    }

    @TeacherCreate
    @GetMapping("/new")
    public String getNewThresholdList(Model model){
        if (!auxiliaryController.teachesASubject()){
            log.debug("User is not registered with any subject");
            model.addAttribute("message", "You are not currently registered to teach any subject");
            return "/SRM/customMessage";
        }

        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();
        List<Threshold> thresholdSet = auxiliaryController.sortThresholdByUniqueID(thresholdService.findAll());
        List<StudentTask> studentTaskList = auxiliaryController.sortStudentTaskByTitle(studentTaskService.findAll());
        ThresholdList newList = ThresholdList.builder().thresholds(new HashSet<>()).uploader(currentTeacher).build();
        model.addAttribute("thresholds", thresholdSet);
        model.addAttribute("thresholdList", newList);
        model.addAttribute("studentTasks", studentTaskList);
        return "/SRM/thresholdList/newThresholdList";
    }

    @TeacherCreate
    @GetMapping("/new/search")
    public String getNewThresholdList_search(Model model, String thresholdUniqueId, String studentTaskTitle){
        if (!auxiliaryController.teachesASubject()){
            log.debug("User is not registered with any subject");
            model.addAttribute("message", "You are not currently registered to teach any subject");
            return "/SRM/customMessage";
        }

        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();
        List<Threshold> thresholdSet;
        if (thresholdUniqueId != null || !thresholdUniqueId.isBlank()){
            thresholdSet = auxiliaryController.sortThresholdByUniqueID(thresholdService.findAllByUniqueIDContainingIgnoreCase(thresholdUniqueId));
        } else {
            thresholdSet = auxiliaryController.sortThresholdByUniqueID(thresholdService.findAll());
        }

        List<StudentTask> studentTaskList;
        if (studentTaskTitle != null || !studentTaskTitle.isBlank()){
            studentTaskList = auxiliaryController.sortStudentTaskByTitle(studentTaskService.findAllByTitleIgnoreCase(studentTaskTitle));
        } else {
            studentTaskList = auxiliaryController.sortStudentTaskByTitle(studentTaskService.findAll());
        }

        ThresholdList newList = ThresholdList.builder().thresholds(new HashSet<>()).uploader(currentTeacher).build();
        model.addAttribute("thresholds", thresholdSet);
        model.addAttribute("thresholdList", newList);
        model.addAttribute("studentTasks", studentTaskList);

        return "/SRM/thresholdList/newThresholdList";
    }

    @TeacherCreate
    @PostMapping("/new")
    public String postNewThresholdList(Model model, @ModelAttribute("threshold") ThresholdList submittedThresholdList){
        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();

        if (!submittedThresholdList.getUniqueID().isBlank() && thresholdListService.findByUniqueID(submittedThresholdList.getUniqueID()) != null){
            Set<ThresholdList> thresholdListSet =
                    thresholdListService.findAllByUniqueIDContainingIgnoreCase(submittedThresholdList.getUniqueID());
            Optional<ThresholdList> found = thresholdListSet.stream().filter(thresholdList -> thresholdList.getUploader().equals(currentTeacher)).findFirst();
            if (found.isPresent()){
                log.debug("Unique ID already in use");
                model.addAttribute("thresholdListFeedback", "Unique id " + submittedThresholdList.getUniqueID() + " already exists");
                model.addAttribute("thresholds", auxiliaryController.sortThresholdByUniqueID(thresholdService.findAll()));
                ThresholdList newList = ThresholdList.builder().thresholds(new HashSet<>()).uploader(currentTeacher).build();
                model.addAttribute("thresholdList", newList);
                return "/SRM/thresholdList/newThresholdList";
            }
        }

        //sync thresholds with this list and save
        Set<Threshold> thresholdSet = submittedThresholdList.getThresholds();
        thresholdSet.forEach(threshold -> {
            threshold.getThresholdLists().add(submittedThresholdList);
            thresholdService.save(threshold);
        });

        submittedThresholdList.setUploader(auxiliaryController.getCurrentTeacherUser());
        ThresholdList saved = thresholdListService.save(submittedThresholdList);

        //sync student tasks with this list and save
        Set<StudentTask> studentTasks = submittedThresholdList.getStudentTaskSet();
        studentTasks.forEach(studentTask -> {
            studentTask.getThresholdListSet().add(saved);
            studentTaskService.save(studentTask);
        });

        log.debug("Threshold-list saved");
        model.addAttribute("thresholdList", saved);
        model.addAttribute("thresholds", saved.getThresholds());
        model.addAttribute("thresholdListFeedback", "Threshold-list saved");

        model.addAttribute("teacher", currentTeacher);
        return "/SRM/thresholdList/viewThresholdList";
    }

    @TeacherRead
    @GetMapping("/{id}")
    public String viewThresholdList(Model model, @PathVariable("id") String thresholdListID){
        if (thresholdListService.findById(Long.valueOf(thresholdListID)) == null){
            log.debug("Threshold list not found");
            throw new NotFoundException("Threshold list not found");
        }

        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();
        ThresholdList found = thresholdListService.findById(Long.valueOf(thresholdListID));

        model.addAttribute("teacher", currentTeacher);
        model.addAttribute("thresholdList", found);
        model.addAttribute("thresholds", found.getThresholds());
        return "/SRM/thresholdList/viewThresholdList";
    }

    @TeacherUpdate
    @GetMapping("/{id}/edit")
    public String getUpdateThresholdList(Model model, @PathVariable("id") String thresholdListID){
        if (thresholdListService.findById(Long.valueOf(thresholdListID)) == null){
            log.debug("Threshold list not found");
            throw new NotFoundException("Threshold list not found");
        }

        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();
        ThresholdList found = thresholdListService.findById(Long.valueOf(thresholdListID));
        if (!found.getUploader().equals(currentTeacher)){
            log.debug("Teachers not permitted to edit other teachers' threshold lists");
            model.addAttribute("message", "You are not permitted to edit threshold lists of other teachers");
            return "/SRM/customMessage";
        }

        model.addAttribute("thresholdList", found);
        model.addAttribute("thresholds", found.getThresholds());
        model.addAttribute("studentTasks", found.getStudentTaskSet());
        model.addAttribute("currentTeacher", auxiliaryController.getCurrentTeacherUser());
        return "/SRM/thresholdList/updateThresholdList";
    }

    @TeacherUpdate
    @GetMapping("/{id}/edit/search")
    public String getUpdateThresholdList_search(Model model, @PathVariable("id") String thresholdListID, String thresholdUniqueId,
            String studentTaskTitle){
        if (thresholdListService.findById(Long.valueOf(thresholdListID)) == null){
            log.debug("Threshold list not found");
            throw new NotFoundException("Threshold list not found");
        }

        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();
        ThresholdList found = thresholdListService.findById(Long.valueOf(thresholdListID));
        if (!found.getUploader().equals(currentTeacher)){
            log.debug("Teachers not permitted to edit other teachers' threshold lists");
            model.addAttribute("message", "You are not permitted to edit threshold lists of other teachers");
            return "/SRM/customMessage";
        }

        Set<Threshold> thresholdSet;
        if (thresholdUniqueId != null || !thresholdUniqueId.isBlank()){
            thresholdSet = thresholdService.findAllByUniqueIDContainingIgnoreCase(thresholdUniqueId);
            //ensure stored thresholds are viewable too
            thresholdSet.addAll(found.getThresholds());
        } else {
            thresholdSet = thresholdService.findAll();
        }

        Set<StudentTask> studentTasks;
        if (studentTaskTitle != null || !studentTaskTitle.isBlank()){
            studentTasks = studentTaskService.findAllByTitleIgnoreCase(studentTaskTitle);
            //ensure stored thresholds are viewable too
            studentTasks.addAll(found.getStudentTaskSet());
        } else {
            studentTasks = studentTaskService.findAll();
        }

        model.addAttribute("studentTasks", studentTasks);
        model.addAttribute("thresholdList", found);
        model.addAttribute("thresholds", auxiliaryController.sortThresholdByUniqueID(thresholdSet));
        model.addAttribute("currentTeacher", auxiliaryController.getCurrentTeacherUser());
        return "/SRM/thresholdList/updateThresholdList";
    }

    @TeacherUpdate
    @PostMapping("/{id}/edit")
    public String postUpdateThresholdList(Model model, @PathVariable("id") String thresholdListID,
                                          @ModelAttribute("thresholdList") ThresholdList submittedThresholdList){
        if (thresholdListService.findById(Long.valueOf(thresholdListID)) == null){
            log.debug("Threshold list not found");
            throw new NotFoundException("Threshold list not found");
        }

        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();
        ThresholdList onFile = thresholdListService.findById(Long.valueOf(thresholdListID));
        if (!onFile.getUploader().equals(currentTeacher)){
            log.debug("Teachers not permitted to edit other teachers' threshold lists");
            model.addAttribute("message", "You are not permitted to edit threshold lists of other teachers");
            return "/SRM/customMessage";
        }

        if (!submittedThresholdList.getUniqueID().equals(onFile.getUniqueID())){
            if (thresholdListService.findByUniqueID(submittedThresholdList.getUniqueID()) != null){
                Set<ThresholdList> thresholdListSet =
                        thresholdListService.findAllByUniqueIDContainingIgnoreCase(submittedThresholdList.getUniqueID());
                Optional<ThresholdList> found = thresholdListSet.stream().filter(thresholdList -> thresholdList.getUploader().equals(currentTeacher)).findFirst();
                if (found.isPresent()){
                    log.debug("Unique ID already in use");
                    model.addAttribute("thresholdListFeedback", "Unique id " + submittedThresholdList.getUniqueID() + " already exists");
                    model.addAttribute("thresholds", auxiliaryController.sortThresholdByUniqueID(thresholdService.findAll()));
                    model.addAttribute("thresholdList", onFile);
                    return "/SRM/thresholdList/updateThresholdList";
                }
            }
        }
        //update Thresholds removed from onFile
        Set<Threshold> removed = new HashSet<>(onFile.getThresholds());
        removed.removeIf(submittedThresholdList.getThresholds()::contains);

        //remove records of removed Thresholds
        removed.forEach(threshold -> {
            threshold.getThresholdLists().remove(onFile);
            thresholdService.save(threshold);
        });

        //update all submittedThresholdList Thresholds
        submittedThresholdList.getThresholds().forEach(threshold -> {
            threshold.getThresholdLists().add(onFile);
            thresholdService.save(threshold);
        });

        //update Tasks removed from onFile
        Set<StudentTask> tasksRemoved = new HashSet<>(onFile.getStudentTaskSet());
        tasksRemoved.removeIf(submittedThresholdList.getStudentTaskSet()::contains);

        //remove records of removed Tasks
        tasksRemoved.forEach(studentTask -> {
            studentTask.getThresholdListSet().remove(onFile);
            studentTaskService.save(studentTask);
        });

        //update all submittedThresholdList Tasks
        submittedThresholdList.getStudentTaskSet().forEach(studentTask -> {
            studentTask.getThresholdListSet().add(onFile);
            studentTaskService.save(studentTask);
        });

        onFile.setThresholds(submittedThresholdList.getThresholds());
        onFile.setStudentTaskSet(submittedThresholdList.getStudentTaskSet());
        ThresholdList saved = thresholdListService.save(onFile);
        log.debug("Threshold-list updated");
        model.addAttribute("thresholdList", saved);
        model.addAttribute("thresholds", saved.getThresholds());
        model.addAttribute("thresholdListFeedback", "Threshold-list updated");

        model.addAttribute("teacher", currentTeacher);
        return "/SRM/thresholdList/viewThresholdList";
    }

    @TeacherUpdate
    @GetMapping("/{id}/delete")
    public String getDeleteThresholdList(@PathVariable("id") String thresholdListID){
        if (thresholdListService.findById(Long.valueOf(thresholdListID)) == null){
            log.debug("Threshold list not found");
            throw new NotFoundException("Threshold list not found");
        }

        TeacherUser currentTeacher = auxiliaryController.getCurrentTeacherUser();
        ThresholdList found = thresholdListService.findById(Long.valueOf(thresholdListID));
        if (!found.getUploader().equals(currentTeacher)){
            throw new ForbiddenException("Permission denied");
        }

        // update the student tasks
        Set<StudentTask> studentTasks = found.getStudentTaskSet();
        // each task can be assigned to multiple (different) threshold lists
        studentTasks.forEach(studentTask -> {
            studentTask.getThresholdListSet().stream()
                    .filter(thresholdList ->
                        thresholdList.equals(found)
                    ).forEach(list -> {
                        studentTask.getThresholdListSet().remove(list);
                        studentTaskService.save(studentTask);
            });
        });

        Set<Threshold> thresholds = found.getThresholds();
        // each threshold references this list only once
        thresholds.forEach(threshold -> {
                    threshold.getThresholdLists().remove(found);
                    thresholdService.save(threshold);
                });

        thresholdListService.delete(found);
        log.info("Deleted threshold list with ID: " + thresholdListID);

        return "redirect:/thresholdLists/index";
    }
}
