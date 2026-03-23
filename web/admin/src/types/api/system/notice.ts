import type { PageDomain, BaseEntity } from "../common";

/** 通知公告分页查询参数 */
export interface NoticeQueryParams extends PageDomain  {
  /** 公告标题 */
  noticeTitle?: string;
  /** 操作人员 */
  createBy?: string;
  /** 公告类型 */
  noticeType?: string;
  /** 业务分类 */
  bizCategory?: string;
  /** 接收范围 */
  receiverScope?: string;
  /** 状态 */
  status?: string;
}

/** 通知公告信息 */
export interface SysNotice extends BaseEntity {
  /** 公告编号 */
  noticeId?: number;
  /** 公告标题 */
  noticeTitle?: string;
  /** 公告类型（1通知 2公告） */
  noticeType?: '1' | '2';
  /** 公告内容 */
  noticeContent?: string;
  /** 状态（0正常 1停用） */
  status?: '0' | '1';
  /** 业务分类 */
  bizCategory?: 'TODO' | 'MESSAGE' | 'NOTICE';
  /** 摘要 */
  noticeSummary?: string;
  /** 接收范围 */
  receiverScope?: 'ALL' | 'STUDENT' | 'TEACHER' | 'PARENT' | 'CUSTOM';
  /** 指定用户ID */
  receiverUserIds?: string;
  /** 动作类型 */
  actionType?: string;
  /** 动作路径 */
  actionPath?: string;
  /** 动作目标ID */
  actionTargetId?: number;
  /** 是否置顶 */
  pinned?: '0' | '1';
  /** 发布时间 */
  publishTime?: string;
  /** 失效时间 */
  expireTime?: string;
}
