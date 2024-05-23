package com.pvt.groupOne.repository;

import org.springframework.data.repository.CrudRepository;

import com.pvt.groupOne.model.BugReport;

public interface BugReportRepository extends CrudRepository<BugReport, Integer> {
    BugReport findById(int id);
}
