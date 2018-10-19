package com.lmg.mybatis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lmg.mybatis.bean.Employee;

public interface EmployeeMapperPlus {
	
	public Employee getEmpById(Integer id);
	
	public Employee getEmpAndDept(Integer id);
	
	public Employee getEmpByIdStep(Integer id);
	
	public List<Employee> getEmpsByDeptId(Integer id);
	
}
