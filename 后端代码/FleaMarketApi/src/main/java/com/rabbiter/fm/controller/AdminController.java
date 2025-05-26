package com.rabbiter.fm.controller;

import com.rabbiter.fm.service.IdleItemService;
import com.rabbiter.fm.service.UserService;
import com.rabbiter.fm.common.enums.ErrorMsg;
import com.rabbiter.fm.model.AdminModel;
import com.rabbiter.fm.model.IdleItemModel;
import com.rabbiter.fm.model.UserModel;
import com.rabbiter.fm.service.AdminService;
import com.rabbiter.fm.service.OrderService;
import com.rabbiter.fm.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@CrossOrigin
@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private IdleItemService idleItemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("login")
    public ResultVo login(@RequestParam("accountNumber") @NotNull @NotEmpty String accountNumber,
                          @RequestParam("adminPassword") @NotNull @NotEmpty String adminPassword,
                          HttpSession session){
        AdminModel adminModel=adminService.login(accountNumber,adminPassword);
        if (null == adminModel) {
            return ResultVo.fail(ErrorMsg.EMAIL_LOGIN_ERROR);
        }
        session.setAttribute("admin",adminModel);
        return ResultVo.success(adminModel);
    }

    @GetMapping("loginOut")
    public ResultVo loginOut( HttpSession session){
        session.removeAttribute("admin");
        return ResultVo.success();
    }

    @GetMapping("list")
    public ResultVo getAdminList(HttpSession session,
                                 @RequestParam(value = "page",required = false) Integer page,
                                 @RequestParam(value = "nums",required = false) Integer nums){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        return ResultVo.success(adminService.getAdminList(p,n));
    }

    @PostMapping("add")
    public ResultVo addAdmin(HttpSession session,
                             @RequestBody AdminModel adminModel){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        if(adminService.addAdmin(adminModel)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.PARAM_ERROR);
    }

    @GetMapping("idleList")
    public ResultVo idleList(HttpSession session,
                             @RequestParam("status") @NotNull @NotEmpty Integer status,
                             @RequestParam(value = "page",required = false) Integer page,
                             @RequestParam(value = "nums",required = false) Integer nums){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        return ResultVo.success(idleItemService.adminGetIdleList(status,p,n));
    }

    @GetMapping("updateIdleStatus")
    public ResultVo updateIdleStatus(HttpSession session,
                                     @RequestParam("id") @NotNull @NotEmpty Long id,
                                     @RequestParam("status") @NotNull @NotEmpty Integer status
                                     ){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        IdleItemModel idleItemModel=new IdleItemModel();
        idleItemModel.setId(id);
        idleItemModel.setIdleStatus(status.byteValue());
        if(idleItemService.updateIdleItem(idleItemModel)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("orderList")
    public ResultVo orderList(HttpSession session,
                              @RequestParam(value = "page",required = false) Integer page,
                              @RequestParam(value = "nums",required = false) Integer nums){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        return ResultVo.success(orderService.getAllOrder(p,n));
    }

    @GetMapping("deleteOrder")
    public ResultVo deleteOrder(HttpSession session,
                              @RequestParam("id") @NotNull @NotEmpty Long id){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        if(orderService.deleteOrder(id)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("userList")
    public ResultVo userList(HttpSession session,
                             @RequestParam(value = "page",required = false) Integer page,
                             @RequestParam(value = "nums",required = false) Integer nums,
                             @RequestParam("status") @NotNull @NotEmpty Integer status){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        return ResultVo.success(userService.getUserByStatus(status,p,n));
    }

    @GetMapping("updateUserStatus")
    public ResultVo updateUserStatus(HttpSession session,
                                     @RequestParam("id") @NotNull @NotEmpty Long id,
                                     @RequestParam("status") @NotNull @NotEmpty Integer status){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        UserModel userModel=new UserModel();
        userModel.setId(id);
        userModel.setUserStatus(status.byteValue());
        if(userService.updateUserInfo(userModel))
            return ResultVo.success();
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("getPendingUsers")
    public ResultVo getPendingUsers(HttpSession session,
                             @RequestParam(value = "page",required = false) Integer page,
                             @RequestParam(value = "nums",required = false) Integer nums){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        // 获取状态为2(待审核)的用户
        return ResultVo.success(userService.getUserByStatus(2, p, n));
    }

    @GetMapping("approveUser")
    public ResultVo approveUser(HttpSession session,
                                @RequestParam("id") @NotNull @NotEmpty Long id,
                                @RequestParam("approved") @NotNull Boolean approved){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        if (approved) {
            // 批准申请，将用户状态设为0(正常)
            UserModel userModel = new UserModel();
            userModel.setId(id);
            userModel.setUserStatus((byte)0);
            
            if(userService.updateUserInfo(userModel)) {
                return ResultVo.success();
            }
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        } else {
            // 拒绝申请，直接删除用户数据
            if(userService.deleteUser(id)) {
                return ResultVo.success();
            }
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }

    // 新增：商品审核功能接口
    /**
     * 获取待审核商品列表
     */
    @GetMapping("getPendingGoods")
    public ResultVo getPendingGoods(HttpSession session,
                                   @RequestParam(value = "page",required = false) Integer page,
                                   @RequestParam(value = "nums",required = false) Integer nums){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        // 获取状态为3(待审核)的商品
        return ResultVo.success(idleItemService.adminGetIdleList(3, p, n));
    }

    /**
     * 审核商品
     * @param id 商品ID
     * @param approved 是否通过审核
     */
    @GetMapping("approveGoods")
    public ResultVo approveGoods(HttpSession session,
                                @RequestParam("id") @NotNull @NotEmpty Long id,
                                @RequestParam("approved") @NotNull Boolean approved){
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        if (approved) {
            // 批准审核，将商品状态设为1(上架)
            IdleItemModel idleItemModel = new IdleItemModel();
            idleItemModel.setId(id);
            idleItemModel.setIdleStatus((byte)1);
            
            if(idleItemService.updateIdleItem(idleItemModel)) {
                return ResultVo.success();
            }
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        } else {
            // 拒绝审核，直接删除商品数据
            if(idleItemService.deleteIdleItem(id)) {
                return ResultVo.success();
            }
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }

    // 新增：用户等级管理接口
    /**
     * 更新用户等级（管理员功能）
     * @param session 会话
     * @param userId 用户ID
     * @param userLevel 新的用户等级（1-5）
     * @return 操作结果
     */
    @PostMapping("updateUserLevel")
    public ResultVo updateUserLevel(HttpSession session,
                                   @RequestParam("userId") @NotNull @NotEmpty Long userId,
                                   @RequestParam("userLevel") @NotNull @NotEmpty Byte userLevel) {
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        // 验证等级范围
        if (userLevel < 1 || userLevel > 5) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("用户等级必须在1-5之间"));
        }
        
        if (userService.updateUserLevel(userId, userLevel)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 获取用户详细信息（包含等级）
     * @param session 会话
     * @param userId 用户ID
     * @return 用户详细信息
     */
    @GetMapping("getUserDetail")
    public ResultVo getUserDetail(HttpSession session,
                                 @RequestParam("userId") @NotNull @NotEmpty Long userId) {
        if(session.getAttribute("admin")==null){
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        UserModel user = userService.getUser(userId);
        if (user == null) {
            return ResultVo.fail(ErrorMsg.ACCOUNT_NOT_EXIT);
        }
        
        return ResultVo.success(user);
    }

}
