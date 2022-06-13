import request from '@/utils/platform-request'

export function listResources(pageParam) {
  return request({
    url: '/manage/material/page',
    method: 'POST',
    data: pageParam
  })
}

export function deleteResources(id) {
  return request({
    url: '/manage/material/' + id,
    method: 'DELETE'
  })
}
export function addResources(data) {
  return request({
    url: '/manage/material/add',
    method: 'POST',
    data:data
  })
}

export function sendToCarrier(data) {
  return request({
    url: '/manage/material/sendToCarrier',
    method: 'POST',
    data:data
  })
}
export function auditMaterial(data) {
  return request({
    url: '/manage/material/audit',
    method: 'POST',
    data:data
  })
}
export function materialDetail(id) {
  return request({
    url: '/manage/material/item/' + id,
    method: 'GET',
  })
}

export function materialList(query) {
  return request({
    url: '/manage/material/list',
    method: 'GET',
    params:query
  })
}

export function rebuildMaterial(data) {
  return request({
    url: '/manage/material/rebuild',
    method: 'PUT',
    data:data
  })
}

