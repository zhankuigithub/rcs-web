import request from '@/utils/platform-request'

export function page(data) {
  return request({
    url: '/manage/sensitiveWords/page',
    method: 'post',
    data: data
  })
}

export function addSensitiveWords(data) {
  return request({
    url: '/manage/sensitiveWords/add',
    method: 'post',
    data: data
  })
}

export function checkSensitiveWords(data) {
  return request({
    url: '/manage/sensitiveWords/check',
    method: 'post',
    data: data
  })
}

export function updateSensitiveWords(data) {
  return request({
    url: '/manage/sensitiveWords/update',
    method: 'put',
    data: data
  })
}

export function deleteSensitiveWords(data) {
  return request({
    url: '/manage/sensitiveWords/delete',
    method: 'delete',
    data: data
  })
}
