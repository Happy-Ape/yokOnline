package com.yok.framework.domain.learning;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name="xc_learning_comment")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class XcLearningComment implements Serializable {
    private static final long serialVersionUID = -916357210051789799L;
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;
    @Column(name = "course_id")
    private String courseId;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "comment")
    private String comment;
    @Column(name = "score")
    private Float score;
    @Column(name = "create_time")
    private Date createTime;

}
