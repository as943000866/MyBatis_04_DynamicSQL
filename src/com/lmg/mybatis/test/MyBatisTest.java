package com.lmg.mybatis.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import com.lmg.mybatis.bean.Department;
import com.lmg.mybatis.bean.Employee;
import com.lmg.mybatis.dao.DepartmentMapper;
import com.lmg.mybatis.dao.EmployeeMapper;
import com.lmg.mybatis.dao.EmployeeMapperAnnotation;
import com.lmg.mybatis.dao.EmployeeMapperDynamicSQL;
import com.lmg.mybatis.dao.EmployeeMapperPlus;

/**
 * 1. �ӿ�ʽ���
 * ԭ��: 			Dao 	===> DaoImpl
 * mybatis: 	Mapper 	===> xxMapper.xml
 * 
 * 2. SqlSession ��������ݿ��һ�λỰ, �������ر�.
 * 3. SqlSession �� Connection һ�����Ƿ��̰߳�ȫ��.
 * 4. mapper �ӿ�û��ʵ����, ���� mybatis ��Ϊ����ӿ�����һ���������.
 * 		(���ӿ� �� xml ���а�)
 * 		EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
 * 5. ������Ҫ�������ļ�:
 * 		mybatis ��ȫ�������ļ�: �������ݿ����ӳ���Ϣ, �����������Ϣ��... ϵͳ���л�����Ϣ
 * 		sqlӳ���ļ�:������ÿһ�� sql ����ӳ����Ϣ:
 * 					�� sql ��ȡ����
 *   
 * @author Administrator
 *
 */
public class MyBatisTest {
	
	private SqlSessionFactory getSqlSessionFactory() throws IOException {
		String resource = "mybatis-conf.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		return sqlSessionFactory;
	}
	
	@Test
	public void testInnerParam() throws IOException{
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession openSession = sqlSessionFactory.openSession();
		try {
			EmployeeMapperDynamicSQL mapper = openSession.getMapper(EmployeeMapperDynamicSQL.class);
			List<Employee> list = mapper.getEmpsTestInnerParameter(new Employee(null,"e",null,null));
			
			for (Employee emp : list) {
				System.out.println(emp);
			}
		} finally {
			openSession.close();
		}
	}
	
	@Test
	public void testBatchSave() throws IOException{
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession openSession = sqlSessionFactory.openSession();
		try {
			EmployeeMapperDynamicSQL mapper = openSession.getMapper(EmployeeMapperDynamicSQL.class);
			List<Employee> emps = new ArrayList<>();
			emps.add(new Employee(null,"smith","smith@atguigu.com","1",new Department(1)));
			emps.add(new Employee(null,"allen","allen@atguigu.com","1",new Department(1)));
			mapper.addEmps(emps);
			openSession.commit();
		} finally {
			openSession.close();
		}
	}
	@Test
	public void testDynamicSql() throws IOException{
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession openSession = sqlSessionFactory.openSession();
		try {
			EmployeeMapperDynamicSQL mapper = openSession.getMapper(EmployeeMapperDynamicSQL.class);
			
			//����if\where
			Employee employee = new Employee(2,"TOM",null,null);
			/*List<Employee> list = mapper.getEmpsByConditionIf(employee);
			for (Employee emp : list) {
				System.out.println(emp);
			}*/
			
			//��ѯ��ʱ�����ĳЩ����û������ sql ƴװ������
			//1.�� where ������� 1=1, �Ժ�������� and xxx
			//2.mybatis ʹ�� where ��ǩ�������еĲ�ѯ������������. mybatis �ͻὫ where ��ǩ��ƴװ�� sql, ������� and ���� orȥ��
				//where ֻ��ȥ����һ��������� and ���� or.
			//���� Trim
			/*List<Employee> list = mapper.getEmpsByConditionTrim(employee);
			for (Employee emp : list) {
				System.out.println(emp);
			}*/
			//����choose
			/*List<Employee> list = mapper.getEmpsByConditionChoose(employee);
			for (Employee emp : list) {
				System.out.println(emp);
			}*/
			//����set��ǩ
			/*mapper.updateEmp(employee);
			openSession.commit();*/
			
			List<Employee> list = mapper.getEmpsByConditionForeach(Arrays.asList(1,2,3));
			for (Employee emp : list) {
				System.out.println(emp);
			}
		} finally {
			openSession.close();
		}
		
	}
	/**
	 * 1. ����xml�����ļ� (ȫ�������ļ�) ����һ�� SqlSessionFactory ����
	 * 		������ԴһЩ���л�����Ϣ
	 * 2. sql ӳ���ļ�:������ÿһ�� sql, �Լ� sql �ķ�װ����ȡ�
	 * 3. �� sql ӳ���ļ�ע����ȫ�������ļ���
	 * 4. д����:
	 * 		 1). ����ȫ�������ļ��õ� SqlSessionFactory
	 * 		 2). ʹ�� sqlSession ����, ��ȡ sqlSession ����ʹ������ִ����ɾ�Ĳ�
	 * 			 һ�� sqlSession ���Ǵ�������ݿ��һ�λỰ, ����ر�
	 * 		 3). ʹ�� sql ��Ψһ��ʶ������ MyBatis ִ���ĸ� sql��sql ���Ǳ����� sql ӳ���ļ��еġ�
	 * @throws IOException
	 */
	@Test
	public void test() throws IOException{
		String resource = "mybatis-conf.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		
		//2.��ȡsqlSessionʵ��,��ֱ��ִ���Ѿ�ӳ���sql���
		//statement Unique identifier matching the statement to use.
		//parameter A parameter object to pass to the statement.
		
		SqlSession session = sqlSessionFactory.openSession();
		try {
			Employee employee = session.selectOne("com.lmg.mybatis.EmployeeMapper.selectEmp", 1);
			System.out.println(employee);
		} finally {
			session.close();
			
		}
		
	}
	
