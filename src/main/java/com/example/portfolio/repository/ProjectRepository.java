package com.example.portfolio.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.portfolio.dto.ProjectListDto;
import com.example.portfolio.model.Project;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
	@Query("SELECT new com.example.portfolio.dto.ProjectListDto(p.id, p.title, p.thumbnailUrl, p.createdAt, p.view, c.name, NULL) "
			+ "FROM Project p JOIN p.category c "
			+ "where c.id= :categoryId ")
	Slice<ProjectListDto> findByCategory_id(Pageable pageable,@Param("categoryId") Long categoryId);
	
	@Query("SELECT new com.example.portfolio.dto.ProjectListDto(p.id, p.title, p.thumbnailUrl, p.createdAt, p.view, s.name, NULL) "
			+ "FROM Project p JOIN p.subCategory s "
			+ "where s.id= :subCategoryId " )
	Slice<ProjectListDto> findBySubCategory_id(Pageable pageable,@Param("subCategoryId") Long subCategoryId);

	@Query("SELECT new com.example.portfolio.dto.ProjectListDto(p.id, p.title, p.thumbnailUrl, p.createdAt, p.view, c.name, " 
			+"(SELECT COUNT(ph) FROM Photo ph WHERE ph.projectId = p.id)) "
			+"FROM Project p JOIN p.category c "
		    +"WHERE p.title LIKE (CONCAT('%', :keyWord, '%')) ")
	Page<ProjectListDto> findByKeyWord(Pageable pageable,@Param("keyWord") String keyWord);
	
	List<Project> findByCategory_Id(Long categoryId);

	List<Project> findBySubCategory_Id(Long subCategory);

	// 카테고리 사용 유무 확인
	boolean existsByCategory_Id(Long Categoryid);

	// 서브 카테고리 사용 유무
	boolean existsBySubCategory_Id(Long subCategoryId);
	
	
	@Modifying
	@Transactional
	@Query("UPDATE Project p SET p.view = p.view + 1 WHERE p.id = :projectId")
	void updateViewCount(@Param("projectId") Long projectId);
	
}
 