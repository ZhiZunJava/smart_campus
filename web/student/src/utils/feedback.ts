import { defineComponent, h, isVNode, markRaw, type VNode } from 'vue'
import {
  ElMessage as BaseMessage,
  ElMessageBox as BaseMessageBox,
  ElNotification as BaseNotification,
} from 'element-plus'
import type { ElMessageBoxOptions } from 'element-plus'

type FeedbackType = 'success' | 'warning' | 'info' | 'error'

const createRemixIcon = (name: string, className: string) =>
  markRaw(defineComponent({
    name,
    setup() {
      return () => h('i', { class: ['portal-feedback-remix-icon', className], 'aria-hidden': 'true' })
    },
  }))

const feedbackTypeIcons: Record<FeedbackType, ReturnType<typeof createRemixIcon>> = {
  success: createRemixIcon('PortalFeedbackSuccessIcon', 'ri-checkbox-circle-fill'),
  warning: createRemixIcon('PortalFeedbackWarningIcon', 'ri-error-warning-fill'),
  info: createRemixIcon('PortalFeedbackInfoIcon', 'ri-information-fill'),
  error: createRemixIcon('PortalFeedbackErrorIcon', 'ri-close-circle-fill'),
}

const feedbackCloseIcon = createRemixIcon('PortalFeedbackCloseIcon', 'ri-close-line')

function normalizeComponentOption<T>(value: T): T {
  if (value && (typeof value === 'object' || typeof value === 'function')) {
    return markRaw(value as object) as T
  }
  return value
}

function normalizeType(type?: string): FeedbackType | undefined {
  if (type === 'success' || type === 'warning' || type === 'info' || type === 'error') {
    return type
  }
  return undefined
}

function mergeCustomClass(customClass: unknown, injectedClass: string) {
  if (Array.isArray(customClass)) {
    return [...customClass, injectedClass].filter(Boolean).join(' ')
  }
  if (typeof customClass === 'string' && customClass.trim()) {
    return `${customClass} ${injectedClass}`
  }
  return injectedClass
}

function isRenderableMessage(value: unknown): value is string | VNode | (() => VNode) {
  return typeof value === 'string' || typeof value === 'function' || isVNode(value)
}

function normalizeMessageParams(params: any, forcedType?: FeedbackType) {
  if (isRenderableMessage(params)) {
    return {
      message: params,
      type: forcedType,
      icon: forcedType ? normalizeComponentOption(feedbackTypeIcons[forcedType]) : undefined,
      customClass: 'portal-feedback-message',
    }
  }

  const type = forcedType || normalizeType(params?.type)
  return {
    ...params,
    type,
    icon: normalizeComponentOption(params?.icon || (type ? feedbackTypeIcons[type] : undefined)),
    customClass: mergeCustomClass(params?.customClass, 'portal-feedback-message'),
  }
}

function normalizeNotificationParams(params: any, forcedType?: FeedbackType) {
  if (isRenderableMessage(params)) {
    return {
      message: params,
      type: forcedType,
      icon: forcedType ? normalizeComponentOption(feedbackTypeIcons[forcedType]) : undefined,
      closeIcon: normalizeComponentOption(feedbackCloseIcon),
      customClass: 'portal-feedback-notification',
    }
  }

  const type = forcedType || normalizeType(params?.type)
  return {
    ...params,
    type,
    icon: normalizeComponentOption(params?.icon || (type ? feedbackTypeIcons[type] : undefined)),
    closeIcon: normalizeComponentOption(params?.closeIcon || feedbackCloseIcon),
    customClass: mergeCustomClass(params?.customClass, 'portal-feedback-notification'),
  }
}

function normalizeMessageBoxOptions(options?: ElMessageBoxOptions, forcedType?: FeedbackType): ElMessageBoxOptions {
  const type = forcedType || normalizeType(options?.type)
  return {
    ...options,
    type,
    icon: normalizeComponentOption(options?.icon || (type ? feedbackTypeIcons[type] : undefined)),
    closeIcon: normalizeComponentOption(options?.closeIcon || feedbackCloseIcon),
    customClass: mergeCustomClass(options?.customClass, 'portal-feedback-message-box'),
  }
}

const ElMessage = Object.assign(
  (params: any) => BaseMessage(normalizeMessageParams(params)),
  {
    success: (params: any) => BaseMessage.success(normalizeMessageParams(params, 'success')),
    warning: (params: any) => BaseMessage.warning(normalizeMessageParams(params, 'warning')),
    info: (params: any) => BaseMessage.info(normalizeMessageParams(params, 'info')),
    error: (params: any) => BaseMessage.error(normalizeMessageParams(params, 'error')),
    closeAll: BaseMessage.closeAll,
  }
)

const ElNotification = Object.assign(
  (params: any) => BaseNotification(normalizeNotificationParams(params)),
  {
    success: (params: any) => BaseNotification.success(normalizeNotificationParams(params, 'success')),
    warning: (params: any) => BaseNotification.warning(normalizeNotificationParams(params, 'warning')),
    info: (params: any) => BaseNotification.info(normalizeNotificationParams(params, 'info')),
    error: (params: any) => BaseNotification.error(normalizeNotificationParams(params, 'error')),
    closeAll: BaseNotification.closeAll,
  }
)

const ElMessageBox = Object.assign(
  (message?: string | VNode, title?: string, options?: ElMessageBoxOptions) =>
    (BaseMessageBox as any)(message, title, normalizeMessageBoxOptions(options)),
  {
    alert: (message?: string | VNode, title?: string, options?: ElMessageBoxOptions) =>
      BaseMessageBox.alert(message, title, normalizeMessageBoxOptions(options)),
    confirm: (message?: string | VNode, title?: string, options?: ElMessageBoxOptions) =>
      BaseMessageBox.confirm(message, title, normalizeMessageBoxOptions(options)),
    prompt: (message?: string | VNode, title?: string, options?: ElMessageBoxOptions) =>
      BaseMessageBox.prompt(message, title, normalizeMessageBoxOptions(options)),
    close: BaseMessageBox.close,
  }
)

export { ElMessage, ElMessageBox, ElNotification }
