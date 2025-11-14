package com.acc.local.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "univ_departments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UnivDepartInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "first_major", nullable = false, unique = true)
	private String firstMajor;

	@Column(name = "colege", nullable = false)
	private String college;

	@Column(name = "department", nullable = false)
	private String department;

	@Column(name = "registered_by", nullable = false)
	private String registeredBy;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Builder
	private UnivDepartInfoEntity(String firstMajor, String college, String department, String registeredBy) {
		this.firstMajor = firstMajor;
		this.college = college;
		this.department = department;
		this.registeredBy = registeredBy;
		this.createdAt = LocalDateTime.now();
	}

}
