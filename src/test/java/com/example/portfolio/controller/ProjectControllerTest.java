package com.example.portfolio.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.service.AdminService;
import com.example.portfolio.service.CategoryService;
import com.example.portfolio.service.PhotoService;
import com.example.portfolio.service.ProjectService;
import com.google.gson.Gson;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
    private ProjectService projectService;
	    
    @MockBean
    private CategoryService categoryService;
    
    @MockBean
    private AdminService adminService;
    
    @MockBean
    private PhotoService photoService;

	@Test
	@DisplayName("컨트롤러 생성 성공 테스트")
	void ControllerCreateTest() throws Exception {
		
		// Given
		// 썸네일 사진 생성
		MockMultipartFile thumbnailFile = new MockMultipartFile(
				"thumbnailMultipartFile",
				"thumbnail.jpg",
				"image/jpeg",
				"thumbnail content".getBytes()
				);
				
		 // 여러 개의 사진 파일 생성
	    MockMultipartFile photoFile1 = new MockMultipartFile(
    		"photoMultipartFiles",
	        "photo1.jpg", 
	        "image/jpeg", 
	        "photo1 content".getBytes()
	    );
	    MockMultipartFile photoFile2 = new MockMultipartFile(
    		"photoMultipartFiles", 
	        "photo2.jpg", 
	        "image/jpeg", 
	        "photo2 content".getBytes()
	    );
	    
        // When & Then
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
        
        ArgumentCaptor<ProjectCreateDto> dtoCaptor = ArgumentCaptor.forClass(ProjectCreateDto.class);
        // 서비스로 전달되는 ProjectCreateDto를 캡처해서 검사
        verify(projectService).createProject(dtoCaptor.capture());
        ProjectCreateDto capturedDto = dtoCaptor.getValue();

        assertNotNull(capturedDto.getThumbnailMultipartFile());
        assertEquals(2, capturedDto.getPhotoMultipartFiles().length);
        assertEquals("서비스테스트제목", capturedDto.getTitle());
        assertEquals(1L, capturedDto.getCategoryId());
        assertEquals(1L, capturedDto.getSubcategoryId());
		
        // 서비스를 호출 했는지 확인
        verify(projectService).createProject(any(ProjectCreateDto.class));
	}

}
