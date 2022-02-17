package com.lyj.eblog.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyj.eblog.Vo.UserMessageVo;
import com.lyj.eblog.pojo.UserMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author LiuYunJie
 * @since 2022-02-08
 */

@Component
public interface UserMessageMapper extends BaseMapper<UserMessage> {

    IPage<UserMessageVo> selectMessage(Page page,
                       @Param(Constants.WRAPPER) QueryWrapper<UserMessage> wrapper);

/*    @Transactional*/
    @Update("update m_user_message set status = 1 ${ew.customSqlSegment}")
    void updateToReaded(QueryWrapper<UserMessage> wrapper);
}
