package com.jobprep.randomserver.repository;

import com.jobprep.randomserver.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, String> {
    @Query("SELECT v FROM Video v WHERE v.duration > :min")
    List<Video> findVideosLengthGreater(@Param("min") int min);

    List<Video> findByCompletedFalse();
}
