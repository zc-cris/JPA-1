package com.zc.cris.helloworld;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestJPA {

	String persistenceUnitName = null;
	EntityManagerFactory entityManagerFactory = null;
	EntityManager entityManager = null;
	EntityTransaction transaction = null;

	@BeforeEach
	void init() {
		persistenceUnitName = "JPA-1";
		entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
		entityManager = entityManagerFactory.createEntityManager();
		transaction = entityManager.getTransaction();
		transaction.begin();
	}

	@AfterEach
	void destory() {
		transaction.commit();
		entityManager.close();
		entityManagerFactory.close();
	}
	
	
	
	/*
	 * NativeQuery 适用于本地 sql
	 */
	@Test
	void testNativeQuery() {
		String sql = "select age from jap_customers where id = ?";
		Object singleResult = entityManager.createNativeQuery(sql).setParameter(1, 1).getSingleResult();
		System.out.println(singleResult);
	}
	
	
	
	/*
	 * 测试预定义的 namedQuery 语句(在实体类前面使用 @NamedQuery 标记的查询语句)
	 */
	@Test
	void testNamedQuery() {
		Customer customer = (Customer) entityManager.createNamedQuery("testNamedQuery").setParameter("age", 16)
				.getSingleResult();
		System.out.println(customer);
	}

	/*
	 * 查询部分数据，默认情况下返回的是 Object[] 类型的结果，或者是 Object[] 类型的 List 通过在实体类中构造对应的构造器，然后在
	 * jpql 语句中利用对应的构造器返回实体类的对象
	 */
	@Test
	void testPartlyProperties() {
		String jpql = "select new Customer(c.name, c.age) from Customer c where c.age > :age";
		Query query = entityManager.createQuery(jpql);
		query.setParameter("age", 18);
		List<Customer> resultList = query.getResultList();
		System.out.println(resultList);
	}

	/*
	 * 使用 jpql 获取满足条件的对象集合数据
	 */
	@Test
	void testJPQL() {
		String jpql = "from Customer c where c.age > :age";
		Query query = entityManager.createQuery(jpql);
		query.setParameter("age", 18);
		List<Customer> resultList = query.getResultList();
		System.out.println(resultList.size());
	}

	@Test
	void test() {

		// 1. 创建 EntitymanagerFactory
		String persistenceUnitName = "JPA-1";
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);

		// 2. 创建 EntitymanagerFactory
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		// 3. 开启事务
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		// 4. 进行持久化操作
		Customer customer = new Customer();
		customer.setAge(20);
		customer.setEmail("fwfw@qq.com");
		customer.setName("水菜丽");
		customer.setBirth(new Date());
		customer.setCreateTime(new Date());
		entityManager.persist(customer);

		// 5. 提交事务
		transaction.commit();

		// 6. 关闭 EntityManager
		entityManager.close();

		// 7. 关闭 EntityManagerFactory
		entityManagerFactory.close();

	}
}
