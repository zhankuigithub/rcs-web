import request from '@/utils/admin-request'

export function getGroups() {
  return request({
    url: '/user/groups',
    method: 'get'
  })
}

export function page(pageParam) {
  return request({
    url: '/user/defaultPage',
    method: 'POST',
    data: pageParam
  })
}

export function save(data) {
  return request({
    url: '/user/add',
    method: 'POST',
    data
  })
}

export function edit(data) {
  return request({
    url: '/user/edit',
    method: 'PUT',
    data
  })
}

export function remove(id) {
  return request({
    url: '/user/' + id,
    method: 'DELETE'
  })
}
