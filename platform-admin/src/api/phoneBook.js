import request from '@/utils/platform-request'
import axios from 'axios'
import { getToken } from '@/utils/auth'

const proxyPath = '/admin/gateway/rcs-platform'

export function pageByCid(data) {
  return request({
    url: '/manage/phoneNumberBook/pageByCid',
    method: 'post',
    data: data
  })
}

export function page(data, isExcel = false) {
  if (isExcel) {
    return axios({
      method: 'POST',
      url: proxyPath + `/manage/phoneNumberBook/page?isExcel=true`,
      data: data,
      responseType: 'blob',
      headers: {
        'ACCESS-TOKEN': getToken()
      }
    })
  }
  return request({
    url: '/manage/phoneNumberBook/page',
    method: 'post',
    data: data
  })
}

export function addPhoneNumberBook(data) {
  return request({
    url: '/manage/phoneNumberBook/add',
    method: 'post',
    data: data
  })
}

export function getPhonesByLabel(data) {
  return request({
    url: '/manage/phoneNumberBook/getPhonesByLabel',
    method: 'post',
    data: data
  })
}

export function updatePhoneNumberBook(data) {
  return request({
    url: '/manage/phoneNumberBook/update',
    method: 'put',
    data: data
  })
}

export function deletePhoneNumberBook(id) {
  return request({
    url: '/manage/phoneNumberBook/' + id,
    method: 'delete'
  })
}

export function deleteByCid(id) {
  return request({
    url: '/manage/phoneNumberBook/deleteByCid/' + id,
    method: 'delete'
  })
}

export function batchAdd(data) {
  return request({
    url: '/manage/phoneNumberBook/batchAdd',
    method: 'post',
    data: data
  })
}

export function batchImportAdd(form) {
  return axios({
    method: 'POST',
    url: proxyPath + `/manage/phoneNumberBook/batchAddByExcel`,
    data: form,
    responseType: 'json',
    headers: {
      'Content-Type': 'multipart/form-data',
      'ACCESS-TOKEN': getToken()
    }
  })
}

export function readExcel(form) {
  return axios({
    method: 'POST',
    url: proxyPath + `/manage/phoneNumberBook/readExcel`,
    data: form,
    responseType: 'json',
    headers: {
      'Content-Type': 'multipart/form-data',
      'ACCESS-TOKEN': getToken()
    }
  })
}
