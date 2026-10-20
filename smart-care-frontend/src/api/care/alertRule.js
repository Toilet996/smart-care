import request from '@/utils/request'

// 查询预警规则配置列表
export function listAlertRule(query) {
  return request({
    url: '/care/alertRule/list',
    method: 'get',
    params: query
  })
}

// 查询预警规则配置详细
export function getAlertRule(ruleId) {
  return request({
    url: '/care/alertRule/' + ruleId,
    method: 'get'
  })
}

// 新增预警规则配置
export function addAlertRule(data) {
  return request({
    url: '/care/alertRule',
    method: 'post',
    data: data
  })
}

// 修改预警规则配置
export function updateAlertRule(data) {
  return request({
    url: '/care/alertRule',
    method: 'put',
    data: data
  })
}

// 删除预警规则配置
export function delAlertRule(ruleId) {
  return request({
    url: '/care/alertRule/' + ruleId,
    method: 'delete'
  })
}
