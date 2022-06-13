import request from '@/utils/admin-request'

export function page(pageParam) {
  return request({
    url: '/manage/roles/defaultPage',
    method: 'POST',
    data: pageParam
  })
}

export function save(data) {
  return request({
    url: '/manage/roles/add',
    method: 'POST',
    data
  })
}

export function edit(data) {
  return request({
    url: '/manage/roles/edit',
    method: 'PUT',
    data
  })
}

export function remove(id) {
  return request({
    url: `/manage/roles/${id}`,
    method: 'DELETE'
  })
}

export function getRoles(token) {
  return request({
    url: '/manage/roles/list',
    method: 'GET'
  })
}
