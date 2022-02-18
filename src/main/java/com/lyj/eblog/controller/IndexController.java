package com.lyj.eblog.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyj.eblog.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController extends BaseController {

    @RequestMapping({"", "/", "/index"})
    public String index() {

        /*参数：1分页信息 2分类 3用户 4置顶 5精选 6排序*/
        IPage results = postService.paging(getPage(), null, null, null, null, "created");
        /*分页的数据*/
        request.setAttribute("pageData", results);

        /*默认首页的id是0*/
        request.setAttribute("currentCategoryId", 0);
        return "index";
    }

    @RequestMapping("/search")
    public String search(String q) {

        IPage pageDate = searchService.search(getPage(), q);

        request.setAttribute("q", q);
        request.setAttribute("pageData", pageDate);
        return "search";
    }

}
