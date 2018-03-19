package com.zc.cris.helloworld;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Cacheable(true)
@NamedQuery(name="testNamedQuery", query="select c from Customer c where c.age = :age")
@Table(name = "JAP_CUSTOMERS")
@Entity
public class Customer {

	private Integer id;
	private String name;

	private String email;
	private int age;

	private Date birth;
	private Date createTime;
	
	private Set<Order> orders = new HashSet<>();

	// @TableGenerator(name="ID_GENERATOR",
	// table="jpa_id_generator",
	// pkColumnName="PK_NAME",
	// pkColumnValue="CUSTOMER_ID",
	// valueColumnName="PK_VALUE",
	// allocationSize=100)
	// @GeneratedValue(strategy=GenerationType.TABLE, generator="ID_GENERATOR")
	// @Column(name="ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "名字", length = 50, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Temporal(TemporalType.DATE)
	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	// 工具方法，不需要映射为数据表的一列
	@Transient
	public String getInfo() {
		return "名字：" + name + "年龄：" + age;
	}

	public Customer(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}

	public Customer() {
		super();

	}
	
	// @OneToMany 单向一对多的关系
	// 使用@JoinColumn 指定多的一方的外键列名，值则是一的一方的主键id
	// 可以使用 @OneToMany 的fetch 属性来修改默认的懒加载策略
	// 可以通过修改 @OneToMany 的cascade 属性来修改默认的删除策略(删除一之前将多的外键置为空)
	// 注意：双向1-n的情况下，如果在1的一端 的 @OneToMany 中使用 mappedBy 属性，则 @OneToMany 端就一定不要再使用 @JoinColumn 注解了
//	@JoinColumn(name="CUSTOMER_ID")
	@OneToMany(fetch=FetchType.LAZY, cascade= {CascadeType.REMOVE}, mappedBy="customer")
	public Set<Order> getOrders() {
		return orders;
	}
	
	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", name=" + name + ", email=" + email + ", age=" + age + ", birth=" + birth
				+ ", createTime=" + createTime + "]";
	}
	
}
