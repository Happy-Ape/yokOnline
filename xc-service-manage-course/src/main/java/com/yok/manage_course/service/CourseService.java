package com.yok.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yok.framework.domain.cms.CmsPage;
import com.yok.framework.domain.cms.response.CmsPageResult;
import com.yok.framework.domain.cms.response.CmsPostPageResult;
import com.yok.framework.domain.course.*;
import com.yok.framework.domain.course.ext.CourseInfo;
import com.yok.framework.domain.course.ext.CourseView;
import com.yok.framework.domain.course.ext.TeachplanNode;
import com.yok.framework.domain.course.request.CourseListRequest;
import com.yok.framework.domain.course.response.AddCourseResult;
import com.yok.framework.domain.course.response.CourseCode;
import com.yok.framework.domain.course.response.CoursePublishResult;
import com.yok.framework.exception.ExceptionCast;
import com.yok.framework.model.response.CommonCode;
import com.yok.framework.model.response.QueryResponseResult;
import com.yok.framework.model.response.QueryResult;
import com.yok.framework.model.response.ResponseResult;
import com.yok.manage_course.client.CmsPageClient;
import com.yok.manage_course.client.SeachClient;
import com.yok.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
public class CourseService {

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    TeachplanMediaRepository teachplanMediaRepository;

    @Autowired
    CourseMarketRepository courseMarketRepository;

    @Autowired
    CoursePicRepository coursePicRepository;

    @Autowired
    CoursePubRepository coursePubRepository;

    @Autowired
    CmsPageClient cmsPageClient;

    @Autowired
    TeachplanMediaPubRepository teachplanMediaPubRepository;

    @Autowired
    SeachClient seachClient;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;

    /**
     * 课程计划查询
     *
     * @param courseId
     * @return
     */
    public TeachplanNode findTeachplanList(String courseId) {
        return teachplanMapper.selectList(courseId);
    }

