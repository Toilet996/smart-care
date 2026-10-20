<template>
  <div class="app-container ai-care-page">
    <el-row :gutter="16" class="mb16">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-label">接入模型</div>
          <div class="summary-value">{{ providerLabel }}</div>
          <div class="summary-tip">支持通义千问 / 文心一言（千帆）</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-label">当前老人</div>
          <div class="summary-value">{{ form.elderlyName || '--' }}</div>
          <div class="summary-tip">可从老人档案跳转后自动带入</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-label">分析维度</div>
          <div class="summary-value">3 项</div>
          <div class="summary-tip">健康评估、饮食建议、随访干预</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="summary-card">
          <div class="summary-label">调用状态</div>
          <div class="summary-value" :class="{ success: !!result.content, pending: loading }">
            {{ loading ? '生成中' : result.content ? '已生成' : '待生成' }}
          </div>
          <div class="summary-tip">调用前请先在后端配置 API Key</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :xl="11">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>AI 评估输入</span>
              <el-tag type="info" effect="light">后端统一调用大模型接口</el-tag>
            </div>
          </template>

          <el-form ref="aiCareRef" :model="form" :rules="rules" label-width="100px">
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="模型渠道" prop="provider">
                  <el-select v-model="form.provider" style="width: 100%">
                    <el-option label="通义千问" value="qwen" />
                    <el-option label="文心一言" value="qianfan" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="模型名称">
                  <el-input v-model="form.model" placeholder="留空则走后端默认模型" clearable />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="选择老人" prop="elderlyId">
              <el-select
                v-model="form.elderlyId"
                filterable
                remote
                reserve-keyword
                clearable
                placeholder="请输入老人姓名搜索"
                :remote-method="remoteSearchElderly"
                :loading="elderlyLoading"
                style="width: 100%"
                @change="handleElderlyChange"
              >
                <el-option
                  v-for="item in elderlyOptions"
                  :key="item.elderlyId"
                  :label="`${item.elderlyName}（ID:${item.elderlyId}）`"
                  :value="item.elderlyId"
                />
              </el-select>
            </el-form-item>

            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="老人姓名" prop="elderlyName">
                  <el-input v-model="form.elderlyName" placeholder="自动带入，可手动调整" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="年龄">
                  <el-input-number v-model="form.age" :min="0" :max="130" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="性别">
                  <el-select v-model="form.gender" style="width: 100%" clearable>
                    <el-option v-for="dict in sys_user_sex" :key="dict.value" :label="dict.label" :value="dict.value" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="健康状态" prop="healthStatus">
              <el-input v-model="form.healthStatus" type="textarea" :rows="3" placeholder="例如：高血压2级，夜间睡眠浅，近期食欲一般" />
            </el-form-item>

            <el-form-item label="生命体征摘要" prop="vitalSummary">
              <el-input v-model="form.vitalSummary" type="textarea" :rows="3" placeholder="例如：近3日平均心率 78，夜间呼吸波动偏大，凌晨2点后活动增加" />
            </el-form-item>

            <el-form-item label="预警摘要" prop="alertSummary">
              <el-input v-model="form.alertSummary" type="textarea" :rows="3" placeholder="例如：近7天夜间离床 4 次，1 次如厕超时，暂无三级紧急预警" />
            </el-form-item>

            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="过敏禁忌">
                  <el-input v-model="form.allergyInfo" placeholder="例如：海鲜过敏、低盐饮食" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="饮食偏好">
                  <el-input v-model="form.dietPreference" placeholder="例如：偏软食、喜欢粥类和炖菜" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="医生建议">
              <el-input v-model="form.doctorAdvice" type="textarea" :rows="2" placeholder="例如：控制盐摄入，增加优质蛋白，关注夜间血压波动" />
            </el-form-item>

            <el-form-item label="补充要求">
              <el-input v-model="form.extraPrompt" type="textarea" :rows="3" placeholder="例如：请额外强调家属照护重点和社区随访频率" />
            </el-form-item>

            <div class="action-row">
              <el-button type="primary" :loading="loading" @click="submitGenerate">生成 AI 评估</el-button>
              <el-button @click="fillDemoData">填充演示数据</el-button>
              <el-button @click="resetForm">重置</el-button>
              <el-button link type="primary" @click="goToHealthReport">查看健康报告页</el-button>
            </div>
          </el-form>
        </el-card>
      </el-col>

      <el-col :xs="24" :xl="13">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>AI 生成结果</span>
              <div class="header-actions">
                <el-tag v-if="result.provider" type="success" effect="light">{{ providerText(result.provider) }}</el-tag>
                <el-tag v-if="result.model" type="info" effect="light">{{ result.model }}</el-tag>
              </div>
            </div>
          </template>

          <el-empty v-if="!result.content && !loading" description="填写老人信息后生成 AI 健康评估与饮食建议" />

          <div v-else v-loading="loading">
            <el-row :gutter="12" class="mb12">
              <el-col :span="24">
                <el-card shadow="hover" class="result-card">
                  <template #header><span>健康评估报告</span></template>
                  <div class="result-content">{{ result.healthReport || '模型尚未返回该部分内容' }}</div>
                </el-card>
              </el-col>
            </el-row>

            <el-row :gutter="12" class="mb12">
              <el-col :span="24">
                <el-card shadow="hover" class="result-card">
                  <template #header><span>饮食建议</span></template>
                  <div class="result-content">{{ result.dietAdvice || '模型尚未返回该部分内容' }}</div>
                </el-card>
              </el-col>
            </el-row>

            <el-row :gutter="12">
              <el-col :span="24">
                <el-card shadow="hover" class="result-card">
                  <template #header><span>随访与干预建议</span></template>
                  <div class="result-content">{{ result.followUpAdvice || '模型尚未返回该部分内容' }}</div>
                </el-card>
              </el-col>
            </el-row>

            <el-collapse class="raw-result">
              <el-collapse-item title="查看模型原始输出" name="raw">
                <div class="result-content raw-text">{{ result.content }}</div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="AiCare">
