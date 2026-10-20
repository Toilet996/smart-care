import request from '@/utils/request'

// 查询日常随访记录列表
export function listVisitRecord(query) {
  return request({
    url: '/care/visitRecord/list',
    method: 'get',
    params: query
  })
}

// 查询日常随访记录详细
export function getVisitRecord(recordId) {
  return request({
    url: '/care/visitRecord/' + recordId,
    method: 'get'
  })
}

// 新增日常随访记录
export function addVisitRecord(data) {
  return request({
    url: '/care/visitRecord',
    method: 'post',
    data: data
  })
}

// 修改日常随访记录
export function updateVisitRecord(data) {
  return request({
    url: '/care/visitRecord',
    method: 'put',
    data: data
  })
}

// 删除日常随访记录
export function delVisitRecord(recordId) {
  return request({
    url: '/care/visitRecord/' + recordId,
    method: 'delete'
  })
}
