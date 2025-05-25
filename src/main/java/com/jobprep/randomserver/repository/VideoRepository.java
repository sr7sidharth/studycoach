package com.jobprep.randomserver.repository;

import com.jobprep.randomserver.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, String> {

}
