package com.rabbiter.fm.service;

import com.rabbiter.fm.model.TypeModel;

import java.util.List;

public interface TypeService {

    List<TypeModel> listByCondition(int begin, int nums);
}
