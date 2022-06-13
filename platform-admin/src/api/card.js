import request from '@/utils/platform-request'

export function listCard(pageParam) {
  return request({
    url: '/manage/card/page',
    method: 'POST',
    data: pageParam
  })
}

export function saveCard(data) {
  return request({
    url: '/manage/card',
    method: 'post',
    data: data
  })
}

export function cardDelete(id) {
  return request({
    url: '/manage/card/' + id,
    method: 'DELETE'
  })
}

export function cardDetail(id) {
  return request({
    url: '/manage/card/' + id,
    method: 'GET'
  })
}
