package com.rabbiter.fm.service;

import com.rabbiter.fm.model.ReviewModel;

import java.util.List;

public interface ReviewService {
    
    /**
     * 添加商品评价
     * @param reviewModel 评价信息
     * @return 是否成功
     */
    boolean addReview(ReviewModel reviewModel);
    
    /**
     * 获取评价详情
     * @param id 评价ID
     * @return 评价信息
     */
    ReviewModel getReview(Long id);
    
    /**
     * 根据订单ID获取评价
     * @param orderId 订单ID
     * @return 评价信息
     */
    ReviewModel getReviewByOrderId(Long orderId);
    
    /**
     * 获取商品的所有评价
     * @param idleId 商品ID
     * @return 评价列表
     */
    List<ReviewModel> getIdleReviews(Long idleId);
    
    /**
     * 获取用户发布的所有评价
     * @param userId 用户ID
     * @return 评价列表
     */
    List<ReviewModel> getUserReviews(Long userId);
    
    /**
     * 获取用户作为卖家被评价的记录
     * @param sellerId 卖家用户ID
     * @return 评价列表
     */
    List<ReviewModel> getSellerReceivedReviews(Long sellerId);
    
    /**
     * 更新评价信息
     * @param reviewModel 评价信息
     * @return 是否成功
     */
    boolean updateReview(ReviewModel reviewModel);
    
    /**
     * 删除评价
     * @param id 评价ID
     * @return 是否成功
     */
    boolean deleteReview(Long id);
    
    /**
     * 检查用户是否有权限评价该订单
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 是否有权限
     */
    boolean checkReviewPermission(Long userId, Long orderId);
} 