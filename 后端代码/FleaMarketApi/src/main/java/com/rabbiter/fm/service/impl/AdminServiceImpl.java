package com.rabbiter.fm.service.impl;

import com.rabbiter.fm.dao.AdminDao;
import com.rabbiter.fm.model.AdminModel;
import com.rabbiter.fm.service.AdminService;
import com.rabbiter.fm.vo.PageVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminDao adminDao;

    public AdminModel login(String accountNumber, String adminPassword){
        return adminDao.login(accountNumber,adminPassword);
    }

    public PageVo<AdminModel> getAdminList(int page, int nums){
        List<AdminModel> list=adminDao.getList((page-1)*nums,nums);
        int count=adminDao.getCount();
        return new PageVo<>(list,count);
    }

    public boolean addAdmin(AdminModel adminModel){
        return adminDao.insert(adminModel)==1;
    }
}
