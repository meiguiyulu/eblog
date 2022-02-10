package com.lyj.eblog.service.impl;

import com.lyj.eblog.pojo.UserMessage;
import com.lyj.eblog.mapper.UserMessageMapper;
import com.lyj.eblog.service.IUserMessageService;
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
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements IUserMessageService {

}
