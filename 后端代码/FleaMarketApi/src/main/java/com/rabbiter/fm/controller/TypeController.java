package com.rabbiter.fm.controller;

import com.rabbiter.fm.service.TypeService;
import com.rabbiter.fm.vo.ResultVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/type")
public class TypeController {
    @Resource
    private TypeService typeService;

    @GetMapping("listByCondition")
    public ResultVo listByCondition(Integer begin, Integer size) {
        return ResultVo.success(typeService.listByCondition(begin, size));
    }


}
