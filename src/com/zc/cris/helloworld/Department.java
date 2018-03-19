package com.zc.cris.helloworld;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table(name="JPA_DEPARTMENTS")
@Entity
public class Department {
	
	private Integer id;
	private String name;
	
	private Manager mgr;

	// JPA 2.1 中如果连接mysql，必须设置这个生成策略，默认使用 序列生成策略
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Id
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="DEPT_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// 使用 @OneToOne 来映射一对一关系
	// 如果需要在当前表中添加外键（指向另外一方的主键），那么需要 @JoinColumn 来进行映射，注意，1-1，必须设置 unique=true （外键唯一性约束）
	@JoinColumn(name="MANAGER_ID", unique=true)
	@OneToOne(fetch=FetchType.LAZY)
	public Manager getMgr() {
		return mgr;
	}

	public void setMgr(Manager mgr) {
		this.mgr = mgr;
	}

	@Override
	public String toString() {
		return "Department [id=" + id + ", name=" + name + ", mgr=" + mgr + "]";
	}
	
	
}
