package com.rabbiter.fm.dao;

import com.rabbiter.fm.model.ReviewModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewDao {
    int deleteByPrimaryKey(Long id);
    
    int insert(ReviewModel record);
    
    int insertSelective(ReviewModel record);
    
    ReviewModel selectByPrimaryKey(Long id);
    
    ReviewModel selectByOrderId(Long orderId);
    
    int updateByPrimaryKeySelective(ReviewModel record);
    
    int updateByPrimaryKey(ReviewModel record);
    
    // 获取指定商品的所有评价
    List<ReviewModel> getIdleReviews(Long idleId);
    
    // 获取用户的所有评价
    List<ReviewModel> getUserReviews(Long userId);
    
    // 获取卖家收到的所有评价
    List<ReviewModel> getSellerReceivedReviews(Long sellerId);
    
    // 计算商品的平均评分
    Double calculateAvgRating(Long idleId);
    
    // 获取商品的评价数量
    int countIdleReviews(Long idleId);
} 