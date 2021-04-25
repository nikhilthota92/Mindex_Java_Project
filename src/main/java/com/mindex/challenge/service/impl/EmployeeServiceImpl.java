package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);
        LOG.debug("employee with id [{}]",employee );


        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    /*
    * Input : employeeId
    * Output : ReportStructure Object
    * */

    @Override
    public ReportingStructure getReportStructure(String employeeId) {
      Employee employee=  employeeRepository.findByEmployeeId(employeeId);

      List<Employee> allSubDirectReportsList= new ArrayList<>();

      //get the list of all subDirectReports and their subDirectReports and stored in allSubDirectReportsList
      getDirectReportsForEmployee(employee.getDirectReports(),allSubDirectReportsList);

      //Finding the total number of different employee in entire subDirectReportsList
      Long numberOfReports= allSubDirectReportsList.stream()
                                                   .distinct()
                                                   .count();

      ReportingStructure reportingStructure=new ReportingStructure();
      reportingStructure.setEmployee(employee);
      reportingStructure.setNumberOfReports(numberOfReports.intValue());

      return reportingStructure;
    }

    //Recursive method to find all the subDirectReports and their corresponding subDirectReports
    private static void getDirectReportsForEmployee(List<Employee> employeeList, List<Employee> finalSubList) {
        for(Employee employee:employeeList){
            finalSubList.add(employee);
            if(employee.getDirectReports()!=null) {
                for(Employee employee1:employee.getDirectReports()){
                    finalSubList.add(employee1);
                    if(employee1.getDirectReports()!=null) getDirectReportsForEmployee(employee1.getDirectReports(),finalSubList);
                }
            }
        }
    }

    @Override
    public  Compensation createCompensation(Compensation compensation) {
        Compensation compensationSavedData = compensationRepository.save(compensation);
        return compensationSavedData;
    }

    @Override
    public Compensation getCompensation(String id) {
        Employee employee=employeeRepository.findByEmployeeId(id);
        Query query=new Query();
        query.addCriteria(Criteria.where("employee.employeeId").is(id));
       return  mongoTemplate.findOne(query,Compensation.class);

    }




}
