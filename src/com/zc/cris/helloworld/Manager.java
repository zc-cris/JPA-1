package com.zc.cris.helloworld;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Table(name="JPA_MANAGERS")
@Entity
public class Manager {
	
	private Integer id;
	private String name;
	
	private Department department;

	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Id
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="MANAGER_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	// 对于不维护关联关系的一方，即没有外键的一方，使用 @OneToOne 来映射，并且要设置 mappedBy=mgr 来放弃关联关系的维护
	// 事实上，对于基于外键的 1-1（一般都是双向），我们通过都是通过拥有外键的一端来维护关联关系，没有外键（将主键提供给另一端作为外键）的一方放弃
	// 维护关联关系
	@OneToOne(mappedBy="mgr")
	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	@Override
	public String toString() {
		return "Manager [id=" + id + ", name=" + name + ", department=" + department + "]";
	}
	
	
	
}
