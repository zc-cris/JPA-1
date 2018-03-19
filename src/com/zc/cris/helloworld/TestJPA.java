package com.zc.cris.helloworld;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hibernate.jpa.QueryHints;
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
	 * 测试使用 jpql 语句进行数据的更新（删除同理）
	 */
	@Test
	void testJPQLUpdate() {
		String jpql = "update Customer c set c.name = :name where c.id = :id";
		entityManager.createQuery(jpql).setParameter("name", "新垣结衣").setParameter("id", 2).executeUpdate();
	}
	
	
	
	/*
	 * 使用 jpql 的内置函数(将查询结果全部大写)
	 */
	@Test
	void testJpqlFunction() {
		String jqpl = "select upper(c.email) from Customer c";
		List<String> list = entityManager.createQuery(jqpl).getResultList();
		System.out.println(list);
		
	}
	
	
	/*
	 * 查询客户名字为 林允儿的orders（使用子查询）
	 */
	@Test
	void testSubSelect() {
		String jpql = "select o from Order o where o.customer = (select c from Customer c where c.name = :name)";
		Query query = entityManager.createQuery(jpql).setParameter("name", "林允儿");
		List<Order> resultList = query.getResultList();
		System.out.println(resultList);
	}
	
	
	
	
	
	
	/*
	 * jpql 的关联查询同 hql
	 *	查询语句中的 fetch 不能省，否则返回的是 Object类型的数据（至于为什么，可以把去掉fetch 的sql语句放到数据库执行一下就懂了）
	 */
	@Test
	void testLeftOuterJoinFetch() {
		String jpql = "select c from Customer c left outer join fetch c.orders where c.id = :id";
		Query query = entityManager.createQuery(jpql).setParameter("id", 2);
		
		// 返回的是 一个customer的实例，并且orders都已经初始化好了
		Customer customer = (Customer) query.getSingleResult();
		System.out.println(customer.getName());
		System.out.println(customer.getOrders().size());
		
	}
	
	
	
	/*
	 * 测试分组函数：查询哪些customer 的order 数量大于1的customer
	 */
	@Test
	void testGroupBy() {
		
		String jpql = "select o.customer from Order o group by o.customer having count(o.id) >1";
		Query query = entityManager.createQuery(jpql);
		List resultList = query.getResultList();
		System.out.println(resultList);
		
	}
	
	
	/*
	 * 测试排序函数
	 */
	@Test
	void testOrderBy() {
		
		String jpql = "from Customer c where c.age > :age order by c.age desc";
		// 显性启动查询缓存
		Query query = entityManager.createQuery(jpql);
		query.setParameter("age", 18);
		List<Customer> resultList = query.getResultList();
		System.out.println(resultList.size());
	}
	
	
	/*
	 * 使用hibernate的查询缓存（两个配置）
	 */
	@Test
	void testQueryCache() {
		String jpql = "from Customer c where c.age > :age";
		// 显性启动查询缓存
		Query query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);
		query.setParameter("age", 18);
		List<Customer> resultList = query.getResultList();
		System.out.println(resultList.size());
		
		// 显性启动查询缓存
		query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);
		query.setParameter("age", 18);
		resultList = query.getResultList();
		System.out.println(resultList.size());
		
	}
	
	
	@Test
	void testSecondLevelCache() {
		Customer customer1 = entityManager.find(Customer.class, 2);
		
		transaction.commit();
		entityManager.close();
		
		entityManager = entityManagerFactory.createEntityManager();
		transaction = entityManager.getTransaction();
		transaction.begin();
		
		
		Customer customer2 = entityManager.find(Customer.class, 2);
	}
	
	
	
	/*
	 * 查询不维护关联关系的一方的时候，还是默认采用懒加载机制
	 */
	@Test
	void testManyToManyFind2() {
		Course course = entityManager.find(Course.class, 1);
		System.out.println(course.getName());
		
		System.out.println(course.getStudents().size());
	}
	
	
	/*
	 * 查询维护关联关系的一方的时候，默认采用懒加载机制
	 */
	@Test
	void testManyToManyFind() {
		Student student = entityManager.find(Student.class, 1);
		System.out.println(student.getName());
		
		System.out.println(student.getCourses().size());
		
	}
	
	

	/*
	 * 多对多的保存
	 */
	@Test
	void testManyToManyPersistence() {
		Student student = new Student();
		student.setName("cris");
		
		Student student2 = new Student();
		student2.setName("詹姆斯");
		
		Course course = new Course();
		course.setName("语文");
		
		Course course2 = new Course();
		course2.setName("english");
		
		// 设置关联关系
		student.getCourses().add(course);
		student.getCourses().add(course2);
		
		student2.getCourses().add(course);
		
		course.getStudents().add(student);
		course.getStudents().add(student2);
		
		course2.getStudents().add(student);
		
		// 执行保存操作
		entityManager.persist(student);
		entityManager.persist(student2);
		entityManager.persist(course);
		entityManager.persist(course2);
		
	}
	
	
	
	
	
	/*
	 * 默认情况下
	 * 1.若获取不维护关联关系的一方（没有外键），也会通过左外连接获取其关联的对象
	 * 	依然可以通过 @OneToOne 的 fetch 属性来改变加载策略（强烈不建议），但是会额外发送sql语句初始化关联对象
	 * 	说明在不维护关联关系的一方，不建议修改 fetch 属性，因为修改了也没有用，所以不修改就使用左外连接获取关联对象
	 * 	如果修改为lazy，那么会额外发送一条update语句获取关联对象，所以相比查询两次，还是建议使用左外连接，即不做任何修改
	 * 
	 * 2. 为什么 JPA 要在查询没有外键一方的时候强行将另外一方查询出来？
	 * 		- 对于有外键一方的数据表而言，我们可以通过外键得知对应的另外一端的情况（存在就返回对应的代理对象，不存在就返回null）
	 * 		- 但是对于没有外键一方的数据表来说，它自己是不知道到底和哪张数据表是有关联关系的，如何返回 代理？如何返回null？所以 JPA 规范干脆
	 * 		- 规定 查询没有外键的一方的时候直接将对应的另外一方查询出来，也许是左外连接，也许是额外发送一条select 语句，只要保证 一一对应的关系
	 * 		- 能够体现出来就行
	 * 
	 */ 
	@Test
	void testOneToOneFind2() {
		Manager mgr = entityManager.find(Manager.class, 1);
		System.out.println(mgr.getName());
		System.out.println(mgr.getDepartment().getClass().getName());
		
	}
	
	/*
	 * 默认情况下
	 * 1.若获取维护关联关系的一方（有外键），会通过左外连接获取其关联的对象
	 * 	但是可以通过 @OneToOne 的 fetch 属性来改变加载策略，即修改为懒加载，返回就是一个代理对象
	 */
	@Test
	public void testOneToOneFind() {
		Department dept = entityManager.find(Department.class, 1);
		System.out.println(dept.getName());
		System.out.println(dept.getMgr().getClass().getName());
		
	}
	
	
	
	// 双向 1-1 ，保存数据一定要先保存没有外键的一方，再保存有外键（维护关联关系）的一方，否则会多出update语句
	@Test
	void testOneToOne() {
		Manager manager = new Manager();
		manager.setName("zc-cris");
		
		Department department = new Department();
		department.setName("管理部");
		
		// 设置关联关系
		manager.setDepartment(department);
		department.setMgr(manager);

		entityManager.persist(manager);
		entityManager.persist(department);
	}
	
	
	
	
	
	
	
	
	
	// 通过1的一端去主动修改n的一端的数据
	@Test
	void testOneToManyUpdate() {
		Customer customer = entityManager.find(Customer.class, 2);
		// 修改 n的一端的第一条关联记录
		customer.getOrders().iterator().next().setName("洗发水");
	}
	
	
	// 默认情况下，若删除1的一端，则先把关联的n的一端的外键置为空，然后删除1的一端
	// 可以通过修改 @OneToMany 的cascade 属性来修改默认的删除策略(删除一之前将多的外键置为空)
	@Test
	void testOneToManyRemove() {
		Customer customer = entityManager.find(Customer.class, 18);
		entityManager.remove(customer);
		
	}
	
	// 单向一对多的时候，默认使用懒加载，即查询1的一端不会使用迫切做外连接查询 n 的一端
	// 可以使用 @OneToMany 的fetch 属性来修改默认的懒加载策略
	@Test
	void testOneToManyFind() {
		Customer customer = entityManager.find(Customer.class, 18);
		System.out.println(customer.getName());
		
		System.out.println(customer.getOrders().size());
	}
	
	
	// 双向 1-n 的关联关系，执行保存操作的时候
	// 如果先保存 n 的一端，将会多出 2n 条update 语句（外键在n的一端，n和1都维护关联关系）
	// 如果先保存1 的一端，将会多出 n条update 语句（1还要维护关联关系）
	// 所以在进行双向的1-n 关联关系的时候，建议使用n的一方来维护关联关系，而1的一端不需要维护关联关系，这样会有效减少 无谓的sql语句
	// 如果进行保存操作，还是先保存1，再保存多（如果设置级联保存的化，只需要保存1即可，但是不建议使用级联）
	// 注意：如果在1的一端 的 @OneToMany 中使用 mappedBy 属性，则 @OneToMany 端就一定不要再使用 @JoinColumn 注解了
	
	
	// 单向一对多的时候，执行保存一定会多出update语句，因为关联关系是由1的一方来进行维护的（这样很不好）
	// 所以 n的一端插入数据表的时候是不会插入外键列的，只有等到1的一端插入的时候再执行update 操作
	@Test
	void testOneToMany() {
		Customer customer = new Customer("cris", 22);
		customer.setBirth(new Date());
		customer.setCreateTime(new Date());
		customer.setEmail("aiwer@we.com");
		
		Order order1 = new Order();
		order1.setName("耐克");
		
		Order order2 = new Order();
		order2.setName("阿迪达斯");
		
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		entityManager.persist(customer);
		entityManager.persist(order1);
		entityManager.persist(order2);
	}
	
	
	/*
	 * 自动根据查询出来的对象的修改来进行数据库的update更新操作，其原理就是时间戳的比较
	 */
	@Test
	void testUpdate() {
		Customer customer = entityManager.find(Customer.class, 3);
		customer.setName("cris");
	}
	
	/*
	 * 可以直接通过查询出来的多的一端修改1的一端的数据，JPA 会根据修改自动同步到数据库
	 */
	
	@Test
	void testManyToOneUpdate() {
		Order order = entityManager.find(Order.class, 5);
		order.getCustomer().setName("明日花绮罗");
	}
	
	
	//不能直接删除1的一端，因为多的一端的外键引用1的一端的主键
	//只能直接删除多的一端
	@Test
	void testManyToOneRemove() {
//		Order order = entityManager.find(Order.class, 5);
//		entityManager.remove(order);
		
		Customer customer = entityManager.find(Customer.class, 12);
		entityManager.remove(customer);
	}
	
	
	// 默认情况下，使用迫切左外连接的方式获取n的数据（同时获取对应的1的数据）
	// 可以使用 @ManyToOne 的fetch 属性来修改默认的关联属性的加载策略(即手动设置懒加载)
	@Test
	void testManyToOneFind() {
		Order order = entityManager.find(Order.class, 5);
		
		System.out.println(order.getName());
		
		System.out.println(order.getCustomer().getName());
	}
	

	/*
	 * 保存单向多对1的时候，尽量先保存一，再保存多，避免无谓的update操作
	 */
	@Test
	void testManyToOne() {
		Customer customer = new Customer("艾琳娜", 23);
		customer.setBirth(new Date());
		customer.setCreateTime(new Date());
		customer.setEmail("ailinna@we.com");
		
		Order order1 = new Order();
		order1.setName("衣服");
		order1.setCustomer(customer);
		
		Order order2 = new Order();
		order2.setName("裤子");
		order2.setCustomer(customer);
		
		entityManager.persist(customer);
		entityManager.persist(order1);
		entityManager.persist(order2);
	}
	
	
	
	/*
	 * 同 hibernate 的reflush 方法，根据数据表的记录同步持久化对象的状态
	 */
	@Test
	void testReFlush() {
		Customer customer = entityManager.find(Customer.class, 2);
		customer.setName("福原爱");
		
		entityManager.refresh(customer);
		System.out.println(customer);
	}
	
	/*
	 * 同 hibernate 的flush 方法，根据持久化对象和数据表的记录保持一致（以持久化对象为主）
	 */
	@Test
	void testFlush() {
		Customer customer = entityManager.find(Customer.class, 2);
		System.out.println(customer);
		customer.setName("林允儿");
		
		entityManager.flush();
	}
	
	
	
	/*
	 * 若传入的对象是一个游离对象（有OID，persistence 方法会直接报错）
	 * 1. 若在 EntityManager 的缓存中有该对象
	 * 2. JPA 将游离对象的属性值复制到 EntityManager 的缓存中的对象中
	 * 3. 对 EntityManager 的缓存中的对象执行 update 操作
	 */
	@Test
	void testMerge4() {
		Customer customer = new Customer("芭芭拉", 18);
		customer.setBirth(new Date());
		customer.setCreateTime(new Date());
		customer.setEmail("jiexika@we.com");
		customer.setId(9);
		
		Customer customer2 = entityManager.find(Customer.class, 9);
		
		entityManager.merge(customer);
		
		System.out.println(customer == customer2);	//false
	}
	
	
	/*
	 * 若传入的对象是一个游离对象（有OID，persistence 方法会直接报错）
	 * 1. 若在 EntityManager 的缓存中没有该对象
	 * 2. 若在数据表中有该条记录
	 * 3. JPA 查询数据表中的对应记录，返回该记录对应的对象，将游离对象的属性值复制到查询出来的对象中
	 * 4. 对查询的对象执行 update 操作
	 */
	@Test
	void testMerge3() {
		Customer customer = new Customer("杰西卡", 18);
		customer.setBirth(new Date());
		customer.setCreateTime(new Date());
		customer.setEmail("jiexika@we.com");
		customer.setId(9);
		
		Customer customer2 = entityManager.merge(customer);
		System.out.println(customer == customer2);	//false
	}
	
	
	/*
	 * 若传入的对象是一个游离对象（有OID，persistence 方法会直接报错）
	 * 1. 若在 EntityManager 的缓存中没有该对象
	 * 2. 若在数据表中没有该条记录
	 * 3. JPA 会创建一个新的对象，将游离对象的属性值复制到 新的对象中
	 * 4. 对新的对象执行 insert操作
	 */
	@Test
	void testMerge2() {
		Customer customer = new Customer("安琪儿", 18);
		customer.setBirth(new Date());
		customer.setCreateTime(new Date());
		customer.setEmail("angle@we.com");
		customer.setId(100);
		
		Customer customer2 = entityManager.merge(customer);
		System.out.println(customer.getId());
		System.out.println(customer2.getId());
	}
	
	
	
	/*
	 * 类似于 hibernate的session 的saveOrUpdate 方法
	 * 1. 若传入的是一个临时对象(没有id)，
	 * 		将会创建一个新的对象，将临时对象的属性复制到新的对象中，对新的对象执行insert 操作，
	 * 		所以新的对象有id，而临时对象没有id（不同于 persistence 方法）
	 */
	@Test
	void testMerge1() {
		Customer customer = new Customer("薇薇安", 18);
		customer.setBirth(new Date());
		customer.setCreateTime(new Date());
		customer.setEmail("weiweian@we.com");
		
		Customer customer2 = entityManager.merge(customer);
		System.out.println(customer.getId());
		System.out.println(customer2.getId());
	}
	

	
	/*
	 * 类似于 hibernate 的delete方法，把对象对应的记录从数据表中删除 注意：该方法只能删除持久化对象（可以理解为从数据表查询的对象）， 而
	 * hibernate的delete方法还可以根据删除游离对象删除对应的数据表记录（即根据不在session缓存的customer对象
	 * 的id来找到数据表中对应的记录并删除）
	 */
	@Test
	void testRemove() {

		// Customer customer = new Customer();
		// customer.setId(1);

		Customer customer = entityManager.find(Customer.class, 1);
		entityManager.remove(customer);
	}

	/*
	 * 类似于 hibernate 的save 方法，使对象由临时状态变为持久化状态 和 hibernate 的save
	 * 方法不同的是，若对象自己有id，则不能执行 insert 操作，会抛出异常
	 */
	@Test
	void testPersistence() {
		Customer customer = new Customer("莉莉丝", 17);
		customer.setBirth(new Date());
		customer.setCreateTime(new Date());
		customer.setEmail("123@we.com");
		// customer.setId(234);
		entityManager.persist(customer);
		System.out.println(customer.getId());
	}

	/*
	 * 类似于session 中的load方法，等到使用的时候才发送 sql语句，否则返回一个代理 还是可能出现懒加载异常
	 */
	@Test
	void testGetReference() {
		Customer customer = entityManager.getReference(Customer.class, 1);
		System.out.println(customer.getClass().getName());
		System.out.println("-------------");
		System.out.println(customer);
	}

	/*
	 * 类似于 session 的get方法，查询对象的时候就发送 sql 语句，而不是等到使用的时候再发
	 */
	@Test
	void testFind() {
		Customer customer = entityManager.find(Customer.class, 2);
		System.out.println("-------------");
		System.out.println(customer);
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
