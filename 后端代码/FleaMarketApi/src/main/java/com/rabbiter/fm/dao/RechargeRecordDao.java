package com.rabbiter.fm.dao;

import com.rabbiter.fm.model.RechargeRecordModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RechargeRecordDao {
    int deleteByPrimaryKey(Long id);

    int insert(RechargeRecordModel record);

    int insertSelective(RechargeRecordModel record);

    RechargeRecordModel selectByPrimaryKey(Long id);

    List<RechargeRecordModel> getRecordsByUserId(Long userId);

    int updateByPrimaryKeySelective(RechargeRecordModel record);

    int updateByPrimaryKey(RechargeRecordModel record);
} 