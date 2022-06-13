import request from '@/utils/admin-request'

export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

export function loginBySmsCode(data){
  return request({
    url: '/login/by/smscode',
    method: 'post',
    data
  })
}

export function sendSmsCode(data){
  return request({
    url: '/login/send/code',
    method: 'post',
    data
  })
}

export function getInfo(token) {
  return request({
    url: '/manage/info',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/manage/logout',
    method: 'post'
  })
}

export function updatePassWord(data) {
  return request({
    url: '/manage/admin/updatePassWord',
    method: 'put',
    data
  })
}

export function resetPassword(data) {
  return request({
    url: '/resetPassWord',
    method: 'post',
    data
  })
}

export function sendResetPasswordCode(data) {
  return request({
    url: '/sendResetCode',
    method: 'post',
    data
  })
}


