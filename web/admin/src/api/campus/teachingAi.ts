import request from '@/utils/request'

export function assistTeachingConfig(data: any) {
  return request({
    url: '/campus/ai/teaching/assist',
    method: 'post',
    data,
  })
}
