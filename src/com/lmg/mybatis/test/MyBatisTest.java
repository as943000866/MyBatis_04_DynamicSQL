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
 * 1. 接口式编程
 * 原生: 			Dao 	===> DaoImpl
 * mybatis: 	Mapper 	===> xxMapper.xml
 * 
 * 2. SqlSession 代表和数据库的一次会话, 用完必须关闭.
 * 3. SqlSession 和 Connection 一样都是非线程安全的.
 * 4. mapper 接口没有实现类, 但是 mybatis 会为这个接口生成一个代理对象.
 * 		(将接口 和 xml 进行绑定)
 * 		EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
 * 5. 两个重要的配置文件:
 * 		mybatis 的全局配置文件: 包含数据库连接池信息, 事务管理器信息等... 系统运行环境信息
 * 		sql映射文件:保存了每一个 sql 语句的映射信息:
 * 					将 sql 抽取出来
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
			
			//测试if\where
			Employee employee = new Employee(2,"TOM",null,null);
			/*List<Employee> list = mapper.getEmpsByConditionIf(employee);
			for (Employee emp : list) {
				System.out.println(emp);
			}*/
			
			//查询的时候如果某些条件没带可能 sql 拼装有问题
			//1.给 where 后面加上 1=1, 以后的条件都 and xxx
			//2.mybatis 使用 where 标签来将所有的查询条件包括在内. mybatis 就会将 where 标签中拼装的 sql, 多出来的 and 或者 or去掉
				//where 只会去掉第一个多出来的 and 或者 or.
			//测试 Trim
			/*List<Employee> list = mapper.getEmpsByConditionTrim(employee);
			for (Employee emp : list) {
				System.out.println(emp);
			}*/
			//测试choose
			/*List<Employee> list = mapper.getEmpsByConditionChoose(employee);
			for (Employee emp : list) {
				System.out.println(emp);
			}*/
			//测试set标签
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
	 * 1. 根据xml配置文件 (全局配置文件) 创建一个 SqlSessionFactory 对象
	 * 		有数据源一些运行环境信息
	 * 2. sql 映射文件:配置了每一个 sql, 以及 sql 的封装规则等。
	 * 3. 将 sql 映射文件注册在全局配置文件中
	 * 4. 写代码:
	 * 		 1). 根据全局配置文件得到 SqlSessionFactory
	 * 		 2). 使用 sqlSession 工厂, 获取 sqlSession 对象使用他来执行增删改查
	 * 			 一个 sqlSession 就是代表和数据库的一次会话, 用完关闭
	 * 		 3). 使用 sql 的唯一标识来告诉 MyBatis 执行哪个 sql。sql 都是保存在 sql 映射文件中的。
	 * @throws IOException
	 */
	@Test
	public void test() throws IOException{
		String resource = "mybatis-conf.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		
		//2.获取sqlSession实例,能直接执行已经映射的sql语句
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
		//1. 获取sqlSessionFactory对象
		SqlSessionFactory sessionFactory = getSqlSessionFactory();
		
		//2. 获取sqlSession对象
		SqlSession session = sessionFactory.openSession();
		
		try {
			
			//3.获取接口的实现类对象
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
	 * 测试 增删改
	 * 1.mybatis允许增删改直接定义以下类型返回值
	 * 		Integer,Long,Boolean,void
	 * 2.我需要手动提交
	 * 		sqlSessionFactory.openSession();==>手动提交
	 * 		sqlSessionFactory.openSession(true);==>自动提交
	 * @throws IOException
	 */
	@Test
	public void test03() throws IOException{
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		//1.获取到的SqlSession不会自动提交数据
		SqlSession openSession = sqlSessionFactory.openSession();
		try{
			EmployeeMapper mapper = openSession.getMapper(EmployeeMapper.class);
			//测试添加
			Employee employee = new Employee(null,"jerry","jerry@atguigu.com","1");
			mapper.addEmp(employee);
			System.out.println(employee.getId());
			//测试修改
			//Employee employee = new Employee(2,"Tom","jerry@atguigu.com","0");
			//Boolean updateEmp = mapper.updateEmp(employee);
			//System.out.println(updateEmp);
			//测试删除
			//mapper.deleteEmpById(1);
			//手动提交
			openSession.commit();
		}finally{
			openSession.close();
		}
	}
	
	@Test
	public void test04() throws IOException{
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		//1.获取到的SqlSession不会自动提交数据
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
			//手动提交
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