import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { generateAiCareReport } from '@/api/care/aiCare'
import { getElderly, listElderly } from '@/api/care/elderly'

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()
const { sys_user_sex } = proxy.useDict('sys_user_sex')

const aiCareRef = ref()
const elderlyOptions = ref([])
const elderlyLoading = ref(false)
const loading = ref(false)

const data = reactive({
  form: {
    provider: 'qwen',
    model: '',
    elderlyId: null,
    elderlyName: '',
    age: null,
    gender: '',
    healthStatus: '',
    vitalSummary: '',
    alertSummary: '',
    allergyInfo: '',
    dietPreference: '',
    doctorAdvice: '',
    extraPrompt: ''
  },
  result: {
    provider: '',
    model: '',
    content: '',
    healthReport: '',
    dietAdvice: '',
    followUpAdvice: ''
  },
  rules: {
    provider: [{ required: true, message: '请选择模型渠道', trigger: 'change' }],
    elderlyId: [{ required: true, message: '请选择老人', trigger: 'change' }],
    elderlyName: [{ required: true, message: '请输入老人姓名', trigger: 'blur' }],
    healthStatus: [{ required: true, message: '请输入健康状态', trigger: 'blur' }],
    vitalSummary: [{ required: true, message: '请输入生命体征摘要', trigger: 'blur' }],
    alertSummary: [{ required: true, message: '请输入预警摘要', trigger: 'blur' }]
  }
})

const { form, result, rules } = toRefs(data)

const providerLabel = computed(() => providerText(form.value.provider))

function providerText(provider) {
  return provider === 'qianfan' ? '文心一言' : '通义千问'
}

function resetResult() {
  result.value = {
    provider: '',
    model: '',
    content: '',
    healthReport: '',
    dietAdvice: '',
    followUpAdvice: ''
  }
}

function resetForm() {
  form.value = {
    provider: 'qwen',
    model: '',
    elderlyId: null,
    elderlyName: '',
    age: null,
    gender: '',
    healthStatus: '',
    vitalSummary: '',
    alertSummary: '',
    allergyInfo: '',
    dietPreference: '',
    doctorAdvice: '',
    extraPrompt: ''
  }
  elderlyOptions.value = []
  proxy.resetForm('aiCareRef')
  resetResult()
  applyRouteQuery()
}

function remoteSearchElderly(keyword) {
  elderlyLoading.value = true
  listElderly({
    pageNum: 1,
    pageSize: 20,
    elderlyName: keyword || undefined
  })
    .then((response) => {
      elderlyOptions.value = response.rows || []
    })
    .finally(() => {
      elderlyLoading.value = false
    })
}

function loadElderlyDetail(elderlyId) {
  if (!elderlyId) {
    return
  }
  getElderly(elderlyId).then((response) => {
    const info = response.data || {}
    form.value.elderlyId = info.elderlyId
    form.value.elderlyName = info.elderlyName || ''
    form.value.age = info.age
    form.value.gender = info.gender || ''
    form.value.healthStatus = info.healthStatus || form.value.healthStatus
    if (!elderlyOptions.value.find((item) => item.elderlyId === info.elderlyId)) {
      elderlyOptions.value = [info, ...elderlyOptions.value]
    }
  })
}

