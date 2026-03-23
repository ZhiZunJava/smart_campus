package com.smart.system.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.SysNotice;
import com.smart.system.domain.SysNoticeRead;
import com.smart.system.mapper.SysNoticeMapper;
import com.smart.system.mapper.SysNoticeReadMapper;
import com.smart.system.service.ISysNoticeService;

/**
 * 公告 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class SysNoticeServiceImpl implements ISysNoticeService
{
    @Autowired
    private SysNoticeMapper noticeMapper;

    @Autowired
    private SysNoticeReadMapper noticeReadMapper;

    /**
     * 查询公告信息
     * 
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId)
    {
        return noticeMapper.selectNoticeById(noticeId);
    }

    /**
     * 查询公告列表
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice)
    {
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNotice notice)
    {
        normalizeNotice(notice);
        return noticeMapper.insertNotice(notice);
    }

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice)
    {
        normalizeNotice(notice);
        return noticeMapper.updateNotice(notice);
    }

    /**
     * 删除公告对象
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId)
    {
        return noticeMapper.deleteNoticeById(noticeId);
    }

    /**
     * 批量删除公告信息
     * 
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds)
    {
        return noticeMapper.deleteNoticeByIds(noticeIds);
    }

    @Override
    public List<SysNotice> selectPortalNoticeList(Long userId, String receiverScope, String bizCategory, String readFlag,
            String keyword, Integer limit)
    {
        return noticeMapper.selectPortalNoticeList(userId, receiverScope, bizCategory, readFlag, keyword, limit);
    }

    @Override
    public Map<String, Object> selectPortalNoticeStats(Long userId, String receiverScope)
    {
        return noticeMapper.selectPortalNoticeStats(userId, receiverScope);
    }

    @Override
    public int markNoticeRead(Long noticeId, Long userId, String operator)
    {
        if (noticeId == null || userId == null)
        {
            return 0;
        }
        SysNoticeRead record = new SysNoticeRead();
        record.setNoticeId(noticeId);
        record.setUserId(userId);
        record.setReadTime(new Date());
        record.setCreateBy(operator);
        record.setUpdateBy(operator);
        return noticeReadMapper.insertOrUpdateNoticeRead(record);
    }

    private void normalizeNotice(SysNotice notice)
    {
        if (notice == null)
        {
            return;
        }
        if (StringUtils.isEmpty(notice.getBizCategory()))
        {
            notice.setBizCategory("NOTICE");
        }
        if (StringUtils.isEmpty(notice.getReceiverScope()))
        {
            notice.setReceiverScope("ALL");
        }
        if (StringUtils.isEmpty(notice.getPinned()))
        {
            notice.setPinned("0");
        }
        if (notice.getPublishTime() == null)
        {
            notice.setPublishTime(new Date());
        }
        if (StringUtils.isEmpty(notice.getNoticeSummary()) && StringUtils.isNotEmpty(notice.getNoticeContent()))
        {
            String plain = notice.getNoticeContent().replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
            notice.setNoticeSummary(plain.length() > 120 ? plain.substring(0, 120) : plain);
        }
    }
}
