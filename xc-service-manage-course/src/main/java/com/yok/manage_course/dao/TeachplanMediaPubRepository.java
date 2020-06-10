package com.yok.manage_course.dao;

import com.yok.framework.domain.course.TeachplanMedia;
import com.yok.framework.domain.course.TeachplanMediaPub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeachplanMediaPubRepository extends JpaRepository<TeachplanMediaPub, String> {
    public long deleteByCourseid(String id);
    public long deleteByMediaId(String id);
    public List<TeachplanMediaPub> findByCourseid(String id);
}
