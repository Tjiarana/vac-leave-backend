package com.leave_backend.leave.RowMapper;

import com.leave_backend.leave.models.Employee;
import com.leave_backend.leave.models.Gender;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EmployeeRowMapper implements RowMapper<Employee> {
    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getLong("employee_id"));
        employee.setEmployeeFirstname(rs.getString("employee_firstname"));
        employee.setEmployeeLastname(rs.getString("employee_lastname"));
        employee.setEmployeeGender(Gender.valueOf(rs.getString("employee_gender")));
        employee.setEmployeeEmail(rs.getString("employee_email"));
        employee.setEmployeePhone(rs.getString("employee_phone"));
        employee.setReportTo(rs.getObject("report_to", Long.class));
        employee.setPositionId(rs.getInt("position_id"));
        return employee;
    }
}
