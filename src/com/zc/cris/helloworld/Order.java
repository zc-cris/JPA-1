package com.zc.cris.helloworld;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name = "JPA_ORDERS")
@Entity
public class Order {
	private Integer id;
	private String name;

	private Customer customer;

	/*
	 * 只有 strategy=GenerationType.IDENTITY，mysql才会是主键自增的策略，否则和 oracle一样都是通过额外的
	 * 序列表来生成主键
	 */
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Id
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "ORDER_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// @ManyToOne 声明多对一 的关系
	// @JoinColumn 用于指定外键（Customer 的id）
	// 可以使用 @ManyToOne 的fetch 属性来修改默认的关联属性的加载策略
	@JoinColumn(name = "CUSTOMER_ID")
	@ManyToOne(fetch=FetchType.LAZY)
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

}
