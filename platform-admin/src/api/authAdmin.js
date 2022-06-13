import request from '@/utils/admin-request'

export function page(data) {
  return request({
    url: '/manage/admin/page',
    method: 'post',
    data
  })
}

export function save(data) {
  return request({
    url: '/manage/admin/create',
    method: 'POST',
    data
  })
}

export function edit(data) {
  return request({
    url: '/manage/admin/modify',
    method: 'PUT',
    data
  })
}


export function remove(id) {
  return request({
    url: '/manage/admin/',
    method: 'DELETE',
    params: {id}
  })
}

export function detail(params) {
  console.log(params)
  return request({
    url: '/manage/admin/' + params.id,
    method: 'GET'
  })
}
