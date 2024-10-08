package com.example.portfolio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.portfolio.model.Thumbnail;

@Repository
public interface ThumbnailRepository extends JpaRepository<Thumbnail, Long>{
	List<Thumbnail> findByProjectId(Long id);

}
