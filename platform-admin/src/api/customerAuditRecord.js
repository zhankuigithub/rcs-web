import request from '@/utils/platform-request'

// 查询客户审核记录列表-分页
export function listCustomerAuditRecord(data) {
  return request({
    url: '/manage/customerAuditRecord/page',
    method: 'post',
    data: data
  })
}
