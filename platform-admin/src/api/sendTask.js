import request from '@/utils/platform-request'

/**
 * 发送消息
 * @param {Object} data
 */
export function pushMessage(data) {
  return request({
    url: '/manage/push/sendMessage',
    method: 'POST',
    data
  })
}

export function taskPage(data) {
  return request({
    url: '/manage/push/page',
    method: 'POST',
    data
  })
}

export function cancel(data) {
  return request({
    url: '/manage/push/cancel',
    method: 'put',
    data
  })
}

export function activateOrSuspend(data) {
  return request({
    url: '/manage/push/activateOrSuspend',
    method: 'put',
    data
  })
}
