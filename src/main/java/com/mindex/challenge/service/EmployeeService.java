package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import java.math.BigDecimal;
import java.util.Date;


public interface EmployeeService {
    Employee create(Employee employee);
    Employee read(String id);
    Employee update(Employee employee);
    ReportingStructure getReportStructure(String id);
    Compensation createCompensation(Compensation compensation);
    Compensation getCompensation(String id);



}
