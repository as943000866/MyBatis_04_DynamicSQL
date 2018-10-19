package com.lmg.mybatis.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lmg.mybatis.bean.Employee;

public interface EmployeeMapperDynamicSQL {
	
	public List<Employee> getEmpsTestInnerParameter(Employee employee);
	
	public List<Employee> getEmpsByConditionIf(Employee employee);
	
	public List<Employee> getEmpsByConditionTrim(Employee employee);
	
	public List<Employee> getEmpsByConditionChoose(Employee employee);
	
	public void updateEmp(Employee employee);
	
	public List<Employee> getEmpsByConditionForeach(@Param("ids")List<Integer> ids);
	
	public void addEmps(@Param("emps")List<Employee> emps);
}
