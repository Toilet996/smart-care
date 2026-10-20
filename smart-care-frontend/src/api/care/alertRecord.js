import request from '@/utils/request'

// 查询异常预警记录列表
export function listAlertRecord(query) {
  return request({
    url: '/care/alertRecord/list',
    method: 'get',
    params: query
  })
}

// 查询异常预警记录详细
export function getAlertRecord(alertId) {
  return request({
    url: '/care/alertRecord/' + alertId,
    method: 'get'
  })
}

// 新增异常预警记录
export function addAlertRecord(data) {
  return request({
    url: '/care/alertRecord',
    method: 'post',
    data: data
  })
}

// 修改异常预警记录
export function updateAlertRecord(data) {
  return request({
    url: '/care/alertRecord',
    method: 'put',
    data: data
  })
}

// 删除异常预警记录
export function delAlertRecord(alertId) {
  return request({
    url: '/care/alertRecord/' + alertId,
    method: 'delete'
  })
}
