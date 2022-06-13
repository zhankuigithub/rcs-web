import request from '@/utils/platform-request'


export function sendAndReceiveLog(data) {
  return request({
    url: '/manage/statistical/sendAndReceiveLog',
    method: 'POST',
    data
  })
}


export function chatLog(data) {
  return request({
    url: '/manage/statistical/chatLog',
    method: 'POST',
    data
  })
}
