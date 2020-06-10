package com.yok.ucenter.service;

import com.yok.framework.domain.ucenter.XcCompanyUser;
import com.yok.framework.domain.ucenter.XcMenu;
import com.yok.framework.domain.ucenter.XcUser;
import com.yok.framework.domain.ucenter.XcUserRole;
import com.yok.framework.domain.ucenter.ext.XcUserExt;
import com.yok.framework.domain.ucenter.request.UpdateMsgRequest;
import com.yok.framework.domain.ucenter.request.UserRequest;
import com.yok.framework.domain.ucenter.response.UcenterCode;
import com.yok.framework.exception.ExceptionCast;
import com.yok.framework.model.response.CommonCode;
import com.yok.framework.model.response.ResponseResult;
import com.yok.ucenter.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UcenterService {
    @Autowired
    XcCompanyUserRepository xcCompanyUserRepository;
    @Autowired
    XcUserRepository xcUserRepository;
    @Autowired
    XcUserRoleMapper xcUserRoleMapper;

    @Autowired
    XcCompanyUserMapper xcCompanyUserMapper;
    @Autowired
    XcMenuMapper xcMenuMapper;

    /**
     * 根据username查询用户信息(XcUser和XcUserCompany)
     *
     * @param username
     * @return
     */
    public XcUserExt getUserExt(String username) {
        //根据username查询用户信息
        XcUser xcUser = this.getXcUserByUsername(username);
        if (xcUser == null) {
            return null;
        }
        String userId = xcUser.getId();
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(userId);
        //根据userid查询companyid
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(userId);
        String companyId = null;
        if (xcCompanyUser != null) {
            companyId = xcCompanyUser.getCompanyId();
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);
        xcUserExt.setCompanyId(companyId);
        xcUserExt.setPermissions(xcMenus);
        return xcUserExt;
    }

    /**
     * 根据username查询用户信息
     *
     * @param username
     * @return
     */
    private XcUser getXcUserByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        XcUser xcUser = xcUserRepository.findByUsername(username);
        return xcUser;
    }

    public ResponseResult register(XcUser user) {
        String username = user.getUsername();
        XcUser user1 = xcUserRepository.findByUsername(username);
        if (user1 != null) {
            return new ResponseResult(UcenterCode.UCENTER_CREATEUSER_ERROR);
        }
        //密码加密
        String password = user.getPassword();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(password);
        user.setPassword(encode);
        user.setStatus("1");
        user.setCreateTime(new Date());
        user = xcUserRepository.save(user);
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(user.getId());
        xcUserRole.setUserId(user.getId());
        String utype = user.getUtype();
        if (utype.equals("101003")) {
            xcUserRole.setRoleId("6");
        } else if (utype.equals("101002")) {
            xcUserRole.setRoleId("20");
        } else {
            xcUserRole.setRoleId("17");
        }
        xcUserRole.setCreateTime(new Date());
        xcUserRoleMapper.save(xcUserRole);
        XcCompanyUser xcCompanyUser = new XcCompanyUser();
        xcCompanyUser.setId(user.getId());
        xcCompanyUser.setUserId(user.getId());
        xcCompanyUser.setCompanyId("1");
        xcCompanyUserMapper.save(xcCompanyUser);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 获取用户个人信息
     *
     * @param userRequset
     * @return
     */
    public XcUser getUserMag(UserRequest userRequset) {
        if (userRequset == null || StringUtils.isEmpty(userRequset.getUserId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String userId = userRequset.getUserId();
        Optional<XcUser> optional = xcUserRepository.findById(userId);
        if (optional.isPresent()) {
            XcUser user = optional.get();
            return user;
        }
        return null;
    }

    /**
     * 修改用户信息
     *
     * @param msgRequest
     * @return
     */
    public ResponseResult updateUserMsg(UpdateMsgRequest msgRequest) {
        if (msgRequest == null
                || StringUtils.isEmpty(msgRequest.getUserId())
                || StringUtils.isEmpty(msgRequest.getValue())
                || StringUtils.isEmpty(msgRequest.getFlag())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String flag = msgRequest.getFlag();
        String userId = msgRequest.getUserId();
        String value = msgRequest.getValue();
        Optional<XcUser> optional = xcUserRepository.findById(userId);
        if (optional.isPresent()) {
            XcUser xcUser = optional.get();
            //修改昵称
            if (flag.equals("name")) {
                xcUser.setName(value);
            } else if (flag.equals("password")) {
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                String encode = bCryptPasswordEncoder.encode(value);
                xcUser.setPassword(encode);
            } else if (flag.equals("phone")) {
                xcUser.setPhone(value);
            } else if (flag.equals("email")) {
                xcUser.setEmail(value);
            } else if (flag.equals("qq")) {
                xcUser.setQq(value);
            } else {
                return new ResponseResult(UcenterCode.EDIT_USERMSG_FAIL);
            }
            xcUserRepository.save(xcUser);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(UcenterCode.EDIT_USERMSG_FAIL);
    }

    /**
     * 根据多个userid查询用户信息
     * @param userRequset
     * @return
     */
    public Map<String, XcUser> findUserById(UserRequest userRequset) {
        if (userRequset == null || StringUtils.isEmpty(userRequset.getUserId())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String userIds = userRequset.getUserId();
        String[] userIdArray = userIds.split(",");
        Set<String> set = new HashSet<>();
        for(String userId : userIdArray){
            set.add(userId);
        }
        Map<String, XcUser> resultMap = new HashMap<>();
        for(String userId : set){
            Optional<XcUser> optional = xcUserRepository.findById(userId);
            if (optional.isPresent()){
                XcUser user = optional.get();
                resultMap.put(userId,user);
            }
        }
        return resultMap;
    }
}
