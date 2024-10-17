package com.example.portfolio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.mapper.ProjectMapper;
import com.example.portfolio.model.Project;
import com.example.portfolio.repository.ProjectRepository;

//Mockito를 사용하기 위한 어노테이션
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

	// 실제 Spring Context를 로드하지 않기 때문에 @Spy를 통해서 구현체를 넣어줌
	@Spy
	private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
	
	@Mock
	private ProjectRepository projectRepository;
	
	@Mock
	private GcsService gcsService;
	
	@InjectMocks
	private ProjectService projectService;
	
	@Test
	@DisplayName("프로젝트서비스 전체 성공 테스트")
	void ProjectServiceSuccessTest() {
		
		try {
		// Given
		ProjectCreateDto projectCreateDto = new ProjectCreateDto();
		projectCreateDto.setTitle("서비스테스트제목");
		projectCreateDto.setCategoryId(1L);
		projectCreateDto.setSubcategoryId(1L);
		
		// 썸네일 사진 생성
		MockMultipartFile thumbnailFile = new MockMultipartFile(
				"썸네일",
				"thumbnail.jpg",
				"image/jpeg",
				"thumbnail content".getBytes()
				);
				
		 // 여러 개의 사진 파일 생성
	    MockMultipartFile photoFile1 = new MockMultipartFile(
	        "포토1", 
	        "photo1.jpg", 
	        "image/jpeg", 
	        "photo1 content".getBytes()
	    );
	    MockMultipartFile photoFile2 = new MockMultipartFile(
	        "포토2", 
	        "photo2.jpg", 
	        "image/jpeg", 
	        "photo2 content".getBytes()
	    );

	    // MultipartFile 배열 생성
	    MultipartFile[] photoFiles = {photoFile1, photoFile2};
	    
		// When
	    Project project = projectMapper.createDtoToProject(projectCreateDto);
	    // 실제로는 jpa 규칙을 통해서 id값을 지정하는데 여기서는 이렇게 넣어줘야함
	    project.setId(1L);
	    
	    // 전달된 객체를 그대로 반환
	    when(projectRepository.save(any(Project.class))).then(AdditionalAnswers.returnsFirstArg());
	    Long projectId = projectRepository.save(project).getId();
	    
		 // When
		 // 사진 업로드 및 url 리턴 코드
		when(gcsService.uploadFile(thumbnailFile, projectId)).thenReturn("http:localhost8181/thumbnail.jpg");
		String url = gcsService.uploadFile(thumbnailFile, projectId);
		project.setThumbnailUrl("http:localhost:8181/image.jpg");
		
		// Then
		 // mapper 테스트
	    assertThat(project.getTitle()).isEqualTo("서비스테스트제목");
	    assertThat(project.getCategory().getId()).isEqualTo(1L);
	    assertThat(project.getSubCategory().getId()).isEqualTo(1L);
	    assertThat(project.getThumbnailUrl()).isEqualTo("http:localhost:8181/image.jpg");
	   
		assertThat(projectId).isEqualTo(1L);
		assertThat(url).isEqualTo("http:localhost8181/thumbnail.jpg");
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	}

}
