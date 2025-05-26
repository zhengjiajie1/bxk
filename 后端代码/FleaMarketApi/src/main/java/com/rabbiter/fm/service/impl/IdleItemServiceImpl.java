package com.rabbiter.fm.service.impl;

import com.rabbiter.fm.service.IdleItemService;
import com.rabbiter.fm.dao.IdleItemDao;
import com.rabbiter.fm.dao.UserDao;
import com.rabbiter.fm.model.IdleItemModel;
import com.rabbiter.fm.model.UserModel;
import com.rabbiter.fm.vo.PageVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IdleItemServiceImpl implements IdleItemService {

    @Resource
    private IdleItemDao idleItemDao;

    @Resource
    private UserDao userDao;

    /**
     * 发布闲置
     * @param idleItemModel
     * @return
     */
    public boolean addIdleItem(IdleItemModel idleItemModel) {
        return idleItemDao.insert(idleItemModel) == 1;
    }

    /**
     * 查询闲置信息，同时查出发布者的信息
     * @param id
     * @return
     */
    public IdleItemModel getIdleItem(Long id) {
        IdleItemModel idleItemModel=idleItemDao.selectByPrimaryKey(id);
        if(idleItemModel!=null){
            idleItemModel.setUser(userDao.selectByPrimaryKey(idleItemModel.getUserId()));
        }
        return idleItemModel;
    }

    /**
     * 查询用户发布的所有闲置
     * user_id建索引
     * @param userId
     * @return
     */
    public List<IdleItemModel> getAllIdelItem(Long userId) {
        return idleItemDao.getAllIdleItem(userId);
    }

    /**
     * 搜索，分页
     * 同时查出闲置发布者的信息
     * @param findValue
     * @param page
     * @param nums
     * @return
     */
    public PageVo<IdleItemModel> findIdleItem(String findValue, int page, int nums) {
        List<IdleItemModel> list=idleItemDao.findIdleItem(findValue, (page - 1) * nums, nums);
        if(list.size()>0){
            List<Long> idList=new ArrayList<>();
            for(IdleItemModel i:list){
                idList.add(i.getUserId());
            }
            List<UserModel> userList=userDao.findUserByList(idList);
            Map<Long,UserModel> map=new HashMap<>();
            for(UserModel user:userList){
                map.put(user.getId(),user);
            }
            for(IdleItemModel i:list){
                i.setUser(map.get(i.getUserId()));
            }
        }
        int count=idleItemDao.countIdleItem(findValue);
        return new PageVo<>(list,count);
    }

    /**
     * 分类查询，分页
     * 同时查出闲置发布者的信息，代码结构与上面的类似，可封装优化，或改为join查询
     * @param idleLabel
     * @param page
     * @param nums
     * @return
     */
    public PageVo<IdleItemModel> findIdleItemByLable(int idleLabel, int page, int nums) {
        List<IdleItemModel> list=idleItemDao.findIdleItemByLable(idleLabel, (page - 1) * nums, nums);
        if(list.size()>0){
            List<Long> idList=new ArrayList<>();
            for(IdleItemModel i:list){
                idList.add(i.getUserId());
            }
            List<UserModel> userList=userDao.findUserByList(idList);
            Map<Long,UserModel> map=new HashMap<>();
            for(UserModel user:userList){
                map.put(user.getId(),user);
            }
            for(IdleItemModel i:list){
                i.setUser(map.get(i.getUserId()));
            }
        }
        int count=idleItemDao.countIdleItemByLable(idleLabel);
        return new PageVo<>(list,count);
    }

    /**
     * 更新闲置信息
     * @param idleItemModel
     * @return
     */
    public boolean updateIdleItem(IdleItemModel idleItemModel){
        return idleItemDao.updateByPrimaryKeySelective(idleItemModel)==1;
    }

    public PageVo<IdleItemModel> adminGetIdleList(int status, int page, int nums) {
        List<IdleItemModel> list=idleItemDao.getIdleItemByStatus(status, (page - 1) * nums, nums);
        if(list.size()>0){
            List<Long> idList=new ArrayList<>();
            for(IdleItemModel i:list){
                idList.add(i.getUserId());
            }
            List<UserModel> userList=userDao.findUserByList(idList);
            Map<Long,UserModel> map=new HashMap<>();
            for(UserModel user:userList){
                map.put(user.getId(),user);
            }
            for(IdleItemModel i:list){
                i.setUser(map.get(i.getUserId()));
            }
        }
        int count=idleItemDao.countIdleItemByStatus(status);
        return new PageVo<>(list,count);
    }

    // 新增：删除商品实现
    /**
     * 删除商品
     * @param id 商品ID
     * @return 是否删除成功
     */
    public boolean deleteIdleItem(Long id) {
        return idleItemDao.deleteByPrimaryKey(id) == 1;
    }

    // 新增：获取指定用户已上架商品列表实现
    /**
     * 获取指定用户的已上架商品列表（分页）
     * @param userId 用户ID
     * @param page 页码
     * @param nums 每页数量
     * @return 分页的商品列表
     */
    public PageVo<IdleItemModel> getUserPublishedItems(Long userId, int page, int nums) {
        // 获取指定用户状态为1(已上架)的商品，按发布时间倒序
        List<IdleItemModel> list = idleItemDao.getUserPublishedItems(userId, (page - 1) * nums, nums);
        
        // 设置发布者信息
        if (list.size() > 0) {
            UserModel user = userDao.selectByPrimaryKey(userId);
            for (IdleItemModel item : list) {
                item.setUser(user);
            }
        }
        
        int count = idleItemDao.countUserPublishedItems(userId);
        return new PageVo<>(list, count);
    }
}
