package com.example.portfolio.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.portfolio.model.Category;
import com.example.portfolio.model.Photo;
import com.example.portfolio.model.Project;
import com.example.portfolio.model.SubCategory;

@DataJpaTest
@ActiveProfiles("test") 
class ProjectRepositoryTest {
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private SubCategoryRepository subCategoryRepository;
	
	@Test
	@DisplayName("카테고리&서브카테고리 레포지토리 성공 테스트")
	void categoryAndSubCategoryTest() {
		// given
		Project project = new Project();
		project.setTitle("Test Project");
		project.setView(0);
		project.setThumbnailUrl("http:localhost:8181/testImage.jpg");
		project.setPhotos(new ArrayList<>());
		
		Category category = new Category();
		category.setName("Test Category");
		Category savedCategory = categoryRepository.save(category);
		category.setSubCategories(new ArrayList<>());
		  
		SubCategory subCategory1 = new SubCategory();
        subCategory1.setName("Test SubCategory1");
        subCategory1.setCategory(savedCategory);
        SubCategory subCategory1Saved = subCategoryRepository.save(subCategory1);
        
        SubCategory subCategory2 = new SubCategory();
        subCategory2.setName("Test SubCategory2");
        subCategory2.setCategory(savedCategory);
        SubCategory subCategory2Saved = subCategoryRepository.save(subCategory2);
        
        category.getSubCategories().add(subCategory1Saved);
        category.getSubCategories().add(subCategory2Saved);
        
        Category categorySaved = categoryRepository.save(category);
        
        Photo photo1 = new Photo();
        photo1.setProjectId(1L);
        photo1.setImgoname("Test name1");
        photo1.setImgtype(".jpg");
        photo1.setImageUrl("http:localhost:8181/tesPhoto1.jpg");
        
        Photo photo2 = new Photo();
        photo2.setProjectId(1L);
        photo2.setImgoname("Test name2");
        photo2.setImgtype(".jpg");
        photo2.setImageUrl("http:localhost:8181/tesPhoto2.jpg");
        
        project.getPhotos().add(photo1);
        project.getPhotos().add(photo2);
        
		// when
        Project projectSaved = projectRepository.save(project);
        
		// then
        
        // 카테고리
        assertNotNull(categorySaved.getId());
        assertEquals("Test Category", categorySaved.getName());
        assertEquals(2, categorySaved.getSubCategories().size());
        
        // 서브 카테고리
        assertNotNull(subCategory1Saved.getId());
        assertNotNull(subCategory2Saved.getId());
        assertEquals("Test SubCategory1", subCategory1Saved.getName());
        assertEquals("Test SubCategory2", subCategory2Saved.getName());
        assertTrue(categorySaved.getSubCategories().stream()
        		.anyMatch(sub -> "Test SubCategory1".equals(sub.getName())));
        assertTrue(categorySaved.getSubCategories().stream()
        		.anyMatch(sub -> "Test SubCategory2".equals(sub.getName())));
        
        // 프로젝트
        assertNotNull(projectSaved.getId());
        assertEquals("Test Project", projectSaved.getTitle());
        assertEquals(2, projectSaved.getPhotos().size());
        assertTrue(projectSaved.getPhotos().stream()
        		.anyMatch(pro -> "Test name1".equals(pro.getImgoname())));
        assertTrue(projectSaved.getPhotos().stream()
        		.anyMatch(pro -> "Test name2".equals(pro.getImgoname())));
        
	}
}