function handleElderlyChange(value) {
  if (!value) {
    form.value.elderlyName = ''
    return
  }
  const target = elderlyOptions.value.find((item) => item.elderlyId === value)
  if (target) {
    form.value.elderlyName = target.elderlyName || ''
    form.value.age = target.age
    form.value.gender = target.gender || ''
    form.value.healthStatus = target.healthStatus || form.value.healthStatus
  }
  loadElderlyDetail(value)
}

function fillDemoData() {
  if (!form.value.elderlyName) {
    form.value.elderlyName = '张三'
  }
  if (!form.value.age) {
    form.value.age = 78
  }
  if (!form.value.gender) {
    form.value.gender = '1'
  }
  form.value.healthStatus = form.value.healthStatus || '既往高血压，近期睡眠偏浅，白天活动量较上周略下降。'
  form.value.vitalSummary = form.value.vitalSummary || '近3日平均心率 76 次/分，呼吸频率 18 次/分，凌晨 2 点后离床活动增多。'
  form.value.alertSummary = form.value.alertSummary || '近7天出现 3 次夜间离床预警，1 次活动减少预警，无燃气和跌倒类紧急预警。'
  form.value.allergyInfo = form.value.allergyInfo || '低盐饮食，无明确药物过敏史。'
  form.value.dietPreference = form.value.dietPreference || '偏好软食、粥类、炖煮类菜品。'
  form.value.doctorAdvice = form.value.doctorAdvice || '建议控制钠盐摄入，适度补充优质蛋白，关注夜间血压和睡眠节律。'
  form.value.extraPrompt = form.value.extraPrompt || '请输出适合家属和社区工作人员共同查看的简明建议。'
}

function submitGenerate() {
  aiCareRef.value.validate((valid) => {
    if (!valid) {
      return
    }
    loading.value = true
    resetResult()
    generateAiCareReport(form.value)
      .then((response) => {
        result.value = response.data || {}
        proxy.$modal.msgSuccess('AI 健康评估生成成功')
      })
      .catch(() => {})
      .finally(() => {
        loading.value = false
      })
  })
}

function goToHealthReport() {
  router.push({
    path: '/smartcare/healthReport',
    query: {
      elderlyId: form.value.elderlyId || undefined,
      elderlyName: form.value.elderlyName || undefined
    }
  })
}

function applyRouteQuery() {
  if (route.query.provider) {
    form.value.provider = route.query.provider
  }
  if (route.query.elderlyId) {
    form.value.elderlyId = Number(route.query.elderlyId)
    loadElderlyDetail(form.value.elderlyId)
  }
  if (route.query.elderlyName) {
    form.value.elderlyName = route.query.elderlyName
  }
}

remoteSearchElderly('')
applyRouteQuery()
</script>

<style lang="scss" scoped>
.ai-care-page {
  .mb16 {
    margin-bottom: 16px;
  }

  .mb12 {
    margin-bottom: 12px;
  }

  .summary-card {
    border: 1px solid #e8eef5;

    .summary-label {
      color: #7a869a;
      font-size: 13px;
      margin-bottom: 10px;
    }

    .summary-value {
      color: #1f2d3d;
      font-size: 26px;
      font-weight: 700;
      line-height: 1.2;
    }

    .summary-value.success {
      color: #1f9d55;
    }

    .summary-value.pending {
      color: #d97706;
    }

    .summary-tip {
      color: #98a2b3;
      font-size: 12px;
      margin-top: 10px;
      min-height: 18px;
    }
  }

  .panel-card {
    min-height: 100%;
    border-radius: 14px;
  }

  .panel-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    font-weight: 600;
  }

  .header-actions {
    display: flex;
    gap: 8px;
    align-items: center;
  }

  .action-row {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: 8px;
  }

  .result-card {
    border-radius: 12px;
    background: linear-gradient(180deg, #fbfdff 0%, #f5f9ff 100%);
  }

  .result-content {
    white-space: pre-wrap;
    line-height: 1.8;
    color: #334155;
    font-size: 14px;
  }

  .raw-result {
    margin-top: 16px;
  }

  .raw-text {
    background: #f8fafc;
    border-radius: 10px;
    padding: 12px;
  }
}
</style>
