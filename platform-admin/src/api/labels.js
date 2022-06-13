import request from '@/utils/platform-request'

export function groupLabels(id) {
  return request({
    url: '/manage/labels/group/' + id,
    method: 'get'
  })
}

export function listLabels(id) {
  return request({
    url: '/manage/labels/list/' + id,
    method: 'get'
  })
}

export function addLabels(data) {
  return request({
    url: '/manage/labels/add',
    method: 'post',
    data: data
  })
}

export function updateLabels(data) {
  return request({
    url: '/manage/labels/update',
    method: 'put',
    data: data
  })
}

export function batchUpdLabelLabels(data) {
  return request({
    url: '/manage/labels/batchUpdLabel',
    method: 'put',
    data: data
  })
}

export function deleteLabels(id) {
  return request({
    url: '/manage/labels/' + id,
    method: 'delete'
  })
}


