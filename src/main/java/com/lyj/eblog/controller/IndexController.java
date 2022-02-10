package com.lyj.eblog.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController extends BaseController{

    @RequestMapping({"", "/", "/index"})
    public String index() {

        int pn = ServletRequestUtils.getIntParameter(request, "pn", 1);
        int size = ServletRequestUtils.getIntParameter(request, "size", 2);

        Page page = new Page(pn, size);
        /*参数：1分页信息 2分类 3用户 4置顶 5精选 6排序*/
        IPage results = postService.paging(page, null, null, null, null, "created");
        /*分页的数据*/
        request.setAttribute("pageData", results);

        /*默认首页的id是0*/
        request.setAttribute("currentCategoryId", 0);
        return "index";
    }

}
