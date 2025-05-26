package com.rabbiter.fm.service.impl;

import com.rabbiter.fm.dao.IdleItemDao;
import com.rabbiter.fm.dao.OrderDao;
import com.rabbiter.fm.dao.ReviewDao;
import com.rabbiter.fm.dao.UserDao;
import com.rabbiter.fm.model.OrderModel;
import com.rabbiter.fm.model.ReviewModel;
import com.rabbiter.fm.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {
    
    @Resource
    private ReviewDao reviewDao;
    
    @Resource
    private OrderDao orderDao;
    
    @Resource
    private UserDao userDao;
    
    @Resource
    private IdleItemDao idleItemDao;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addReview(ReviewModel reviewModel) {
        // 检查评价是否已存在
        ReviewModel existingReview = reviewDao.selectByOrderId(reviewModel.getOrderId());
        if (existingReview != null) {
            return false;
        }
        
        // 默认评价状态为正常
        if (reviewModel.getStatus() == null) {
            reviewModel.setStatus((byte) 1);
        }
        
        // 插入评价
        int result = reviewDao.insert(reviewModel);
        
        if (result > 0) {
            // 更新商品的平均评分和评价数量
            updateIdleRating(reviewModel.getIdleId());
            return true;
        }
        
        return false;
    }
    
    @Override
    public ReviewModel getReview(Long id) {
        return reviewDao.selectByPrimaryKey(id);
    }
    
    @Override
    public ReviewModel getReviewByOrderId(Long orderId) {
        ReviewModel reviewModel = reviewDao.selectByOrderId(orderId);
        if (reviewModel != null) {
            reviewModel.setUser(userDao.selectByPrimaryKey(reviewModel.getUserId()));
        }
        return reviewModel;
    }
    
    @Override
    public List<ReviewModel> getIdleReviews(Long idleId) {
        List<ReviewModel> reviewList = reviewDao.getIdleReviews(idleId);
        if (reviewList.size() > 0) {
            List<Long> userIds = new ArrayList<>();
            for (ReviewModel review : reviewList) {
                userIds.add(review.getUserId());
            }
            
            // 批量查询用户信息
            Map<Long, Object> userMap = new HashMap<>();
            userDao.findUserByList(userIds).forEach(user -> userMap.put(user.getId(), user));
            
            // 设置用户信息
            reviewList.forEach(review -> review.setUser(userDao.selectByPrimaryKey(review.getUserId())));
        }
        
        return reviewList;
    }
    
    @Override
    public List<ReviewModel> getUserReviews(Long userId) {
        List<ReviewModel> reviewList = reviewDao.getUserReviews(userId);
        
        if (reviewList.size() > 0) {
            // 填充商品信息
            for (ReviewModel review : reviewList) {
                review.setIdleItem(idleItemDao.selectByPrimaryKey(review.getIdleId()));
            }
        }
        
        return reviewList;
    }
    
    @Override
    public List<ReviewModel> getSellerReceivedReviews(Long sellerId) {
        List<ReviewModel> reviewList = reviewDao.getSellerReceivedReviews(sellerId);
        
        if (reviewList.size() > 0) {
            // 填充商品信息和买家信息
            for (ReviewModel review : reviewList) {
                review.setIdleItem(idleItemDao.selectByPrimaryKey(review.getIdleId()));
                review.setUser(userDao.selectByPrimaryKey(review.getUserId()));
            }
        }
        
        return reviewList;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateReview(ReviewModel reviewModel) {
        // 更新评价信息
        int result = reviewDao.updateByPrimaryKeySelective(reviewModel);
        
        if (result > 0 && reviewModel.getRating() != null) {
            // 如果评分发生变化，重新计算商品的平均评分
            updateIdleRating(reviewModel.getIdleId());
            return true;
        }
        
        return result > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteReview(Long id) {
        ReviewModel review = reviewDao.selectByPrimaryKey(id);
        if (review == null) {
            return false;
        }
        
        Long idleId = review.getIdleId();
        int result = reviewDao.deleteByPrimaryKey(id);
        
        if (result > 0) {
            // 重新计算商品的平均评分
            updateIdleRating(idleId);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean checkReviewPermission(Long userId, Long orderId) {
        OrderModel order = orderDao.selectByPrimaryKey(orderId);
        
        // 检查订单是否存在且属于该用户
        if (order != null && order.getUserId().equals(userId)) {
            // 检查订单是否已完成（状态为3:已完成）
            return order.getOrderStatus() == 3;
        }
        
        return false;
    }
    
    /**
     * 更新商品的平均评分和评价数量
     * @param idleId 商品ID
     */
    private void updateIdleRating(Long idleId) {
        // 计算平均评分
        Double avgRating = reviewDao.calculateAvgRating(idleId);
        // 获取评价数量
        int reviewCount = reviewDao.countIdleReviews(idleId);
        
        // 更新商品的平均评分和评价数量
        idleItemDao.updateAvgRating(idleId, avgRating, reviewCount);
    }
} 