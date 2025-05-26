package com.rabbiter.fm.service;

import com.rabbiter.fm.model.IdleItemModel;
import com.rabbiter.fm.vo.PageVo;

import java.util.List;

public interface IdleItemService {

    /**
     * 发布闲置
     * @param idleItemModel
     * @return
     */
    boolean addIdleItem(IdleItemModel idleItemModel);

    /**
     * 获取某个闲置的信息
     * @param id
     * @return
     */
    IdleItemModel getIdleItem(Long id);

    /**
     * 获取某个用户的所有闲置信息
     * @param userId
     * @return
     */
    List<IdleItemModel> getAllIdelItem(Long userId);

    /**
     * 搜索闲置
     * @param findValue
     * @param page
     * @param nums
     * @return
     */
    PageVo<IdleItemModel> findIdleItem(String findValue, int page, int nums);

    /**
     * 按分类获取闲置，分页器
     * @param idleLabel
     * @param page
     * @param nums
     * @return
     */
    PageVo<IdleItemModel> findIdleItemByLable(int idleLabel, int page, int nums);

    /**
     * 更新闲置的状态信息
     * @param idleItemModel
     * @return
     */
    boolean updateIdleItem(IdleItemModel idleItemModel);

    PageVo<IdleItemModel> adminGetIdleList(int status, int page, int nums) ;

    /**
     * 删除商品
     * @param id 商品ID
     * @return 是否删除成功
     */
    boolean deleteIdleItem(Long id);

    /**
     * 获取指定用户的已上架商品列表（分页）
     * @param userId 用户ID
     * @param page 页码
     * @param nums 每页数量
     * @return 分页的商品列表
     */
    PageVo<IdleItemModel> getUserPublishedItems(Long userId, int page, int nums);
}
