package com.lyj.eblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyj.eblog.pojo.UserMessage;
import com.lyj.eblog.mapper.UserMessageMapper;
import com.lyj.eblog.service.IUserMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LiuYunJie
 * @since 2022-02-08
 */
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements IUserMessageService {

    @Autowired
    UserMessageMapper userMessageMapper;

    @Override
    public IPage paging(Page page, QueryWrapper<UserMessage> wrapper) {
        return userMessageMapper.selectMessage(page, wrapper);
    }

    @Override
    public void updateToReaded(List<Long> ids) {
        if (ids.isEmpty()) return;

        userMessageMapper.updateToReaded(new QueryWrapper<UserMessage>()
                .in("id", ids)
        );
    }
}