    /**
     * 添加课程计划，保证事务
     *
     * @param teachplan
     * @return
     */
    @Transactional
    public ResponseResult addTeachplan(String id, Teachplan teachplan) {
        if (teachplan == null ||
                StringUtils.isEmpty(teachplan.getCourseid()) ||
                StringUtils.isEmpty(teachplan.getPname())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        if (id.equals("null")) {
            //课程id
            String courseid = teachplan.getCourseid();
            //页面传入的parentId
            String parentid = teachplan.getParentid();
            if (StringUtils.isEmpty(parentid)) {
                //取出该课程的根结点
                parentid = this.getTeachplanRoot(courseid);
            }
            Optional<Teachplan> optional = teachplanRepository.findById(parentid);
            Teachplan parentNode = optional.get();
            //父结点的级别
            String grade = parentNode.getGrade();
            //新结点
            Teachplan teachplanNew = new Teachplan();
            //将页面提交的teachplan信息拷贝到teachplanNew对象中
            BeanUtils.copyProperties(teachplan, teachplanNew);
            teachplanNew.setParentid(parentid);
            teachplanNew.setCourseid(courseid);
            if (grade.equals("1")) {
                teachplanNew.setGrade("2");//级别，根据父结点的级别来设置
            } else {
                teachplanNew.setGrade("3");
            }
            teachplanRepository.save(teachplanNew);
            return new ResponseResult(CommonCode.SUCCESS);
        } else {
            return this.updateTeachplan(id, teachplan);
        }
    }

    /**
     * 查询课程的根结点，如果查询不到要自动添加根结点
     *
     * @param courseId
     * @return
     */
    private String getTeachplanRoot(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return null;
        }
        //课程信息
        CourseBase courseBase = optional.get();
        //查询课程的根结点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplanList == null || teachplanList.size() <= 0) {
            //查询不到，要自动添加根结点
            Teachplan teachplan = new Teachplan();
            teachplan.setParentid("0");
            teachplan.setGrade("1");
            teachplan.setPname(courseBase.getName());
            teachplan.setCourseid(courseId);
            teachplan.setStatus("0");
            teachplanRepository.save(teachplan);
            return teachplan.getId();
        }
        //返回根结点id
        return teachplanList.get(0).getId();
    }

    /**
     * 课程列表分页查询  
     *
     * @param page
     * @param size
     * @param courseListRequest
     * @return
     */
    public QueryResponseResult findCourseList(String company_id,int page, int size, CourseListRequest courseListRequest) {
        if (courseListRequest == null) {
            courseListRequest = new CourseListRequest();
        }
        courseListRequest.setCompanyId(company_id);
        if (page <= 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 20;
        }
        //设置分页参数         
        PageHelper.startPage(page, size);
        //分页查询
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        //查询列表         
        List<CourseInfo> list = courseListPage.getResult();
        //总记录数        
        long total = courseListPage.getTotal();
        //查询结果集         
        QueryResult<CourseInfo> courseIncfoQueryResult = new QueryResult<CourseInfo>();
        courseIncfoQueryResult.setList(list);
        courseIncfoQueryResult.setTotal(total);
        return new QueryResponseResult(CommonCode.SUCCESS, courseIncfoQueryResult);
    }

    /**
     * 添加课程提交 
     *
     * @param courseBase
     * @return
     */
    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase) {
        if (courseBase == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //课程状态默认为未发布
        courseBase.setStatus("202001");
        courseBase.setCompanyId("1");
        courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS, courseBase.getId());
    }

    /**
     * 获取课程信息
     *
     * @param courseId
     * @return
     */
    public CourseBase getCoursebaseById(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    /**
     * 更新课程信息
     *
     * @param id
     * @param courseBase
     * @return
     */
    @Transactional
    public ResponseResult updateCoursebase(String id, CourseBase courseBase) {
        if (courseBase == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CourseBase one = this.getCoursebaseById(id);
        if (one == null) {
            //抛出异常.. 
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        //修改课程信息          
        one.setName(courseBase.getName());
        one.setMt(courseBase.getMt());
        one.setSt(courseBase.getSt());
        one.setGrade(courseBase.getGrade());
        one.setStudymodel(courseBase.getStudymodel());
        one.setUsers(courseBase.getUsers());
        one.setDescription(courseBase.getDescription());
        one.setCompanyId("1");
        CourseBase save = courseBaseRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 获取课程营销信息
     *
     * @param courseId
     * @return
     */
    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> courseMarket = courseMarketRepository.findById(courseId);
        if (courseMarket.isPresent()) {
            return courseMarket.get();
        }
        return null;
    }

    /**
     * 更新课程营销信息
     *
     * @param id
     * @param courseMarket
     * @return
     */
    @Transactional
    public CourseMarket updateCourseMarket(String id, CourseMarket courseMarket) {
        if (courseMarket == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CourseMarket one = this.getCourseMarketById(id);
        if (one != null) {
            one.setCharge(courseMarket.getCharge());
            //课程有效期，开始时间 
            one.setStartTime(courseMarket.getStartTime());
            //课程有效期，结束时间   
            one.setEndTime(courseMarket.getEndTime());
            //现价
            one.setPrice(courseMarket.getPrice());
            //原价
            one.setPrice_old(courseMarket.getPrice_old());
            one.setQq(courseMarket.getQq());
            one.setValid(courseMarket.getValid());
            one.setExpires(new Date());
            courseMarketRepository.save(one);
        } else {
            //添加课程营销信息          
            one = new CourseMarket();
            BeanUtils.copyProperties(courseMarket, one);
            //设置课程id          
            one.setId(id);
            one.setExpires(new Date());
            courseMarketRepository.save(one);
        }
        return one;
    }

    /**
     * 添加课程图片到课程表（mysql）
     *
     * @param courseId
     * @param pic
     * @return
     */
    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        CoursePic coursePic = null;
        if (optional.isPresent()) {
            coursePic = optional.get();
        }
        if (coursePic == null) {
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 查询课程图片
     *
     * @param courseId
     * @return
     */
    public CoursePic findCoursePic(String courseId) {
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        if (picOptional.isPresent()) {
            CoursePic coursePic = picOptional.get();
            return coursePic;
        }
        return null;
    }

    /**
     * 删除课程图片
     *
     * @param courseId
     * @return
     */
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        long result = coursePicRepository.deleteByCourseid(courseId);
        if (result > 0) {
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 查询课程视图，包括基本信息、图片、营销、课程计划
     *
     * @param id
     * @return
     */
    public CourseView getCourseView(String id) {
        CourseView courseView = new CourseView();
        //课程基本细信息
        CourseBase coursebase = this.getCoursebaseById(id);
        if (coursebase != null) {
            courseView.setCourseBase(coursebase);
        }
        //课程营销信息
        CourseMarket courseMarket = this.getCourseMarketById(id);
        if (courseMarket != null) {
            courseView.setCourseMarket(courseMarket);
        }
        //课程图片
        CoursePic coursePic = this.findCoursePic(id);
        if (coursePic != null) {
            courseView.setCoursePic(coursePic);
        }
        //课程计划
        TeachplanNode teachplanNode = this.findTeachplanList(id);
        if (teachplanNode != null) {
            courseView.setTeachplanNode(teachplanNode);
        }
        return courseView;
    }

    /**
     * 课程预览
     *
     * @param id
     * @return
     */
    public CoursePublishResult preview(String id) {
        //查询课程
        CourseBase courseBaseById = this.getCoursebaseById(id);
        //请求cms添加页面
        //准备cmsPage信息
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre + id);//数据模型url
        cmsPage.setPageName(id + ".html");//页面名称
        cmsPage.setPageAliase(courseBaseById.getName());//页面别名，就是课程名称
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        cmsPage.setPageWebPath(publish_page_webpath);//页面webpath
        cmsPage.setTemplateId(publish_templateId);//页面模板id
        //远程调用cms
        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);
        if (!cmsPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();
        //拼装页面预览的url
        String url = previewUrl + pageId;
        //返回CoursePublishResult对象（当中包含了页面预览的url）
        return new CoursePublishResult(CommonCode.SUCCESS, url);
    }

    /**
     * 课程发布
     *
     * @param id
     * @return
     */
    @Transactional
    public CoursePublishResult publish(String id) {
        //查询课程
        CourseBase courseBaseById = this.getCoursebaseById(id);

        //准备页面信息
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre + id);//数据模型url
        cmsPage.setPageName(id + ".html");//页面名称
        cmsPage.setPageAliase(courseBaseById.getName());//页面别名，就是课程名称
        cmsPage.setPagePhysicalPath("E:/YokStudyUI/xc-ui-pc-static-portal" + publish_page_physicalpath);//页面物理路径
        cmsPage.setPageWebPath(publish_page_webpath);//页面webpath
        cmsPage.setTemplateId(publish_templateId);//页面模板id
        //调用cms一键发布接口将课程详情页面发布到服务器
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if (!cmsPostPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }

        //保存课程的发布状态为“已发布”
        CourseBase courseBase = this.saveCoursePubState(id);
        if (courseBase == null) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }

        //保存课程索引信息:
        //先创建一个coursePub对象
        CoursePub coursePub = createCoursePub(id);
        //将coursePub对象保存到数据库
        saveCoursePub(id, coursePub);

        //向teachplanMediaPub中保存课程媒资信息
        saveTeachplanMediaPub(id);
        //得到页面的url
        String pageUrl = cmsPostPageResult.getPageUrl();
        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }

    /**
     * 向teachplanMediaPub中保存课程媒资信息
     *
     * @param id
     */
    private void saveTeachplanMediaPub(String id) {
        //先删除teachplanMediaPub中的数据
        teachplanMediaPubRepository.deleteByCourseid(id);
        //从teachplanMedia中查询
        List<TeachplanMedia> teachplanMediaList = teachplanMediaRepository.findByCourseid(id);
        List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
        for (TeachplanMedia teachplanMedia : teachplanMediaList) {
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            BeanUtils.copyProperties(teachplanMedia, teachplanMediaPub);
            //添加时间戳
            teachplanMediaPub.setTimestamp(new Date());
            //添加到teachplanMediaPubList
            teachplanMediaPubList.add(teachplanMediaPub);
        }
        //将teachplanMediaList插入到teachplanMediaPub
        teachplanMediaPubRepository.saveAll(teachplanMediaPubList);
    }

    /**
     * 将coursePub对象保存到数据库
     *
     * @param id
     * @param coursePub
     * @return
     */
    private CoursePub saveCoursePub(String id, CoursePub coursePub) {
        CoursePub coursePubNew = null;
        //根据课程id查询coursePub
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(id);
        if (coursePubOptional.isPresent()) {
            coursePubNew = coursePubOptional.get();
        } else {
            coursePubNew = new CoursePub();
        }
        //将coursePub对象中的信息保存到coursePubNew中
        BeanUtils.copyProperties(coursePub, coursePubNew);
        coursePubNew.setId(id);
        //时间戳,给logstach使用
        coursePubNew.setTimestamp(new Date());
        //发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(date);
        coursePubRepository.save(coursePubNew);
        return coursePubNew;
    }

    /**
     * 创建coursePub对象
     *
     * @param id
     * @return
     */
    private CoursePub createCoursePub(String id) {
        CoursePub coursePub = new CoursePub();
        //根据课程id查询course_base
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(id);
        if (baseOptional.isPresent()) {
            CourseBase courseBase = baseOptional.get();
            //将courseBase属性拷贝到CoursePub中
            BeanUtils.copyProperties(courseBase, coursePub);
        }
        //查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if (picOptional.isPresent()) {
            CoursePic coursePic = picOptional.get();
            BeanUtils.copyProperties(coursePic, coursePub);
        }
        //课程营销信息
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if (marketOptional.isPresent()) {
            CourseMarket courseMarket = marketOptional.get();
            BeanUtils.copyProperties(courseMarket, coursePub);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            Float price = courseMarket.getPrice();
            if (price == null){
                coursePub.setPrice(null);
            }else {
                BigDecimal priceString = new BigDecimal(String.valueOf(price));
                coursePub.setPrice(priceString.doubleValue());
            }
            Float oldPrice = courseMarket.getPrice_old();
            if (price == null){
                coursePub.setPrice_old(null);
            }else {
                BigDecimal oldPriceString = new BigDecimal(String.valueOf(oldPrice));
                coursePub.setPrice_old(oldPriceString.doubleValue());
            }
            Date expires = courseMarket.getExpires();
            if (expires != null) {
                String expiresNew = simpleDateFormat.format(expires);
                coursePub.setExpires(expiresNew);
            }
            Date startTime = courseMarket.getStartTime();
            if (startTime != null) {
                String startTimeNew = simpleDateFormat.format(startTime);
                coursePub.setStartTime(startTimeNew);
            }
            Date endTime = courseMarket.getEndTime();
            if (endTime != null) {
                String endTimeNew = simpleDateFormat.format(endTime);
                coursePub.setEndTime(endTimeNew);
            }
        }
        //课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        String jsonString = JSON.toJSONString(teachplanNode);
        //将课程计划信息json串保存到 course_pub中
        coursePub.setTeachplan(jsonString);
        return coursePub;
    }

    /**
     * 更新课程状态为已发布 202002
     *
     * @param courseId
     * @return
     */
    private CourseBase saveCoursePubState(String courseId) {
        CourseBase courseBaseById = this.getCoursebaseById(courseId);
        courseBaseById.setStatus("202002");
        courseBaseRepository.save(courseBaseById);
        return courseBaseById;
    }

    /**
     * 获取课程计划
     *
     * @param id
     * @return
     */
    public Teachplan getTeachplan(String id) {
        Optional<Teachplan> teachplan = teachplanRepository.findById(id);
        if (teachplan.isPresent()) {
            Teachplan teachplan1 = teachplan.get();
            return teachplan1;
        }
        return null;
    }

    /**
     * 修改计划
     *
     * @param id
     * @param teachplan
     * @return
     */
    @Transactional
    public ResponseResult updateTeachplan(String id, Teachplan teachplan) {
        Teachplan teachplan1 = this.getTeachplan(id);
        if (teachplan != null) {
            teachplan1.setId(id);
            teachplan1.setCourseid(teachplan.getCourseid());
            teachplan1.setPname(teachplan.getPname());
            teachplan1.setDescription(teachplan.getDescription());
            teachplan1.setOrderby(teachplan.getOrderby());
            teachplan1.setParentid(teachplan.getParentid());
            teachplan1.setGrade(teachplan.getGrade());
            teachplan1.setPtype(teachplan.getPtype());
            teachplan1.setStatus(teachplan.getStatus());
            teachplan1.setTimelength(teachplan.getTimelength());
            teachplan1.setTrylearn(teachplan.getTrylearn());
            teachplanRepository.save(teachplan1);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 删除教学计划
     *
     * @param id
     * @return
     */
    public ResponseResult deleteTeachplan(String id) {
        Optional<Teachplan> byId = teachplanRepository.findById(id);
        if (byId.isPresent()) {
            Teachplan teachplan = byId.get();
            List<Teachplan> teachplans = teachplanRepository.findByParentid(teachplan.getId());
            if (teachplans.size() > 0) {
                for (int i = 0; i < teachplans.size(); i++) {
                    Teachplan teachplan1 = teachplans.get(i);
                    teachplanRepository.deleteById(teachplan1.getId());
                    Optional<TeachplanMedia> optionMedia = teachplanMediaRepository.findById(teachplan1.getId());
                    if (optionMedia.isPresent()) {
                        teachplanMediaRepository.deleteById(teachplan1.getId());
                    }
                }
            }
        }
        teachplanRepository.deleteById(id);
        Optional<TeachplanMedia> optionalMedia2 = teachplanMediaRepository.findById(id);
        if (optionalMedia2.isPresent()) {
            teachplanMediaRepository.deleteById(id);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 下线课程
     *
     * @param id
     * @return
     */
    @Transactional
    public ResponseResult down(String id) {
        Optional<CourseBase> courseBase = courseBaseRepository.findById(id);
        if (courseBase.isPresent()) {
            CourseBase course = courseBase.get();
            course.setStatus("202003");
            courseBaseRepository.save(course);
            Optional<CoursePub> optionalCoursePub = coursePubRepository.findById(id);
            if (optionalCoursePub.isPresent()){
                CoursePub coursePub = optionalCoursePub.get();
                coursePub.setStatus("202003");
                coursePub.setTimestamp(new Date());
            }
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CourseCode.COURSE_ISNOTEXIST);
    }

    /**
     * 上线课程,(还需要发布）
     *
     * @param id
     * @return
     */
    @Transactional
    public ResponseResult up(String id) {
        Optional<CourseBase> courseBase = courseBaseRepository.findById(id);
        if (courseBase.isPresent()) {
            CourseBase course = courseBase.get();
            course.setStatus("202001");
            courseBaseRepository.save(course);
            Optional<CoursePub> optionalCoursePub = coursePubRepository.findById(id);
            if (optionalCoursePub.isPresent()){
                CoursePub coursePub = optionalCoursePub.get();
                coursePub.setStatus("202001");
                coursePub.setTimestamp(new Date());
            }
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CourseCode.COURSE_ISNOTEXIST);
    }

    /**
     * 删除课程
     *
     * @param id
     * @return
     */
    @Transactional
    public ResponseResult deleteCourse(String id) {
        Optional<CourseBase> courseBase = courseBaseRepository.findById(id);
        if (courseBase.isPresent()) {
            courseBaseRepository.deleteById(id);
        }
        List<CoursePic> pics = coursePicRepository.findByCourseid(id);
        if (pics.size() > 0) {
            coursePicRepository.deleteByCourseid(id);
        }
        Optional<CourseMarket> market = courseMarketRepository.findById(id);
        if (market.isPresent()) {
            courseMarketRepository.deleteById(id);
        }
        Optional<CoursePub> optionalCoursePub = coursePubRepository.findById(id);
        if (optionalCoursePub.isPresent()) {
            coursePubRepository.deleteById(id);
        }
        List<Teachplan> teachplanList = teachplanRepository.findByCourseid(id);
        if (teachplanList.size() > 0) {
            teachplanRepository.deleteByCourseid(id);
        }
        List<TeachplanMedia> media = teachplanMediaRepository.findByCourseid(id);
        if (media.size() > 0) {
            teachplanMediaRepository.deleteByCourseid(id);
        }
        List<TeachplanMediaPub> teachplanMediaPubs = teachplanMediaPubRepository.findByCourseid(id);
        if (teachplanMediaPubs.size() > 0) {
            teachplanMediaPubRepository.deleteByCourseid(id);
        }
        //删除ES的相应数据
        seachClient.delete(id);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 保存课程计划与媒资视频的关联信息
     *
     * @param teachplanMedia
     * @return
     */
    public ResponseResult saveMedia(TeachplanMedia teachplanMedia) {
        if (teachplanMedia == null || StringUtils.isEmpty(teachplanMedia.getTeachplanId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //校验课程计划是否是3级
        //课程计划
        String teachplanId = teachplanMedia.getTeachplanId();
        //查询到课程计划
        Optional<Teachplan> optional = teachplanRepository.findById(teachplanId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //查询到教学计划
        Teachplan teachplan = optional.get();
        //取出等级
        String grade = teachplan.getGrade();
        if (StringUtils.isEmpty(grade) || !grade.equals("3")) {
            //只允许选择第三级的课程计划关联视频
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }
        //查询teachplanMedia
        Optional<TeachplanMedia> optionalTeachplanMedia = teachplanMediaRepository.findById(teachplanId);
        TeachplanMedia one = null;
        if (optionalTeachplanMedia.isPresent()) {
            one = optionalTeachplanMedia.get();
        } else {
            one = new TeachplanMedia();
        }
        //将one保存到数据库
        one.setCourseid(teachplan.getCourseid());//课程id
        one.setMediaId(teachplanMedia.getMediaId());//媒资文件的id
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());//媒资文件的原始名称
        one.setMediaUrl(teachplanMedia.getMediaUrl());//媒资文件的url
        one.setTeachplanId(teachplanId);
        teachplanMediaRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);

    }

    /**
     * 课程发布前判断课程计划是否全部关联媒资
     *
     * @param courseId
     * @return
     */
    public ResponseResult rejectMediaIsAll(String courseId) {
        //查询课程
        List<Teachplan> teachplans = teachplanRepository.findByCourseid(courseId);
        for (Teachplan teachplan : teachplans) {
            //取出等级
            String grade = teachplan.getGrade();
            if (grade.equals("3")) {
                String teachplanId = teachplan.getId();
                Optional<TeachplanMedia> optional = teachplanMediaRepository.findById(teachplanId);
                if (!optional.isPresent()) {
                    return new ResponseResult(CourseCode.COURSE_MEDIA_NOTALL);
                }
            } else {
                continue;
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 删除媒资:删除media和media_pub中的数据
     * @param mediaId
     * @return
     */
    @Transactional
    public ResponseResult deleteMedia(String mediaId) {
        teachplanMediaRepository.deleteByMediaId(mediaId);
        teachplanMediaPubRepository.deleteByMediaId(mediaId);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
