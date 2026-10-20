<template>
  <div class="app-container">
    <el-form
      ref="queryRef"
      :model="queryParams"
      :inline="true"
      v-show="showSearch"
      label-width="88px"
    >
      <el-form-item label="规则名称" prop="ruleName">
        <el-input
          v-model="queryParams.ruleName"
          placeholder="请输入规则名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备类型" prop="deviceType">
        <el-select v-model="queryParams.deviceType" placeholder="请选择设备类型" clearable>
          <el-option
            v-for="dict in biz_device_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="监测指标" prop="metricType">
        <el-select v-model="queryParams.metricType" placeholder="请选择监测指标" clearable>
          <el-option
            v-for="dict in biz_metric_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="判断条件" prop="conditionOp">
        <el-select v-model="queryParams.conditionOp" placeholder="请选择判断条件" clearable>
          <el-option
            v-for="dict in condition_op"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="预警级别" prop="alertLevel">
        <el-select v-model="queryParams.alertLevel" placeholder="请选择预警级别" clearable>
          <el-option
            v-for="dict in biz_alert_level"
            :key="dict.value"
            :label="dict.label"
            :value="Number(dict.value)"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="预警类型" prop="alertType">
        <el-select v-model="queryParams.alertType" placeholder="请选择预警类型" clearable>
          <el-option
            v-for="dict in biz_alert_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['care:alertRule:add']">
          新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate()"
          v-hasPermi="['care:alertRule:edit']"
        >
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete()"
          v-hasPermi="['care:alertRule:remove']"
        >
          删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['care:alertRule:export']"
        >
          导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="alertRuleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="规则ID" align="center" prop="ruleId" width="90" />
      <el-table-column label="规则名称" align="center" prop="ruleName" min-width="140" />
      <el-table-column label="设备类型" align="center" prop="deviceType" min-width="120">
        <template #default="scope">
          <dict-tag :options="biz_device_type" :value="scope.row.deviceType" />
        </template>
      </el-table-column>
      <el-table-column label="监测指标" align="center" prop="metricType" min-width="120">
        <template #default="scope">
          <dict-tag :options="biz_metric_type" :value="scope.row.metricType" />
        </template>
      </el-table-column>
      <el-table-column label="判断条件" align="center" prop="conditionOp" width="110">
        <template #default="scope">
          <dict-tag :options="condition_op" :value="scope.row.conditionOp" />
        </template>
      </el-table-column>
      <el-table-column label="阈值" align="center" prop="thresholdValue" width="100" />
      <el-table-column label="预警级别" align="center" prop="alertLevel" width="110">
        <template #default="scope">
          <dict-tag :options="biz_alert_level" :value="scope.row.alertLevel" />
        </template>
      </el-table-column>
      <el-table-column label="预警类型" align="center" prop="alertType" min-width="120">
        <template #default="scope">
          <dict-tag :options="biz_alert_type" :value="scope.row.alertType" />
        </template>
      </el-table-column>
      <el-table-column label="规则说明" align="center" prop="remark" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template #default="scope">
          <el-button
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['care:alertRule:edit']"
          >
            修改
          </el-button>
          <el-button
            link
            type="primary"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['care:alertRule:remove']"
          >
            删除
          </el-button>
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

    <el-dialog :title="title" v-model="open" width="580px" append-to-body>
      <el-form ref="alertRuleRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="form.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="设备类型" prop="deviceType">
          <el-select v-model="form.deviceType" placeholder="请选择设备类型">
            <el-option
              v-for="dict in biz_device_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="监测指标" prop="metricType">
          <el-select v-model="form.metricType" placeholder="请选择监测指标">
            <el-option
              v-for="dict in biz_metric_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="判断条件" prop="conditionOp">
          <el-select v-model="form.conditionOp" placeholder="请选择判断条件">
            <el-option
              v-for="dict in condition_op"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="阈值" prop="thresholdValue">
          <el-input v-model="form.thresholdValue" placeholder="请输入阈值" />
        </el-form-item>
        <el-form-item label="预警级别" prop="alertLevel">
          <el-select v-model="form.alertLevel" placeholder="请选择预警级别">
            <el-option
              v-for="dict in biz_alert_level"
              :key="dict.value"
              :label="dict.label"
              :value="Number(dict.value)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预警类型" prop="alertType">
          <el-select v-model="form.alertType" placeholder="请选择预警类型">
            <el-option
              v-for="dict in biz_alert_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="规则说明" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入规则说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="AlertRule">