	@Test
	public void test01() throws IOException{
		//1. ��ȡsqlSessionFactory����
		SqlSessionFactory sessionFactory = getSqlSessionFactory();
		
		//2. ��ȡsqlSession����
		SqlSession session = sessionFactory.openSession();
		
		try {
			
			//3.��ȡ�ӿڵ�ʵ�������
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			
			Employee employee = mapper.getEmpById(1);
			System.out.println(mapper.getClass());
			System.out.println(employee);
		} finally {
			
			session.close();
		}
		
	}
	
	@Test
	public void test02() throws IOException{
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession openSession = sqlSessionFactory.openSession();
		
		try {
			EmployeeMapperAnnotation mapper = openSession.getMapper(EmployeeMapperAnnotation.class);
			Employee empById = mapper.getEmpById(1);
			System.out.println(empById);
			
		}finally{
			openSession.close();
		}
	}
	/**
	 * ���� ��ɾ��
	 * 1.mybatis������ɾ��ֱ�Ӷ����������ͷ���ֵ
	 * 		Integer,Long,Boolean,void
	 * 2.����Ҫ�ֶ��ύ
	 * 		sqlSessionFactory.openSession();==>�ֶ��ύ
	 * 		sqlSessionFactory.openSession(true);==>�Զ��ύ
	 * @throws IOException
	 */
	@Test
	public void test03() throws IOException{
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		//1.��ȡ����SqlSession�����Զ��ύ����
		SqlSession openSession = sqlSessionFactory.openSession();
		try{
			EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
			//�������
			Employee employee = new Employee(null,"jerry","jerry@atguigu.com","1");
			mapper.addEmp(employee);
			System.out.println(employee.getId());
			//�����޸�
			//Employee employee = new Employee(2,"Tom","jerry@atguigu.com","0");
			//Boolean updateEmp = mapper.updateEmp(employee);
			//System.out.println(updateEmp);
			//����ɾ��
			//mapper.deleteEmpById(1);
			//�ֶ��ύ
			openSession.commit();
		}finally{
			openSession.close();
		}
	}
	
	@Test
	public void test04() throws IOException{
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		//1.��ȡ����SqlSession�����Զ��ύ����
		SqlSession openSession = sqlSessionFactory.openSession();
		try{
			EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
			//Employee employee = mapper.getEmpByIdAndLastName(2, "Tom");
//			Map<String, Object> map = new HashMap<>();
//			map.put("id", "2");
//			map.put("lastName", "Tom");
//			map.put("tableName", "tbl_employee");
//			Employee employee = mapper.getEmpByMap(map);
//			System.out.println(employee);
			
//			List<Employee> list = mapper.getEmpsByLastNameLike("%T%");
//			for (Employee employee : list) {
//				System.out.println(employee);
//			}
			
//			Map<String, Object> map = mapper.getEmpsByIdReturnMap(2);
//			System.out.println(map);
			
			Map<Integer, Employee> map = mapper.getEmpByLastNameLikeReturnMap("%T%");
			System.out.println(map);
			//�ֶ��ύ
			openSession.commit();
		}finally{
			openSession.close();
		}
	}
	
	@Test
	public void test05() throws IOException{
		
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession openSession = sqlSessionFactory.openSession();
		
		try{
			EmployeeMapperPlus mapper = openSession.getMapper(EmployeeMapperPlus.class);
			/*Employee employee = mapper.getEmpById(2);
			System.out.println(employee);*/
			/*Employee employee = mapper.getEmpAndDept(2);
			Department dept = employee.getDept();
			System.out.println(employee);
			System.out.println(dept);*/
			Employee employee = mapper.getEmpByIdStep(3);
			System.out.println(employee);
			Department dept = employee.getDept();
			System.out.println(dept);
			
		}finally{
			openSession.close();
		}
	}
	@Test
	public void test06() throws IOException{
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSession openSession = sqlSessionFactory.openSession();
		
		try {
			DepartmentMapper mapper = openSession.getMapper(DepartmentMapper.class);
			/*Department department = mapper.getDeptByIdPlus(1);
			System.out.println(department);
			System.out.println(department.getEmps());*/
			Department department = mapper.getDeptByIdStep(2);
			System.out.println(department.getDepartmentName());
			System.out.println(department.getEmps());
		} finally {
			openSession.close();
		}
	}
}
