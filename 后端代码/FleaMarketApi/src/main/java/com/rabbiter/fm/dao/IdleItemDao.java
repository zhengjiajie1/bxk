package com.rabbiter.fm.dao;

import com.rabbiter.fm.model.IdleItemModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IdleItemDao {
    int deleteByPrimaryKey(Long id);

    int insert(IdleItemModel record);

    int insertSelective(IdleItemModel record);

    IdleItemModel selectByPrimaryKey(Long id);

    List<IdleItemModel> getAllIdleItem(Long userId);

    int countIdleItem(String findValue);

    int countIdleItemByLable(int idleLabel);

    int countIdleItemByStatus(int status);

    List<IdleItemModel> findIdleItem(String findValue, int begin, int nums);

    List<IdleItemModel> findIdleItemByLable(int idleLabel, int begin, int nums);

    List<IdleItemModel> getIdleItemByStatus(@Param("status") int status, @Param("offset") int offset, @Param("limit") int limit);

    int updateByPrimaryKeySelective(IdleItemModel record);

    int updateByPrimaryKey(IdleItemModel record);

    List<IdleItemModel> findIdleByList(List<Long> idList);
    
    /**
     * 减少商品库存
     * @param idleId 商品ID
     * @param quantity 减少的数量
     * @return 影响的行数
     */
    int decreaseStock(@Param("idleId") Long idleId, @Param("quantity") Integer quantity);
    
    /**
     * 增加商品销量
     * @param idleId 商品ID
     * @param quantity 增加的数量
     * @return 影响的行数
     */
    int increaseSoldCount(@Param("idleId") Long idleId, @Param("quantity") Integer quantity);
    
    /**
     * 更新商品的平均评分和评价数量
     * @param idleId 商品ID
     * @param avgRating 平均评分
     * @param reviewCount 评价数量
     * @return 影响的行数
     */
    int updateAvgRating(@Param("idleId") Long idleId, @Param("avgRating") Double avgRating, @Param("reviewCount") Integer reviewCount);

    // 新增：获取指定用户已上架商品相关方法
    /**
     * 获取指定用户已上架的商品列表（分页）
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 商品列表
     */
    List<IdleItemModel> getUserPublishedItems(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计指定用户已上架商品的数量
     * @param userId 用户ID
     * @return 商品数量
     */
    int countUserPublishedItems(@Param("userId") Long userId);
}