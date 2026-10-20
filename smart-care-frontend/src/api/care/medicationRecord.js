import request from '@/utils/request'

export function listMedicationRecord(query) {
  return request({
    url: '/care/medicationRecord/list',
    method: 'get',
    params: query
  })
}

export function getMedicationRecord(recordId) {
  return request({
    url: '/care/medicationRecord/' + recordId,
    method: 'get'
  })
}

export function addMedicationRecord(data) {
  return request({
    url: '/care/medicationRecord',
    method: 'post',
    data
  })
}

export function updateMedicationRecord(data) {
  return request({
    url: '/care/medicationRecord',
    method: 'put',
    data
  })
}

export function delMedicationRecord(recordId) {
  return request({
    url: '/care/medicationRecord/' + recordId,
    method: 'delete'
  })
}
