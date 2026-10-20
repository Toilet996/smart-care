import request from '@/utils/request'

export function generateAiCareReport(data) {
  return request({
    url: '/care/aiCare/generate',
    method: 'post',
    data
  })
}
