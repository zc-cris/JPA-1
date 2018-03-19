package com.zc.cris.helloworld;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Table(name="JPA_STUDENTS")
@Entity
public class Student {

	private Integer id;
	private String name;
	private Set<Course> courses = new HashSet<>();

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="STUDENT_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	/*
	 * @ManyToMany 注解用来映射多对多的关联关系
	 * 使用 @JoinTable 来映射中间表
	 * 1. name 定义中间表的名字
	 * 2. joinColumns 属性定义当前表在中间表的外键
	 * 2.1 name 属性指定当前表在中间表的字段的名字
	 * 2.2 referencedColumnName 属性指定当前表在中间表的字段引用的当前表的字段
	 * 3. inverseJoinColumns 属性映射当前表关联的另外一张表在中间表的外键 
	 */
	@JoinTable(name="STUDENT_COURSE",
			joinColumns= {@JoinColumn(name="STUDENT_ID", referencedColumnName="ID")},
				inverseJoinColumns= {@JoinColumn(name="COURSE_ID", referencedColumnName="ID")})
	@ManyToMany
	public Set<Course> getCourses() {
		return courses;
	}

	public void setCourses(Set<Course> courses) {
		this.courses = courses;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", courses=" + courses + "]";
	}

}
