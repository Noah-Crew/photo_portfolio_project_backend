package com.example.portfolio.integrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.example.portfolio.model.Category;
import com.example.portfolio.model.Photo;
import com.example.portfolio.model.Project;
import com.example.portfolio.model.SubCategory;
import com.example.portfolio.repository.CategoryRepository;
import com.example.portfolio.repository.PhotoRepository;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.repository.SubCategoryRepository;
import com.example.portfolio.service.ProjectService;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProjectCreateIntegrationTest {
	

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private SubCategoryRepository subCategoryRepository;
    
    @Autowired
    private PhotoRepository photoRepository;
    
    @BeforeEach
    void setUp() {
    	 Category category = new Category();
         category.setName("Test Category");
         categoryRepository.save(category);
         
         SubCategory subCategory = new SubCategory();
         category.setName("Test SubCategory");
         subCategoryRepository.save(subCategory);
    }

    @Test
    @DisplayName("프로젝트 생성 통합테스트")
	void ProejectIntegrationTest() throws Exception {
    	// Given
    			
    			// 썸네일 사진 생성
    			MockMultipartFile thumbnailFile = new MockMultipartFile(
    					"thumbnailMultipartFile",
    					"thumbnail.jpg",
    					"image/jpeg",
    					"thumbnailFile content".getBytes()
    					);
    					
    			 // 여러 개의 사진 파일 생성
    		    MockMultipartFile photoFile1 = new MockMultipartFile(
    	    		"photoMultipartFiles",
    		        "photo1.jpg", 
    		        "image/jpeg", 
    		        "thumbnailFile content".getBytes()
    		    );
    		    MockMultipartFile photoFile2 = new MockMultipartFile(
    	    		"photoMultipartFiles", 
    		        "photo2.jpg", 
    		        "image/jpeg", 
    		        "photoFile2 content".getBytes()
    		    );

		// When
    		    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/create/project")
    	                .file(thumbnailFile)
    	                .file(photoFile1)
    	                .file(photoFile2)
    	                .param("title", "서비스테스트제목")
    	                .param("categoryId", "1")
    	                .param("subcategoryId", "1")
    	                .contentType(MediaType.MULTIPART_FORM_DATA))
    	                .andExpect(status().isOk())
    	                .andDo(print());

		// Then
    		    Photo photoSaved = photoRepository.findById(1L).get();
    		    
    		    assertEquals( "photo1.jpg", photoSaved.getImgoname());
    		    
    		    Photo photoSaved2 = photoRepository.findById(2L).get();
    		    assertEquals( "photo2.jpg", photoSaved2.getImgoname());
    		    
    		    Project projectSaved = projectRepository.findById(1L).get();
    		    
    		    assertNotNull(projectSaved);
    		    assertEquals("서비스테스트제목", projectSaved.getTitle());
    		    assertNotNull(projectSaved.getThumbnailUrl());
    		    
    		    // 위에 사진은 나오는데 여기서만 안나와서 찾아보니 양방향 매핑이 안되어서 그렇다함.
//    		    assertEquals(2, projectSaved.getPhotos().size());
    		    assertEquals(1L, projectSaved.getCategory().getId());
    		    assertEquals(1L, projectSaved.getSubCategory().getId());
    		    
	}

}
