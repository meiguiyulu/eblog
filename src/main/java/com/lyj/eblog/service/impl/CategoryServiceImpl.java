package com.lyj.eblog.service.impl;

import com.lyj.eblog.pojo.Category;
import com.lyj.eblog.mapper.CategoryMapper;
import com.lyj.eblog.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LiuYunJie
 * @since 2022-02-08
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}
