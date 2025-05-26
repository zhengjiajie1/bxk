package com.rabbiter.fm.service;

import com.rabbiter.fm.model.AdminModel;
import com.rabbiter.fm.vo.PageVo;

public interface AdminService {

    AdminModel login(String accountNumber, String adminPassword);

    PageVo<AdminModel> getAdminList(int page, int nums);

    boolean addAdmin(AdminModel adminModel);


}