import { addAlertRule, delAlertRule, getAlertRule, listAlertRule, updateAlertRule } from "@/api/care/alertRule"

const { proxy } = getCurrentInstance()
const { biz_device_type, biz_metric_type, condition_op, biz_alert_level, biz_alert_type } = proxy.useDict(
  "biz_device_type",
  "biz_metric_type",
  "condition_op",
  "biz_alert_level",
  "biz_alert_type"
)

const alertRuleList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    ruleName: null,
    deviceType: null,
    metricType: null,
    conditionOp: null,
    alertLevel: null,
    alertType: null
  },
  rules: {
    ruleName: [{ required: true, message: "规则名称不能为空", trigger: "blur" }],
    deviceType: [{ required: true, message: "设备类型不能为空", trigger: "change" }],
    metricType: [{ required: true, message: "监测指标不能为空", trigger: "change" }],
    conditionOp: [{ required: true, message: "判断条件不能为空", trigger: "change" }],
    thresholdValue: [{ required: true, message: "阈值不能为空", trigger: "blur" }],
    alertLevel: [{ required: true, message: "预警级别不能为空", trigger: "change" }],
    alertType: [{ required: true, message: "预警类型不能为空", trigger: "change" }]
  }
})

const { form, queryParams, rules } = toRefs(data)

function getList() {
  loading.value = true
  listAlertRule(queryParams.value)
    .then((response) => {
      alertRuleList.value = response.rows
      total.value = response.total
    })
    .finally(() => {
      loading.value = false
    })
}

function reset() {
  form.value = {
    ruleId: null,
    ruleName: null,
    deviceType: null,
    metricType: null,
    conditionOp: null,
    thresholdValue: null,
    alertLevel: null,
    status: "0",
    alertType: null,
    remark: null
  }
  proxy.resetForm("alertRuleRef")
}

function cancel() {
  open.value = false
  reset()
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.ruleId)
  single.value = selection.length !== 1
  multiple.value = selection.length === 0
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "新增预警规则"
}

function handleUpdate(row) {
  const ruleId = row?.ruleId || ids.value[0]
  if (!ruleId) {
    proxy.$modal.msgWarning("请选择需要修改的数据")
    return
  }
  reset()
  getAlertRule(ruleId).then((response) => {
    form.value = { ...response.data }
    open.value = true
    title.value = "修改预警规则"
  })
}

function submitForm() {
  proxy.$refs.alertRuleRef.validate((valid) => {
    if (!valid) {
      return
    }
    const request = form.value.ruleId ? updateAlertRule(form.value) : addAlertRule(form.value)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.ruleId ? "修改成功" : "新增成功")
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  const ruleIds = row?.ruleId ? [row.ruleId] : ids.value
  if (!ruleIds.length) {
    proxy.$modal.msgWarning("请先选择要删除的数据")
    return
  }
  proxy.$modal
    .confirm(`是否确认删除预警规则编号为“${ruleIds.join(",")}”的数据项？`)
    .then(() => delAlertRule(ruleIds.join(",")))
    .then(() => {
      proxy.$modal.msgSuccess("删除成功")
      getList()
    })
    .catch(() => {})
}

function handleExport() {
  proxy.download("care/alertRule/export", { ...queryParams.value }, `alertRule_${new Date().getTime()}.xlsx`)
}

getList()
</script>
