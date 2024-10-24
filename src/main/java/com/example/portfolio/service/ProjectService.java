package com.example.portfolio.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.portfolio.dto.PhotoListDto;
import com.example.portfolio.dto.ProjectCreateDto;
import com.example.portfolio.dto.ProjectDetailDto;
import com.example.portfolio.dto.ProjectListDto;
import com.example.portfolio.dto.ProjectUpdateDto;
import com.example.portfolio.mapper.ProjectMapper;
import com.example.portfolio.model.Photo;
import com.example.portfolio.model.Project;
import com.example.portfolio.repository.PhotoRepository;
import com.example.portfolio.repository.ProjectRepository;

import jakarta.transaction.Transactional;

@Service
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final GcsService gcsService;
	private final PhotoService photoService;
	private final ProjectMapper projectMapper;
	private final PhotoRepository photoRepository;

	// 여러 의존성을 생성자로 주입
	public ProjectService(ProjectRepository projectRepository,
							GcsService gcsService, PhotoService photoService, 
							ProjectMapper projectMapper, PhotoRepository photoRepository) {
		this.projectRepository = projectRepository;
		this.gcsService = gcsService;
		this.photoService = photoService;
		this.projectMapper = projectMapper;
		this.photoRepository = photoRepository;
	}

	// 프로젝트 생성
	@Transactional
	public void  createProject(ProjectCreateDto projectCreateDtos) {
		
		Project project = projectMapper.createDtoToProject(projectCreateDtos);
		
		Long projectId = projectRepository.save(project).getId();
		
		// 썸네일 생성
		MultipartFile multipartFile = projectCreateDtos.getThumbnailMultipartFile();
		try {
			String url = gcsService.uploadFile(multipartFile, projectId); // GCS에 파일 업로드
			project.setThumbnailUrl(url);
		} catch (IOException e) {
			throw new RuntimeException("Failed to upload thumbnail to GCS", e); // 예외 처리
		}
		// 사진 생성
		photoService.createPhotos(projectCreateDtos, projectId);

		// 최종적으로 프로젝트 업데이트
		projectRepository.save(project);
	}

	// 프로젝트 업데이트
	@Transactional
	public void updateProject(ProjectUpdateDto projectUpdateDto) {
		
		Project project = projectMapper.upadateDtoToProject(projectUpdateDto);
		String thumbnailurl = projectRepository.findById(projectUpdateDto.getId()).get().getThumbnailUrl();
		project.setThumbnailUrl(thumbnailurl);
		
		// 썸네일이 있는 경우에만 업데이트
		if (projectUpdateDto.getThumbnailMultipartFile() != null
				&& !projectUpdateDto.getThumbnailMultipartFile().isEmpty()) {
			
			try {
				// 기존 썸네일 삭제
				gcsService.deleteThumbnailFile(project.getThumbnailUrl());

				// 새 썸네일 업로드
				String url = gcsService.uploadFile(projectUpdateDto.getThumbnailMultipartFile(), project.getId());
				project.setThumbnailUrl(url);
			} catch (IOException e) {
				throw new RuntimeException("Failed to upload new thumbnail to GCS", e);
			}
		}
		
		// 기존 사진 삭제
		if(projectUpdateDto.getDeletedPhotoIds() != null 
				&& !projectUpdateDto.getDeletedPhotoIds().isEmpty()) {
			 photoService.deleteSelectedPhotos(projectUpdateDto.getDeletedPhotoIds());
		}

		// 사진이 있는 경우 업데이트
		if (projectUpdateDto.getPhotoMultipartFiles() != null 
				&& projectUpdateDto.getPhotoMultipartFiles().length > 0) {
			photoService.updatePhotos(projectUpdateDto);
		}

		projectRepository.save(project);
	}

	// 프로젝트 삭제
	@Transactional
	public void deleteProject(Long id) {
		Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
		// GCS 썸네일과 관련 사진들 삭제
		try {
			gcsService.deleteThumbnailFile(project.getThumbnailUrl());
			photoService.deletePhotosByProjectId(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		projectRepository.delete(project);
	}

	//프로젝트 불러오기
	@Transactional
	public List<ProjectListDto> getProjectList(Pageable pageable, Long CategoryId,Long subCategoryId) {
		//서브카테고리가 선택되지 않았을 때 카테고리로 찾아오기 
		if(CategoryId==null) {
			return projectRepository.findAllProject(pageable).getContent();
		}else if(subCategoryId==null) {
			return projectRepository.findByCategory_id(pageable, CategoryId).getContent();
		}else {
			return projectRepository.findBySubCategory_id(pageable, subCategoryId).getContent();
		}
	}
	
	// 프로젝트 디테일 정보 가져오기
	public ProjectDetailDto getAdminProject(Long projectId) {
		ProjectDetailDto projectDetail =  projectRepository.findProjectDetailByProjectId(projectId);
		List<PhotoListDto> photoList = photoRepository.findDetailPhotoByProjectId(projectId);
		projectDetail.setPhotos(photoList);
		return projectDetail;
	}

}
