package com.rabbiter.fm.service.impl;

import com.rabbiter.fm.dao.IdleItemDao;
import com.rabbiter.fm.dao.TypeDao;
import com.rabbiter.fm.model.TypeModel;
import com.rabbiter.fm.service.TypeService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TypeServiceImpl implements TypeService {
    @Resource
    private TypeDao typeDao;

    @Resource
    private IdleItemDao idleItemDao;

    @Override
    public List<TypeModel> listByCondition(int begin, int nums) {
        return typeDao.listByCondition(begin, nums);
    }
}
