package com.rabbiter.fm.dao;

import com.rabbiter.fm.model.TypeModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TypeDao {

    List<TypeModel> listByCondition(@Param("begin") int begin, @Param("nums") int nums);
}
