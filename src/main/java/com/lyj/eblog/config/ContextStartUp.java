package com.lyj.eblog.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lyj.eblog.pojo.Category;
import com.lyj.eblog.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * 实现功能：
 * 项目启动的时候便运行以下代码: header-panel中的类别
 *
 */
@Component
public class ContextStartUp implements ApplicationRunner, ServletContextAware {

    @Autowired
    ICategoryService categoryService;

    ServletContext servletContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<Category> categories = categoryService.list(new QueryWrapper<Category>()
                .eq("status", 0)
        );
        servletContext.setAttribute("categories", categories);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
