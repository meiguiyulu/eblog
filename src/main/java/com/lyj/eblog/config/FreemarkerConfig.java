package com.lyj.eblog.config;

import com.jagregory.shiro.freemarker.ShiroTags;
import com.lyj.eblog.templates.HotsTemplate;
import com.lyj.eblog.templates.PostsTemplate;
import com.lyj.eblog.templates.TimeAgoMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class FreemarkerConfig {

    @Autowired
    private freemarker.template.Configuration configuration;

    @Autowired
    private PostsTemplate postsTemplate;

    @Autowired
    private HotsTemplate hotsTemplate;


    @PostConstruct
    public void setUp() {
        configuration.setSharedVariable("timeAgo", new TimeAgoMethod());
        configuration.setSharedVariable("posts", postsTemplate);
        configuration.setSharedVariable("hots", hotsTemplate);
        configuration.setSharedVariable("shiro", new ShiroTags());
    }

}

