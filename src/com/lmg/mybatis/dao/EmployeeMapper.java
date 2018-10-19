package com.lmg.mybatis.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import com.lmg.mybatis.bean.Employee;

public interface EmployeeMapper {
	
	//多条记录封装一个map: Map<Integer,Employee>:键是这条记录的主键,值是封装后的javaBean
	//告诉mybatis封装这个 map 的时候使用哪个属性作为 map 的 key
	@MapKey("lastName")
	public Map<Integer,Employee> getEmpByLastNameLikeReturnMap(String lastName);
	
	//返回一条记录的map,key就是列名,值就是对应的值
	public Map<String,Object> getEmpsByIdReturnMap(Integer id);
	
	public List<Employee> getEmpsByLastNameLike(String lastName);
	
	public Employee getEmpByMap(Map<String, Object> map);
	
	public Employee getEmpByIdAndLastName(@Param("id")Integer id,@Param("lastName")String lastName);
	
	public Employee getEmpById(Integer id);
	
	public Long addEmp(Employee employee);
	
	public Boolean updateEmp(Employee employee);
	
	public void deleteEmpById(Integer id);
	
}
