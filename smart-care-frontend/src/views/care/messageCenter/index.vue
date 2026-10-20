<template>
  <div class="app-container">
    <el-row :gutter="16" class="mb16">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">消息总数</div>
          <div class="stat-value">{{ stats.total || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">未读消息</div>
          <div class="stat-value text-warning">{{ stats.unread || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">重要消息</div>
          <div class="stat-value text-danger">{{ stats.urgent || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">今日新增</div>
          <div class="stat-value text-primary">{{ stats.today || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-form :model="queryParams" ref="queryRef" size="default" :inline="true" label-width="68px">
      <el-form-item label="标题" prop="messageTitle">
        <el-input v-model="queryParams.messageTitle" placeholder="请输入消息标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="老人姓名" prop="elderlyName">
        <el-input v-model="queryParams.elderlyName" placeholder="请输入老人姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="消息类型" prop="messageType">
        <el-select v-model="queryParams.messageType" placeholder="请选择消息类型" clearable style="width: 180px">
          <el-option label="预警消息" value="alert" />
          <el-option label="工单消息" value="workOrder" />
        </el-select>
      </el-form-item>
      <el-form-item label="阅读状态" prop="readStatus">
        <el-select v-model="queryParams.readStatus" placeholder="请选择阅读状态" clearable style="width: 180px">
          <el-option label="未读" value="0" />
          <el-option label="已读" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="success" icon="Finished" @click="handleReadAll">全部已读</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="messageList">
      <el-table-column label="标题" align="left" prop="messageTitle" min-width="240" show-overflow-tooltip />
      <el-table-column label="老人姓名" align="center" prop="elderlyName" width="120" />
      <el-table-column label="类型" align="center" prop="messageType" width="110">
        <template #default="scope">
          <el-tag v-if="scope.row.messageType === 'alert'" type="danger">预警</el-tag>
          <el-tag v-else type="warning">工单</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="等级" align="center" prop="messageLevel" width="110">
        <template #default="scope">
          <el-tag :type="levelTagType(scope.row.messageLevel)">{{ levelLabel(scope.row.messageLevel) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="内容" align="left" prop="messageContent" min-width="280" show-overflow-tooltip />
      <el-table-column label="时间" align="center" prop="createTime" width="180" />
      <el-table-column label="状态" align="center" prop="readStatus" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.readStatus === '1' ? 'info' : 'primary'">
            {{ scope.row.readStatus === '1' ? '已读' : '未读' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="handleDetail(scope.row)">详情</el-button>
          <el-button link type="success" @click="handleJump(scope.row)">跳转</el-button>
          <el-button v-if="scope.row.readStatus !== '1'" link type="warning" @click="handleRead(scope.row)">标记已读</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog v-model="open" title="消息详情" width="680px" append-to-body>
      <el-descriptions :column="1" border v-if="form.messageId">
        <el-descriptions-item label="标题">{{ form.messageTitle }}</el-descriptions-item>
        <el-descriptions-item label="老人姓名">{{ form.elderlyName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="消息类型">{{ form.messageType === 'alert' ? '预警消息' : '工单消息' }}</el-descriptions-item>
        <el-descriptions-item label="消息等级">{{ levelLabel(form.messageLevel) }}</el-descriptions-item>
        <el-descriptions-item label="消息内容">{{ form.messageContent }}</el-descriptions-item>
        <el-descriptions-item label="消息时间">{{ form.createTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="open = false">关闭</el-button>
          <el-button type="primary" @click="handleJump(form)">前往处理</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { useRouter } from 'vue-router'
import { getMessage, getMessageStats, listMessage, markAllMessageRead, markMessageRead } from '@/api/care/messageCenter'

const { proxy } = getCurrentInstance()
const router = useRouter()

const loading = ref(false)
const open = ref(false)
const total = ref(0)
const messageList = ref([])
const stats = ref({})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    messageTitle: undefined,
    elderlyName: undefined,
    messageType: undefined,
    readStatus: undefined
  }
})

const { form, queryParams } = toRefs(data)

function getList() {
  loading.value = true
  listMessage(queryParams.value).then(response => {
    messageList.value = response.rows
    total.value = response.total
  }).finally(() => {
    loading.value = false
  })
}

function loadStats() {
  getMessageStats().then(response => {
    stats.value = response.data || {}
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
  loadStats()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleDetail(row) {
  getMessage(row.messageId).then(response => {
    form.value = response.data || {}
    open.value = true
    if (row.readStatus !== '1') {
      handleRead(row, false)
    }
  })
}

function handleRead(row, refresh = true) {
  markMessageRead(row.messageId).then(() => {
    if (refresh) {
      proxy.$modal.msgSuccess('已标记为已读')
    }
    getList()
    loadStats()
  })
}

function handleReadAll() {
  markAllMessageRead().then(() => {
    proxy.$modal.msgSuccess('已全部标记为已读')
    getList()
    loadStats()
  })
}

function handleJump(row) {
  if (!row.relatedRoute) {
    proxy.$modal.msgWarning('当前消息没有关联跳转')
    return
  }
  if (row.readStatus !== '1') {
    handleRead(row, false)
  }
  router.push(row.relatedRoute)
}

function levelLabel(level) {
  if (String(level) === '2' || String(level) === '3') return '重要'
  if (String(level) === '1') return '关注'
  return '普通'
}

function levelTagType(level) {
  if (String(level) === '2' || String(level) === '3') return 'danger'
  if (String(level) === '1') return 'warning'
  return 'info'
}

getList()
loadStats()
</script>

<style scoped>
.stat-title {
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.stat-value {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 700;
}

.text-primary {
  color: #409eff;
}

.text-warning {
  color: #e6a23c;
}

.text-danger {
  color: #f56c6c;
}
</style>
