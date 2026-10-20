import request from '@/utils/request'

// 查询健康评估报告列表
export function listHealthReport(query) {
  return request({
    url: '/care/healthReport/list',
    method: 'get',
    params: query
  })
}

// 查询健康评估报告详细
export function getHealthReport(reportId) {
  return request({
    url: '/care/healthReport/' + reportId,
    method: 'get'
  })
}

// 新增健康评估报告
export function addHealthReport(data) {
  return request({
    url: '/care/healthReport',
    method: 'post',
    data: data
  })
}

// 修改健康评估报告
export function updateHealthReport(data) {
  return request({
    url: '/care/healthReport',
    method: 'put',
    data: data
  })
}

// 删除健康评估报告
export function delHealthReport(reportId) {
  return request({
    url: '/care/healthReport/' + reportId,
    method: 'delete'
  })
}
