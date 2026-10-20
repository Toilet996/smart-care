import request from '@/utils/request'

// 查询传感器流水列表
export function listSensorLog(query) {
  return request({
    url: '/iot/sensorLog/list',
    method: 'get',
    params: query
  })
}

// 查询传感器流水详细
export function getSensorLog(logId) {
  return request({
    url: '/iot/sensorLog/' + logId,
    method: 'get'
  })
}

// 新增传感器流水
export function addSensorLog(data) {
  return request({
    url: '/iot/sensorLog',
    method: 'post',
    data: data
  })
}

// 修改传感器流水
export function updateSensorLog(data) {
  return request({
    url: '/iot/sensorLog',
    method: 'put',
    data: data
  })
}

// 删除传感器流水
export function delSensorLog(logId) {
  return request({
    url: '/iot/sensorLog/' + logId,
    method: 'delete'
  })
}

// 传感器数据上报（模拟测试用）
export function uploadSensorData(data) {
  return request({
    url: '/iot/sensorLog/upload',
    method: 'post',
    data: data
  })
}

// 批量传感器数据上报
export function uploadSensorDataBatch(data) {
  return request({
    url: '/iot/sensorLog/uploadBatch',
    method: 'post',
    data: data
  })
}
