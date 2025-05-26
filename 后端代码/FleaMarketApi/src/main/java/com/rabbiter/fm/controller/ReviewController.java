package com.rabbiter.fm.controller;

import com.rabbiter.fm.common.enums.ErrorMsg;
import com.rabbiter.fm.model.OrderModel;
import com.rabbiter.fm.model.ReviewModel;
import com.rabbiter.fm.service.OrderService;
import com.rabbiter.fm.service.ReviewService;
import com.rabbiter.fm.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@CrossOrigin
@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 添加评价
     * @param shUserId 用户ID
     * @param reviewModel 评价模型
     * @return 结果
     */
    @PostMapping("/add")
    public ResultVo addReview(@CookieValue("shUserId")
                              @NotNull(message = "登录异常 请重新登录")
                              @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                              @RequestBody ReviewModel reviewModel) {
        Long userId = Long.valueOf(shUserId);
        
        // 检查评价权限
        if (!reviewService.checkReviewPermission(userId, reviewModel.getOrderId())) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("您没有权限评价该订单或订单尚未完成"));
        }
        
        // 获取订单信息
        OrderModel order = orderService.getOrder(reviewModel.getOrderId());
        
        reviewModel.setUserId(userId);
        reviewModel.setIdleId(order.getIdleId());
        reviewModel.setCreateTime(new Date());
        
        // 检查评分范围
        Integer rating = reviewModel.getRating();
        if (rating == null || rating < 0 || rating > 5) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("评分必须在0-5之间"));
        }
        
        if (reviewService.addReview(reviewModel)) {
            return ResultVo.success(reviewModel);
        }
        
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
    
    /**
     * 获取商品的所有评价
     * @param idleId 商品ID
     * @return 评价列表
     */
    @GetMapping("/idle")
    public ResultVo getIdleReviews(@RequestParam Long idleId) {
        return ResultVo.success(reviewService.getIdleReviews(idleId));
    }
    
    /**
     * 获取用户的所有评价
     * @param shUserId 用户ID
     * @return 评价列表
     */
    @GetMapping("/user")
    public ResultVo getUserReviews(@CookieValue("shUserId")
                                   @NotNull(message = "登录异常 请重新登录")
                                   @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        return ResultVo.success(reviewService.getUserReviews(Long.valueOf(shUserId)));
    }
    
    /**
     * 获取卖家收到的评价
     * @param shUserId 用户ID
     * @return 评价列表
     */
    @GetMapping("/seller")
    public ResultVo getSellerReceivedReviews(@CookieValue("shUserId")
                                   @NotNull(message = "登录异常 请重新登录")
                                   @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        return ResultVo.success(reviewService.getSellerReceivedReviews(Long.valueOf(shUserId)));
    }
    
    /**
     * 根据订单ID获取评价
     * @param orderId 订单ID
     * @return 评价信息
     */
    @GetMapping("/order")
    public ResultVo getReviewByOrderId(@RequestParam Long orderId) {
        return ResultVo.success(reviewService.getReviewByOrderId(orderId));
    }
    
    /**
     * 更新评价
     * @param shUserId 用户ID
     * @param reviewModel 评价模型
     * @return 结果
     */
    @PostMapping("/update")
    public ResultVo updateReview(@CookieValue("shUserId")
                                 @NotNull(message = "登录异常 请重新登录")
                                 @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                 @RequestBody ReviewModel reviewModel) {
        Long userId = Long.valueOf(shUserId);
        
        // 检查评价是否存在
        ReviewModel existingReview = reviewService.getReview(reviewModel.getId());
        if (existingReview == null) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("评价不存在"));
        }
        
        // 检查是否是评价的所有者
        if (!existingReview.getUserId().equals(userId)) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("您没有权限修改该评价"));
        }
        
        // 不允许修改用户ID、订单ID、商品ID
        reviewModel.setUserId(null);
        reviewModel.setOrderId(null);
        reviewModel.setIdleId(null);
        
        // 检查评分范围
        Integer rating = reviewModel.getRating();
        if (rating != null && (rating < 0 || rating > 5)) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("评分必须在0-5之间"));
        }
        
        if (reviewService.updateReview(reviewModel)) {
            return ResultVo.success();
        }
        
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
    
    /**
     * 删除评价
     * @param shUserId 用户ID
     * @param id 评价ID
     * @return 结果
     */
    @PostMapping("/delete")
    public ResultVo deleteReview(@CookieValue("shUserId")
                                 @NotNull(message = "登录异常 请重新登录")
                                 @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                 @RequestParam Long id) {
        Long userId = Long.valueOf(shUserId);
        
        // 检查评价是否存在
        ReviewModel existingReview = reviewService.getReview(id);
        if (existingReview == null) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("评价不存在"));
        }
        
        // 检查是否是评价的所有者
        if (!existingReview.getUserId().equals(userId)) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR.withMsg("您没有权限删除该评价"));
        }
        
        if (reviewService.deleteReview(id)) {
            return ResultVo.success();
        }
        
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
} 