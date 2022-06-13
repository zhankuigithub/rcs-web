import request from '@/utils/platform-request'
import axios from 'axios'
import { getToken } from '@/utils/auth'

const proxyPath = '/admin/gateway/rcs-platform'

export function detailPage(data, isExcel = false) {
  if (isExcel) {
    return axios({
      method: 'POST',
      url: proxyPath + `/manage/logTaskSendEvent/detailPage?isExcel=true`,
      data: data,
      responseType: 'blob',
      headers: {
        'ACCESS-TOKEN': getToken()
      }
    })
  }
  return request({
    url: '/manage/logTaskSendEvent/detailPage',
    method: 'post',
    data: data
  })
}
