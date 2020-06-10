package com.yok.manage_course.dao;

import com.yok.framework.domain.course.TeachplanMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeachplanMediaRepository extends JpaRepository<TeachplanMedia, String> {
    public long deleteByCourseid(String id);
    public long deleteByTeachplanId(String id);
    public long deleteByMediaId(String id);
    public List<TeachplanMedia> findByCourseid(String id);
}
