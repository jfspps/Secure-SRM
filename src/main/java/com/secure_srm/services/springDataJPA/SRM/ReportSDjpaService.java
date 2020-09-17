package com.secure_srm.services.springDataJPA.SRM;

import com.secure_srm.model.academic.Report;
import com.secure_srm.repositories.academicRepos.ReportRepository;
import com.secure_srm.services.academicServices.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Profile("SDjpa")
public class ReportSDjpaService implements ReportService {

    private final ReportRepository reportRepository;

    public ReportSDjpaService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Report findByStudentLastName(String lastName) {
        return reportRepository.findByStudent_LastName(lastName).orElse(null);
    }

    @Override
    public Report findByStudentFirstAndLastName(String firstName, String lastName) {
        return reportRepository.findByStudent_FirstNameAndStudent_LastName(firstName, lastName).orElse(null);
    }

    @Override
    public Report findByTeacherLastName(String lastName) {
        return reportRepository.findByTeacher_LastName(lastName).orElse(null);
    }

    @Override
    public Report findByTeacherFirstAndLastName(String firstName, String lastName) {
        return reportRepository.findByTeacher_FirstNameAndTeacher_LastName(firstName, lastName).orElse(null);
    }

    @Override
    public Report findBySubject(String subjectName) {
        return reportRepository.findBySubject_SubjectName(subjectName).orElse(null);
    }

    @Override
    public Report save(Report object) {
        return reportRepository.save(object);
    }

    @Override
    public Report findById(Long aLong) {
        return reportRepository.findById(aLong).orElse(null);
    }

    @Override
    public Set<Report> findAll() {
        Set<Report> reports = new HashSet<>();
        reports.addAll(reportRepository.findAll());
        return reports;
    }

    @Override
    public void delete(Report objectT) {
        reportRepository.delete(objectT);
    }

    @Override
    public void deleteById(Long aLong) {
        reportRepository.deleteById(aLong);
    }
}
