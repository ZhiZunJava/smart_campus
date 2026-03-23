package com.smart.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.smart.system.domain.SysNoticeRead;

public interface SysNoticeReadMapper
{
    SysNoticeRead selectByNoticeIdAndUserId(@Param("noticeId") Long noticeId, @Param("userId") Long userId);

    int insertOrUpdateNoticeRead(SysNoticeRead record);

    List<Long> selectReadNoticeIdsByUserId(@Param("userId") Long userId);
}
