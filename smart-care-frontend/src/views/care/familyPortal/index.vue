<template>
  <div class="family-portal-page" v-loading="loading">
    <section class="hero-panel">
      <div class="hero-copy">
        <div class="eyebrow">Family Smart Care</div>
        <h1>家属门户</h1>
        <p>从家属视角集中查看老人实时状态、分级预警、历史报告与紧急联络信息，把“看状态、收提醒、查报告、快联系”串成一条顺手的照护链路。</p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" @click="refreshData">刷新数据</el-button>
        <el-button @click="goTo('/smartcare/alertRecord')">预警中心</el-button>
        <el-button @click="goTo('/smartcare/healthReport')">日报周报</el-button>
        <el-button @click="openContactDrawer">紧急联系人</el-button>
        <el-button type="danger" plain @click="openSosDialog">SOS 一键求助</el-button>
      </div>
    </section>

    <section class="stats-grid">
      <div v-for="item in statCards" :key="item.key" class="stat-card">
        <div class="stat-label">{{ item.label }}</div>
        <div class="stat-value" :class="item.highlight">{{ item.value }}</div>
        <div class="stat-desc">{{ item.desc }}</div>
      </div>
    </section>

    <section class="profile-grid-section">
      <el-card shadow="hover" class="panel-card">
        <template #header>
          <div class="panel-header">
            <span>家属信息</span>
            <el-tag type="info" effect="light">当前登录用户：{{ userStore.nickName || userStore.name || '--' }}</el-tag>
          </div>
        </template>
        <div class="profile-grid">
          <div class="profile-item">
            <span class="profile-label">家属姓名</span>
            <span class="profile-value">{{ familyProfile.familyName || '--' }}</span>
          </div>
          <div class="profile-item">
            <span class="profile-label">联系电话</span>
            <span class="profile-value">{{ familyProfile.familyPhone || '--' }}</span>
          </div>
          <div class="profile-item">
            <span class="profile-label">关系概览</span>
            <span class="profile-value">{{ familyProfile.relationSummary || '--' }}</span>
          </div>
          <div class="profile-item">
            <span class="profile-label">关联老人</span>
            <span class="profile-value">{{ coreStats.elderlyTotal || 0 }} 位</span>
          </div>
        </div>
      </el-card>

      <el-card shadow="hover" class="panel-card">
        <template #header>
          <div class="panel-header">
            <span>一键呼叫</span>
            <span class="panel-note">优先呼叫紧急联系人，也可直拨社区和值班热线</span>
          </div>
        </template>
        <div class="hotline-grid">
          <div class="hotline-card primary">
            <div class="hotline-title">优先联系人</div>
            <div class="hotline-name">{{ primaryEmergencyContact.familyName || '未设置' }}</div>
            <div class="hotline-phone">{{ primaryEmergencyContact.familyPhone || '--' }}</div>
            <div class="hotline-actions">
              <el-button size="small" :disabled="!primaryEmergencyContact.familyPhone" @click="copyPhone(primaryEmergencyContact.familyPhone)">复制号码</el-button>
              <el-button size="small" type="primary" :disabled="!primaryEmergencyContact.familyPhone" @click="callPhone(primaryEmergencyContact.familyPhone)">立即呼叫</el-button>
            </div>
          </div>
          <div class="hotline-card">
            <div class="hotline-title">社区值班</div>
            <div class="hotline-phone">{{ serviceHotlines.communityHotline || '12349' }}</div>
            <div class="hotline-actions">
              <el-button size="small" @click="copyPhone(serviceHotlines.communityHotline || '12349')">复制号码</el-button>
              <el-button size="small" type="success" @click="callPhone(serviceHotlines.communityHotline || '12349')">拨打社区</el-button>
            </div>
          </div>
          <div class="hotline-card">
            <div class="hotline-title">医疗急救 / 警务</div>
            <div class="hotline-inline">
              <el-button type="danger" plain @click="callPhone(serviceHotlines.medicalHotline || '120')">120</el-button>
              <el-button type="warning" plain @click="callPhone(serviceHotlines.policeHotline || '110')">110</el-button>
            </div>
            <div class="hotline-tip">紧急情况可直接拨打社会公共救援热线</div>
          </div>
        </div>
      </el-card>
    </section>

    <section class="main-grid">
      <el-card shadow="hover" class="panel-card">
        <template #header>
          <div class="panel-header">
            <span>老人实时状态</span>
            <span class="panel-note">按老人查看在床 / 活动 / 外出 / 离线、生命体征与吃药风险</span>
          </div>
        </template>

        <el-empty v-if="!elderlyList.length" description="当前账号还没有关联老人数据，可先维护家属关联信息。" />

        <div v-else class="elderly-grid">
          <div
            v-for="item in elderlyList"
            :key="item.elderlyId"
            class="elderly-card"
            :class="{ active: selectedElderlyId === item.elderlyId }"
            @click="selectElderly(item)"
          >
            <div class="elderly-top">
              <div>
                <div class="elderly-name">{{ item.elderlyName || '--' }}</div>
                <div class="elderly-meta">{{ genderLabel(item.gender) }} · {{ item.age || '--' }} 岁</div>
              </div>
              <el-tag :type="stateTagType(item.currentState)" effect="light">{{ stateLabel(item.currentState) }}</el-tag>
            </div>

            <div class="metric-row">
              <span class="metric-label">心率</span>
              <span class="metric-value">{{ item.avgHeartRate ?? '--' }} 次/分</span>
            </div>
            <div class="metric-row">
              <span class="metric-label">呼吸</span>
              <span class="metric-value">{{ item.avgBreathRate ?? '--' }} 次/分</span>
            </div>
            <div class="metric-row">
              <span class="metric-label">吃药情况</span>
              <el-tag :type="item.medicationMissedCount > 0 ? 'warning' : 'success'" effect="light">{{ medicationLabel(item) }}</el-tag>
            </div>
            <div class="metric-row">
              <span class="metric-label">门磁状态</span>
              <el-tag :type="doorTagType(item.latestDoorStatus)" effect="light">{{ doorLabel(item.latestDoorStatus) }}</el-tag>
            </div>
            <div class="metric-row">
              <span class="metric-label">健康评分</span>
              <span class="metric-value">{{ item.latestHealthScore ?? '--' }}</span>
            </div>

            <div class="elderly-footer">
              <span>最近监测：{{ formatDateTime(item.latestMetricTime) }}</span>
              <div class="footer-actions">
                <el-button link type="primary" @click.stop="goToElderly(item)">档案</el-button>
                <el-button link type="danger" @click.stop="goToAlert(item)">预警</el-button>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <el-card shadow="hover" class="panel-card">
        <template #header>
          <div class="panel-header">
            <span>{{ selectedElderlyName }} 生命体征曲线</span>
            <span class="panel-note">最近心率 / 呼吸趋势</span>
          </div>
        </template>
        <div ref="vitalsChartRef" class="chart-container"></div>
      </el-card>
    </section>

    <section class="table-grid">
      <el-card shadow="hover" class="panel-card">
        <template #header>
          <div class="panel-header">
            <span>分级预警推送</span>
            <el-button link type="primary" @click="goTo('/smartcare/alertRecord')">更多</el-button>
          </div>
        </template>
        <el-table :data="recentAlerts" size="small">
          <el-table-column label="老人" min-width="90">
            <template #default="{ row }">
              <el-button link type="primary" @click="goToAlert(row)">{{ row.elderlyName }}</el-button>
            </template>
          </el-table-column>
          <el-table-column label="等级" width="90">
            <template #default="{ row }">
              <el-tag :type="alertLevelTagType(row.alertLevel)" effect="light">{{ alertLevelLabel(row.alertLevel) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="类型" prop="alertType" min-width="120" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="handleStatusTagType(row.handleStatus)" effect="light">{{ handleStatusLabel(row.handleStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="时间" min-width="150">
            <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card shadow="hover" class="panel-card">
        <template #header>
          <div class="panel-header">
            <span>历史日报周报</span>
            <el-button link type="primary" @click="goTo('/smartcare/healthReport')">更多</el-button>
          </div>
        </template>
        <el-table :data="latestReports" size="small">
          <el-table-column label="老人" min-width="90">
            <template #default="{ row }">
              <el-button link type="primary" @click="goToReport(row)">{{ row.elderlyName }}</el-button>
            </template>
          </el-table-column>
          <el-table-column label="类型" width="90">
            <template #default="{ row }">
              <dict-tag :options="biz_report_type" :value="row.reportType" />
            </template>
          </el-table-column>
          <el-table-column label="综合评分" width="90" prop="totalScore" />
          <el-table-column label="日期" min-width="120">
            <template #default="{ row }">{{ formatDate(row.reportDate) }}</template>
          </el-table-column>
          </el-table>
      </el-card>

      <el-card shadow="hover" class="panel-card">
        <template #header>
          <div class="panel-header">
            <span>{{ selectedElderlyName }} 今日服药</span>
            <div class="inline-actions">
              <el-tag type="info" effect="light">计划 {{ medicationSummary.planTotal || 0 }}</el-tag>
              <el-button link type="primary" @click="goTo('/smartcare/medicationRecord')">更多</el-button>
            </div>
          </div>
        </template>
        <div class="medication-summary">
          <div class="summary-chip success">已服 {{ medicationSummary.takenTotal || 0 }}</div>
          <div class="summary-chip warning">待服 {{ medicationSummary.pendingTotal || 0 }}</div>
          <div class="summary-chip danger">漏服 {{ medicationSummary.missedTotal || 0 }}</div>
        </div>
        <div class="medication-plan-list">
          <el-empty v-if="!todayMedicationPlans.length" description="今日暂无服药计划" :image-size="72" />
          <div v-else v-for="item in todayMedicationPlans" :key="item.planId" class="medication-plan-item">
            <div>
              <div class="contact-name">{{ item.medicationName || '--' }}</div>
              <div class="contact-meta">{{ item.planTime || '--:--' }} · {{ item.dosage || '按医嘱' }}</div>
            </div>
            <div class="sos-side">
              <el-tag :type="executeStatusTagType(item.executeStatus)" effect="light">{{ executeStatusLabel(item.executeStatus) }}</el-tag>
              <span class="sos-time">{{ formatDateTime(item.actualTime) }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card shadow="hover" class="panel-card">
        <template #header>
          <div class="panel-header">
            <span>紧急联系人与 SOS</span>
            <div class="inline-actions">
              <el-button link type="primary" @click="openContactDrawer">设置联系人</el-button>
              <el-button link type="danger" @click="openSosDialog">发起 SOS</el-button>
            </div>
          </div>
        </template>
        <div class="contact-list">
          <div v-for="item in emergencyContacts" :key="item.id" class="contact-item">
            <div>
              <div class="contact-name">
                {{ item.familyName || '--' }}
                <el-tag v-if="isEmergency(item.isEmergency)" type="danger" effect="light">紧急</el-tag>
              </div>
              <div class="contact-meta">{{ item.relation || '--' }} · {{ item.familyPhone || '--' }}</div>
              <div class="contact-elderly">关联老人：{{ item.elderlyName || '--' }}</div>
            </div>
            <div class="contact-actions">
              <el-button size="small" @click="copyPhone(item.familyPhone)">复制号码</el-button>
              <el-button size="small" type="primary" @click="callPhone(item.familyPhone)">一键呼叫</el-button>
            </div>
          </div>
          <el-empty v-if="!emergencyContacts.length" description="暂无紧急联系人，请先在本页维护。" />
        </div>

        <div class="sos-history">
          <div class="sub-title">最近 SOS 记录</div>
          <el-empty v-if="!recentSosAlerts.length" description="最近暂无 SOS 记录" :image-size="84" />
          <div v-else class="sos-list">
            <div v-for="item in recentSosAlerts" :key="item.alertId" class="sos-item">
              <div>
                <div class="sos-name">{{ item.elderlyName }}</div>
                <div class="sos-desc">{{ item.alertDesc }}</div>
              </div>
              <div class="sos-side">
                <el-tag :type="handleStatusTagType(item.handleStatus)" effect="light">{{ handleStatusLabel(item.handleStatus) }}</el-tag>
                <span class="sos-time">{{ formatDateTime(item.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </section>

    <el-drawer v-model="contactDrawerVisible" title="紧急联系人设置" size="680px" append-to-body>
      <div class="drawer-toolbar">
        <el-button type="primary" @click="handleAddContact">新增联系人</el-button>
        <el-button @click="loadContacts">刷新</el-button>
      </div>
      <el-table :data="contactList" size="small" border>
        <el-table-column label="老人" prop="elderlyName" min-width="90" />
        <el-table-column label="姓名" prop="familyName" min-width="100" />
        <el-table-column label="电话" prop="familyPhone" min-width="120" />
        <el-table-column label="关系" prop="relation" min-width="90">
          <template #default="{ row }">
            <dict-tag :options="biz_family_relation" :value="row.relation" />
          </template>
        </el-table-column>
        <el-table-column label="紧急联系人" width="110">
          <template #default="{ row }">
            <el-tag :type="isEmergency(row.isEmergency) ? 'danger' : 'info'" effect="light">{{ isEmergency(row.isEmergency) ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEditContact(row)">编辑</el-button>
            <el-button link type="success" @click="callPhone(row.familyPhone)">呼叫</el-button>
            <el-button link type="danger" @click="handleDeleteContact(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>

    <el-dialog v-model="contactDialogVisible" :title="contactDialogTitle" width="520px" append-to-body>
      <el-form ref="contactFormRef" :model="contactForm" :rules="contactRules" label-width="96px">
        <el-form-item label="关联老人" prop="elderlyId">
          <el-select v-model="contactForm.elderlyId" placeholder="请选择老人" style="width: 100%">
            <el-option v-for="item in elderlyList" :key="item.elderlyId" :label="item.elderlyName" :value="item.elderlyId" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系人姓名" prop="familyName">
          <el-input v-model="contactForm.familyName" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="familyPhone">
          <el-input v-model="contactForm.familyPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="亲属关系" prop="relation">
          <el-select v-model="contactForm.relation" placeholder="请选择亲属关系" style="width: 100%">
            <el-option v-for="item in biz_family_relation" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急联系人" prop="isEmergency">
          <el-switch v-model="contactForm.isEmergency" active-value="Y" inactive-value="N" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitContactForm">保存</el-button>
          <el-button @click="contactDialogVisible = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="sosDialogVisible" title="发起 SOS 一键求助" width="560px" append-to-body>
      <el-form ref="sosFormRef" :model="sosForm" :rules="sosRules" label-width="92px">
        <el-form-item label="老人" prop="elderlyId">
          <el-select v-model="sosForm.elderlyId" placeholder="请选择老人" style="width: 100%">
            <el-option v-for="item in elderlyList" :key="item.elderlyId" :label="item.elderlyName" :value="item.elderlyId" />
          </el-select>
        </el-form-item>
        <el-form-item label="情况说明" prop="description">
          <el-input v-model="sosForm.description" type="textarea" :rows="4" placeholder="请输入当前异常情况，例如老人无应答、疑似跌倒、需要立即上门。" />
        </el-form-item>
        <el-form-item label="位置提示">
          <el-input v-model="sosForm.locationHint" placeholder="例如卧室、卫生间、客厅等" />
        </el-form-item>
        <el-form-item label="回拨电话">
          <el-input v-model="sosForm.callbackPhone" placeholder="默认使用家属联系电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="danger" :loading="sosSubmitting" @click="submitSos">立即求助</el-button>
          <el-button @click="sosDialogVisible = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="FamilyPortal">
import { computed, getCurrentInstance, nextTick, onBeforeUnmount, onMounted, reactive, ref, toRefs } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { parseTime } from '@/utils/ruoyi'
import { getFamilyPortalData, listFamilyPortalContacts, addFamilyPortalContact, updateFamilyPortalContact, deleteFamilyPortalContact, triggerFamilyPortalSos } from '@/api/care/familyPortal'
import useUserStore from '@/store/modules/user'

const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { sys_user_sex, biz_report_type, biz_family_relation } = proxy.useDict('sys_user_sex', 'biz_report_type', 'biz_family_relation')

const loading = ref(false)
const sosSubmitting = ref(false)
const contactDrawerVisible = ref(false)
const contactDialogVisible = ref(false)
const sosDialogVisible = ref(false)
const contactDialogTitle = ref('新增联系人')
const familyProfile = ref({})
const coreStats = ref({})
const elderlyList = ref([])
const recentAlerts = ref([])
const latestReports = ref([])
const emergencyContacts = ref([])
const recentSosAlerts = ref([])
const medicationSummary = ref({})
const todayMedicationPlans = ref([])
const contactList = ref([])
const serviceHotlines = ref({})
const selectedElderlyId = ref(null)
const selectedElderlyName = ref('老人')
const vitalsTrend = ref([])
const vitalsChartRef = ref()

const data = reactive({
  contactForm: { id: null, elderlyId: null, familyName: '', familyPhone: '', relation: '', isEmergency: 'Y' },
  sosForm: { elderlyId: null, description: '', locationHint: '', callbackPhone: '' },
  contactRules: {
    elderlyId: [{ required: true, message: '请选择老人', trigger: 'change' }],
    familyName: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
    familyPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
    relation: [{ required: true, message: '请选择亲属关系', trigger: 'change' }]
  },
  sosRules: {
    elderlyId: [{ required: true, message: '请选择老人', trigger: 'change' }],
    description: [{ required: true, message: '请输入情况说明', trigger: 'blur' }]
  }
})

const { contactForm, sosForm, contactRules, sosRules } = toRefs(data)
let vitalsChart

const statCards = computed(() => [
  { key: 'elderlyTotal', label: '照护老人', value: coreStats.value.elderlyTotal || 0, desc: '当前家属已关联的老人数量' },
  { key: 'todayAlertTotal', label: '今日预警', value: coreStats.value.todayAlertTotal || 0, desc: '今天产生的预警消息', highlight: 'warning' },
  { key: 'urgentAlertTotal', label: '紧急预警', value: coreStats.value.urgentAlertTotal || 0, desc: '三级紧急且仍待处理', highlight: 'danger' },
  { key: 'weekReportTotal', label: '近 7 日报告', value: coreStats.value.weekReportTotal || 0, desc: '睡眠、活动与健康评估报告' },
  { key: 'emergencyContactTotal', label: '紧急联系人', value: coreStats.value.emergencyContactTotal || 0, desc: '可快速呼叫的紧急联系人数量' }
])

const primaryEmergencyContact = computed(() => emergencyContacts.value.find((item) => isEmergency(item.isEmergency)) || emergencyContacts.value[0] || {})

function currentParams(extra = {}) {
  const params = { ...extra }
  if (route.query.userId) {
    params.userId = Number(route.query.userId)
  }
  return params
}

function refreshData() {
  fetchData(selectedElderlyId.value)
}

function fetchData(elderlyId) {
  loading.value = true
  getFamilyPortalData(currentParams(elderlyId ? { elderlyId } : {}))
    .then((response) => {
      const source = response.data || {}
      familyProfile.value = source.familyProfile || {}
      coreStats.value = source.coreStats || {}
      elderlyList.value = (source.elderlyList || []).map((item) => ({ ...item, currentState: inferCurrentState(item) }))
      recentAlerts.value = source.recentAlerts || []
      latestReports.value = source.latestReports || []
      emergencyContacts.value = source.emergencyContacts || []
      recentSosAlerts.value = source.recentSosAlerts || []
      medicationSummary.value = source.todayMedicationSummary || {}
      todayMedicationPlans.value = source.todayMedicationPlans || []
      serviceHotlines.value = source.serviceHotlines || {}
      vitalsTrend.value = source.vitalsTrend || []
      selectedElderlyId.value = source.focusElderlyId || elderlyList.value[0]?.elderlyId || null
      selectedElderlyName.value = elderlyList.value.find((item) => item.elderlyId === selectedElderlyId.value)?.elderlyName || '老人'
      if (!sosForm.value.elderlyId) {
        sosForm.value.elderlyId = selectedElderlyId.value
      }
      if (!sosForm.value.callbackPhone) {
        sosForm.value.callbackPhone = familyProfile.value.familyPhone || ''
      }
      nextTick(() => renderVitalsChart())
    })
    .finally(() => {
      loading.value = false
    })
}

function loadContacts() {
  return listFamilyPortalContacts(currentParams()).then((response) => {
    contactList.value = response.data || []
  })
}

function openContactDrawer() {
  contactDrawerVisible.value = true
  loadContacts()
}

function resetContactForm() {
  contactForm.value = {
    id: null,
    elderlyId: selectedElderlyId.value || elderlyList.value[0]?.elderlyId || null,
    familyName: '',
    familyPhone: familyProfile.value.familyPhone || '',
    relation: '',
    isEmergency: 'Y'
  }
  proxy.resetForm('contactFormRef')
}

function handleAddContact() {
  resetContactForm()
  contactDialogTitle.value = '新增联系人'
  contactDialogVisible.value = true
}

function handleEditContact(row) {
  contactForm.value = {
    id: row.id,
    elderlyId: row.elderlyId,
    familyName: row.familyName,
    familyPhone: row.familyPhone,
    relation: row.relation,
    isEmergency: isEmergency(row.isEmergency) ? 'Y' : 'N'
  }
  contactDialogTitle.value = '编辑联系人'
  contactDialogVisible.value = true
}

function submitContactForm() {
  proxy.$refs.contactFormRef.validate((valid) => {
    if (!valid) {
      return
    }
    const request = contactForm.value.id
      ? updateFamilyPortalContact(contactForm.value, currentParams())
      : addFamilyPortalContact(contactForm.value, currentParams())
    request.then(() => {
      proxy.$modal.msgSuccess(contactForm.value.id ? '联系人已更新' : '联系人已新增')
      contactDialogVisible.value = false
      loadContacts()
      fetchData(selectedElderlyId.value)
    })
  })
}

function handleDeleteContact(row) {
  proxy.$modal.confirm(`确认删除联系人“${row.familyName || '--'}”吗？`)
    .then(() => deleteFamilyPortalContact(row.id, currentParams()))
    .then(() => {
      proxy.$modal.msgSuccess('联系人已删除')
      loadContacts()
      fetchData(selectedElderlyId.value)
    })
    .catch(() => {})
}

function openSosDialog() {
  if (!elderlyList.value.length) {
    proxy.$modal.msgWarning('当前没有可发起 SOS 的老人数据')
    return
  }
  if (!sosForm.value.elderlyId) {
    sosForm.value.elderlyId = selectedElderlyId.value || elderlyList.value[0]?.elderlyId || null
  }
  if (!sosForm.value.callbackPhone) {
    sosForm.value.callbackPhone = familyProfile.value.familyPhone || primaryEmergencyContact.value.familyPhone || ''
  }
  sosDialogVisible.value = true
}

function submitSos() {
  proxy.$refs.sosFormRef.validate((valid) => {
    if (!valid) {
      return
    }
    sosSubmitting.value = true
    triggerFamilyPortalSos(sosForm.value, currentParams())
      .then((response) => {
        const payload = response.data || {}
        proxy.$modal.msgSuccess(payload.message || 'SOS 已提交')
        sosDialogVisible.value = false
        sosForm.value.description = ''
        sosForm.value.locationHint = ''
        fetchData(selectedElderlyId.value)
        if (payload.orderId) {
          router.push({ path: '/smartcare/workOrder', query: { orderId: payload.orderId, alertId: payload.alertId } })
        }
      })
      .finally(() => {
        sosSubmitting.value = false
      })
  })
}

function inferCurrentState(item) {
  if (!item || !item.latestMetricTime) return 'offline'
  if (String(item.latestDoorStatus || '').toLowerCase() === 'open' && isRecent(item.latestDoorStatusTime, 12)) return 'out'
  if (String(item.latestBedStatus || '').toLowerCase() === 'on_bed' && isRecent(item.latestBedStatusTime, 12)) return 'in_bed'
  if (String(item.latestBedStatus || '').toLowerCase() === 'off_bed' && isRecent(item.latestBedStatusTime, 12)) return 'active'
  const diffHours = (Date.now() - new Date(item.latestMetricTime).getTime()) / (1000 * 60 * 60)
  return diffHours > 12 ? 'offline' : 'active'
}

function isRecent(value, maxHours) {
  if (!value) return false
  const diffHours = (Date.now() - new Date(value).getTime()) / (1000 * 60 * 60)
  return diffHours <= maxHours
}

function stateLabel(value) {
  if (value === 'in_bed') return '在床'
  if (value === 'active') return '活动'
  if (value === 'out') return '外出'
  return '离线'
}

function stateTagType(value) {
  if (value === 'in_bed') return 'success'
  if (value === 'active') return 'primary'
  if (value === 'out') return 'warning'
  return 'info'
}

function medicationLabel(item) {
  if ((item?.medicationMissedCount || 0) > 0) return `今日漏服 ${item.medicationMissedCount} 次`
  if ((item?.medicationTakenCount || 0) > 0) return `今日已服药 ${item.medicationTakenCount} 次`
  if (String(item?.latestPillStatus || '').toLowerCase() === 'waiting') return '待服药提醒'
  return '今日暂无服药记录'
}

function doorLabel(value) {
  const current = String(value || '').toLowerCase()
  if (current === 'open') return '门已打开'
  if (current === 'closed') return '门已关闭'
  return '暂无数据'
}

function doorTagType(value) {
  const current = String(value || '').toLowerCase()
  if (current === 'open') return 'warning'
  if (current === 'closed') return 'success'
  return 'info'
}

function genderLabel(value) {
  return sys_user_sex.value.find((item) => item.value === String(value ?? ''))?.label || '未设置'
}

function alertLevelLabel(value) {
  if (String(value) === '2') return '三级紧急'
  if (String(value) === '1') return '二级警示'
  return '一级关注'
}

function alertLevelTagType(value) {
  if (String(value) === '2') return 'danger'
  if (String(value) === '1') return 'warning'
  return 'info'
}

function handleStatusLabel(value) {
  if (String(value) === '2') return '已结办'
  if (String(value) === '1') return '处理中'
  if (String(value) === '3') return '误报'
  return '未处理'
}

function handleStatusTagType(value) {
  if (String(value) === '2') return 'success'
  if (String(value) === '1') return 'warning'
  if (String(value) === '3') return 'info'
  return 'danger'
}

function executeStatusLabel(value) {
  if (String(value) === '1') return '已服药'
  if (String(value) === '2') return '漏服'
  return '待服药'
}

function executeStatusTagType(value) {
  if (String(value) === '1') return 'success'
  if (String(value) === '2') return 'danger'
  return 'warning'
}

function isEmergency(value) {
  return ['1', 'Y', 'y', 'true', 'TRUE'].includes(String(value))
}

function formatDateTime(value) {
  return value ? parseTime(value, '{y}-{m}-{d} {h}:{i}') : '--'
}

function formatDate(value) {
  return value ? parseTime(value, '{y}-{m}-{d}') : '--'
}

function goTo(path) {
  router.push(path)
}

function goToElderly(row) {
  router.push({ path: '/smartcare/elderly', query: { elderlyName: row.elderlyName } })
}

function goToAlert(row) {
  router.push({ path: '/smartcare/alertRecord', query: { elderlyId: row.elderlyId, elderlyName: row.elderlyName } })
}

function goToReport(row) {
  router.push({ path: '/smartcare/healthReport', query: { elderlyId: row.elderlyId, elderlyName: row.elderlyName } })
}

function copyPhone(phone) {
  if (!phone || phone === '--') {
    proxy.$modal.msgWarning('当前没有可用电话号码')
    return
  }
  navigator.clipboard.writeText(String(phone)).then(() => {
    proxy.$modal.msgSuccess('号码已复制')
  })
}

function callPhone(phone) {
  if (!phone || phone === '--') {
    proxy.$modal.msgWarning('当前没有可用电话号码')
    return
  }
  window.location.href = `tel:${phone}`
}

function selectElderly(item) {
  if (!item?.elderlyId) return
  selectedElderlyId.value = item.elderlyId
  selectedElderlyName.value = item.elderlyName || '老人'
  fetchData(item.elderlyId)
}

function renderVitalsChart() {
  if (!vitalsChartRef.value) return
  if (!vitalsChart) vitalsChart = echarts.init(vitalsChartRef.value)
  const source = [...(vitalsTrend.value || [])].reverse()
  const timeAxis = source.map((item) => item.timeLabel)
  const heartSeries = source.map((item) => (item.metricType === 'heart_rate' ? Number(item.metricValue) : null))
  const breathSeries = source.map((item) => (item.metricType === 'breath_rate' ? Number(item.metricValue) : null))
  vitalsChart.setOption({
    color: ['#ef4444', '#2563eb'],
    tooltip: { trigger: 'axis' },
    legend: { data: ['心率', '呼吸'] },
    grid: { left: 42, right: 24, top: 40, bottom: 28 },
    xAxis: { type: 'category', data: timeAxis, axisLabel: { color: '#64748b' } },
    yAxis: { type: 'value', axisLabel: { color: '#64748b' }, splitLine: { lineStyle: { color: '#e5e7eb' } } },
    series: [
      { name: '心率', type: 'line', smooth: true, connectNulls: false, data: heartSeries },
      { name: '呼吸', type: 'line', smooth: true, connectNulls: false, data: breathSeries }
    ]
  })
}

function handleResize() {
  vitalsChart?.resize()
}

onMounted(() => {
  fetchData(Number(route.query.elderlyId) || null)
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  vitalsChart?.dispose()
  vitalsChart = null
})
</script>

<style lang="scss" scoped>
.family-portal-page {
  padding: 18px;
  min-height: calc(100vh - 84px);
  background:
    radial-gradient(circle at top left, rgba(56, 189, 248, 0.12), transparent 28%),
    radial-gradient(circle at top right, rgba(16, 185, 129, 0.12), transparent 22%),
    #f6f9fc;

  .hero-panel,
  .panel-card,
  .stat-card {
    border-radius: 20px;
  }

  .hero-panel {
    display: flex;
    justify-content: space-between;
    gap: 24px;
    padding: 28px;
    margin-bottom: 18px;
    background: linear-gradient(135deg, #0f766e 0%, #0f9d8a 48%, #38bdf8 100%);
    color: #fff;
  }

  .eyebrow {
    font-size: 12px;
    letter-spacing: 0.16em;
    text-transform: uppercase;
    opacity: 0.78;
    margin-bottom: 12px;
  }

  h1 {
    margin: 0 0 12px;
    font-size: 34px;
    line-height: 1.15;
  }

  p {
    margin: 0;
    max-width: 760px;
    line-height: 1.8;
    opacity: 0.92;
  }

  .hero-actions {
    display: flex;
    flex-wrap: wrap;
    align-items: flex-start;
    justify-content: flex-end;
    gap: 12px;
    min-width: 320px;
  }

  .stats-grid,
  .table-grid {
    display: grid;
    gap: 16px;
    margin-bottom: 16px;
  }

  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  }

  .stat-card {
    padding: 22px;
    background: #fff;
    box-shadow: 0 14px 40px rgba(15, 23, 42, 0.06);
  }

  .stat-label { color: #64748b; font-size: 13px; margin-bottom: 10px; }
  .stat-value { color: #0f172a; font-size: 30px; font-weight: 700; }
  .stat-value.warning { color: #d97706; }
  .stat-value.danger { color: #dc2626; }
  .stat-desc { margin-top: 10px; font-size: 12px; color: #94a3b8; }

  .profile-grid-section {
    display: grid;
    grid-template-columns: 1.2fr 1fr;
    gap: 16px;
    margin-bottom: 16px;
  }

  .panel-card {
    background: rgba(255, 255, 255, 0.94);
    box-shadow: 0 14px 34px rgba(15, 23, 42, 0.06);
    border: 1px solid rgba(226, 232, 240, 0.9);
  }

  .panel-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    font-weight: 600;
  }

  .panel-note { color: #94a3b8; font-size: 12px; font-weight: 400; }
  .inline-actions,
  .hotline-inline,
  .hotline-actions,
  .footer-actions,
  .contact-actions,
  .drawer-toolbar { display: flex; align-items: center; gap: 8px; }

  .profile-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 14px;
  }

  .profile-item {
    background: #f8fbff;
    border: 1px solid #e5edf7;
    border-radius: 16px;
    padding: 16px 18px;
  }

  .profile-label { display: block; color: #64748b; font-size: 12px; margin-bottom: 6px; }
  .profile-value { color: #0f172a; font-weight: 600; }

  .hotline-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 12px;
  }

  .hotline-card {
    border: 1px solid #e5edf7;
    border-radius: 16px;
    padding: 16px;
    background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
  }

  .hotline-card.primary {
    border-color: #38bdf8;
    box-shadow: 0 10px 24px rgba(56, 189, 248, 0.12);
  }

  .hotline-title { color: #64748b; font-size: 13px; margin-bottom: 10px; }
  .hotline-name,
  .hotline-phone { color: #0f172a; font-size: 20px; font-weight: 700; }
  .hotline-tip { margin-top: 10px; font-size: 12px; color: #94a3b8; }
  .hotline-actions { margin-top: 14px; }

  .main-grid {
    display: grid;
    grid-template-columns: 1.25fr 1fr;
    gap: 16px;
    margin-bottom: 16px;
  }

  .elderly-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
    gap: 14px;
  }

  .elderly-card {
    border: 1px solid #e5edf7;
    border-radius: 18px;
    padding: 16px;
    background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
    cursor: pointer;
    transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
  }

  .elderly-card.active {
    border-color: #38bdf8;
    box-shadow: 0 12px 30px rgba(56, 189, 248, 0.14);
    transform: translateY(-2px);
  }

  .elderly-top,
  .elderly-footer,
  .metric-row,
  .contact-item,
  .sos-item { display: flex; align-items: center; justify-content: space-between; gap: 12px; }

  .elderly-name,
  .contact-name,
  .sos-name { color: #0f172a; font-size: 18px; font-weight: 700; }

  .elderly-meta,
  .contact-meta,
  .contact-elderly,
  .sos-desc,
  .sos-time { color: #64748b; font-size: 12px; margin-top: 4px; }

  .metric-row { margin-top: 12px; }
  .metric-label { color: #64748b; font-size: 13px; }
  .metric-value { color: #0f172a; font-weight: 600; }
  .elderly-footer { margin-top: 14px; color: #94a3b8; font-size: 12px; }
  .chart-container { height: 380px; }
  .table-grid { grid-template-columns: repeat(3, minmax(0, 1fr)); }
  .contact-list,
  .sos-list,
  .medication-plan-list { display: flex; flex-direction: column; gap: 12px; }

  .medication-summary {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 14px;
  }

  .summary-chip {
    padding: 8px 12px;
    border-radius: 999px;
    font-size: 12px;
    font-weight: 600;
    background: #eff6ff;
    color: #1d4ed8;
  }

  .summary-chip.success { background: #ecfdf5; color: #047857; }
  .summary-chip.warning { background: #fffbeb; color: #b45309; }
  .summary-chip.danger { background: #fef2f2; color: #b91c1c; }

  .contact-item,
  .sos-item,
  .medication-plan-item {
    border: 1px solid #e5edf7;
    border-radius: 16px;
    padding: 14px 16px;
    background: #f8fbff;
  }

  .sos-history {
    margin-top: 18px;
    padding-top: 18px;
    border-top: 1px dashed #d9e4f0;
  }

  .sub-title { margin-bottom: 12px; color: #0f172a; font-weight: 600; }
  .sos-side { display: flex; flex-direction: column; align-items: flex-end; }
  .drawer-toolbar { justify-content: flex-end; margin-bottom: 14px; }

  @media (max-width: 1400px) {
    .profile-grid-section,
    .main-grid,
    .table-grid,
    .hotline-grid { grid-template-columns: 1fr; }
  }

  @media (max-width: 900px) {
    .hero-panel { flex-direction: column; }
    .hero-actions { justify-content: flex-start; min-width: 0; }
  }
}
</style>
