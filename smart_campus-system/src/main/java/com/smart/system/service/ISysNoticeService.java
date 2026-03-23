package com.smart.system.service;

import java.util.List;
import java.util.Map;
import com.smart.system.domain.SysNotice;

/**
 * 公告 服务层
 * 
 * @author ruoyi
 */
public interface ISysNoticeService
{
    /**
     * 查询公告信息
     * 
     * @param noticeId 公告ID
     * @return 公告信息
     */
    public SysNotice selectNoticeById(Long noticeId);

    /**
     * 查询公告列表
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    public List<SysNotice> selectNoticeList(SysNotice notice);

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    public int insertNotice(SysNotice notice);

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    public int updateNotice(SysNotice notice);

    /**
     * 删除公告信息
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    public int deleteNoticeById(Long noticeId);
    
    /**
     * 批量删除公告信息
     * 
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    public int deleteNoticeByIds(Long[] noticeIds);

    /**
     * 门户端查询消息列表
     */
    List<SysNotice> selectPortalNoticeList(Long userId, String receiverScope, String bizCategory, String readFlag,
            String keyword, Integer limit);

    /**
     * 门户端查询消息统计
     */
    Map<String, Object> selectPortalNoticeStats(Long userId, String receiverScope);

    /**
     * 标记已读
     */
    int markNoticeRead(Long noticeId, Long userId, String operator);
}
