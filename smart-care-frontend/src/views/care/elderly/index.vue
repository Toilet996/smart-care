<template>
  <div class="app-container">
    <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch" label-width="82px">
      <el-form-item label="老人姓名"><el-input v-model="queryParams.elderlyName" clearable @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item label="年龄"><el-input v-model="queryParams.age" clearable @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item label="性别">
        <el-select v-model="queryParams.gender" clearable>
          <el-option v-for="dict in sys_user_sex" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="联系电话"><el-input v-model="queryParams.phone" clearable @keyup.enter="handleQuery" /></el-form-item>
      <el-form-item label="纳管状态">
        <el-select v-model="queryParams.managementStatus" clearable>
          <el-option v-for="item in managementStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" clearable>
          <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['care:elderly:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['care:elderly:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['care:elderly:remove']">删除</el-button></el-col>
      <el-col :span="1.5"><el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['care:elderly:export']">导出</el-button></el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="elderlyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="老人ID" align="center" prop="elderlyId" width="88" />
      <el-table-column label="老人姓名" align="center" prop="elderlyName" min-width="110" />
      <el-table-column label="年龄" align="center" prop="age" width="76" />
      <el-table-column label="性别" align="center" width="80"><template #default="{ row }"><dict-tag :options="sys_user_sex" :value="row.gender" /></template></el-table-column>
      <el-table-column label="联系电话" align="center" prop="phone" min-width="130" />
      <el-table-column label="纳管状态" align="center" width="120"><template #default="{ row }"><el-tag :type="managementStatusTagType(row.managementStatus)" effect="light">{{ managementStatusLabel(row.managementStatus) }}</el-tag></template></el-table-column>
      <el-table-column label="责任人" align="center" prop="responsiblePerson" min-width="110" />
      <el-table-column label="最近处置人" align="center" prop="latestHandler" min-width="120" />
      <el-table-column label="纳管开始" align="center" min-width="150"><template #default="{ row }">{{ formatDateTime(row.managementStartTime) }}</template></el-table-column>
      <el-table-column label="下次随访" align="center" min-width="150"><template #default="{ row }">{{ formatDateTime(row.nextVisitTime) }}</template></el-table-column>
      <el-table-column label="预警数" align="center" width="90"><template #default="{ row }"><el-button link type="danger" @click="goToAlertRecord(row)">{{ row.alertCount || 0 }}</el-button></template></el-table-column>
      <el-table-column label="未结工单" align="center" width="100"><template #default="{ row }"><el-button link type="warning" @click="goToWorkOrder(row)">{{ row.openOrderCount || 0 }}</el-button></template></el-table-column>
      <el-table-column label="跟进结果" align="center" prop="followUpResult" min-width="180" show-overflow-tooltip />
      <el-table-column label="解除原因" align="center" prop="releaseReason" min-width="180" show-overflow-tooltip />
      <el-table-column label="干预措施" align="center" prop="interventionPlan" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="300">
        <template #default="{ row }">
          <el-button link type="primary" @click="goToDevice(row)">设备</el-button>
          <el-button link type="danger" @click="goToAlertRecord(row)">预警</el-button>
          <el-button link type="warning" @click="goToWorkOrder(row)">工单</el-button>
          <el-button link type="success" @click="goToFocusManagement(row)">专项</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)" v-hasPermi="['care:elderly:edit']">修改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="860px" append-to-body>
      <el-form ref="elderlyRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="老人姓名" prop="elderlyName"><el-input v-model="form.elderlyName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="年龄" prop="age"><el-input v-model.number="form.age" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="性别" prop="gender"><el-select v-model="form.gender" style="width: 100%"><el-option v-for="dict in sys_user_sex" :key="dict.value" :label="dict.label" :value="dict.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="身份证号" prop="idCard"><el-input v-model="form.idCard" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="联系电话" prop="phone"><el-input v-model="form.phone" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="状态" prop="status"><el-select v-model="form.status" style="width: 100%"><el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" /></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="详细地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="健康状态"><el-input v-model="form.healthStatus" type="textarea" :rows="3" /></el-form-item>
        <el-divider content-position="left">专项管理</el-divider>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="纳管状态"><el-select v-model="form.managementStatus" style="width: 100%"><el-option v-for="item in managementStatusOptions" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="责任人"><el-input v-model="form.responsiblePerson" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="纳管开始"><el-date-picker v-model="form.managementStartTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="下次随访"><el-date-picker v-model="form.nextVisitTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="最近处置人"><el-input v-model="form.latestHandler" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="跟进结果"><el-input v-model="form.followUpResult" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="干预措施"><el-input v-model="form.interventionPlan" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="解除原因"><el-input v-model="form.releaseReason" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><div class="dialog-footer"><el-button type="primary" @click="submitForm">确定</el-button><el-button @click="cancel">取消</el-button></div></template>
    </el-dialog>
  </div>
</template>

<script setup name="Elderly">
import { getCurrentInstance, reactive, ref, toRefs, watch } from "vue"
import { useRoute, useRouter } from "vue-router"
import { addElderly, delElderly, getElderly, listElderly, updateElderly } from "@/api/care/elderly"
const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()
const { sys_user_sex, sys_normal_disable } = proxy.useDict("sys_user_sex", "sys_normal_disable")
const elderlyList = ref([]), open = ref(false), loading = ref(true), showSearch = ref(true), ids = ref([]), single = ref(true), multiple = ref(true), total = ref(0), title = ref("")
const managementStatusOptions = [{ label: "未纳管", value: "0" }, { label: "重点纳管", value: "1" }, { label: "高风险跟踪", value: "2" }, { label: "已解除", value: "3" }]
const data = reactive({ form: {}, queryParams: { pageNum: 1, pageSize: 10, elderlyName: null, age: null, gender: null, phone: null, managementStatus: null, status: null }, rules: { elderlyName: [{ required: true, message: "老人姓名不能为空", trigger: "blur" }], gender: [{ required: true, message: "请选择性别", trigger: "change" }], phone: [{ required: true, message: "联系电话不能为空", trigger: "blur" }], status: [{ required: true, message: "请选择状态", trigger: "change" }] } })
const { queryParams, form, rules } = toRefs(data)
function reset() { form.value = { elderlyId: null, elderlyName: null, age: null, gender: "0", idCard: null, phone: null, address: null, healthStatus: null, status: "0", managementStatus: "0", responsiblePerson: null, nextVisitTime: null, interventionPlan: null, followUpResult: null, latestHandler: null, managementStartTime: null, releaseReason: null, remark: null }; proxy.resetForm("elderlyRef") }
function getList() { loading.value = true; listElderly(queryParams.value).then((r) => { elderlyList.value = r.rows; total.value = r.total; loading.value = false }) }
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm("queryRef"); applyRouteQuery(); handleQuery() }
function handleSelectionChange(selection) { ids.value = selection.map((i) => i.elderlyId); single.value = selection.length !== 1; multiple.value = !selection.length }
function handleAdd() { reset(); open.value = true; title.value = "新增老人档案" }
function handleUpdate(row) { const elderlyId = row?.elderlyId || ids.value[0]; if (!elderlyId) return proxy.$modal.msgWarning("请先选择要修改的数据"); reset(); getElderly(elderlyId).then((r) => { form.value = { ...form.value, ...r.data }; open.value = true; title.value = "修改老人档案" }) }
function submitForm() { proxy.$refs.elderlyRef.validate((valid) => { if (!valid) return; const req = form.value.elderlyId ? updateElderly(form.value) : addElderly(form.value); req.then(() => { proxy.$modal.msgSuccess(form.value.elderlyId ? "修改成功" : "新增成功"); open.value = false; getList() }) }) }
function handleDelete(row) { const elderlyIds = row?.elderlyId ? [row.elderlyId] : ids.value; if (!elderlyIds.length) return proxy.$modal.msgWarning("请先选择要删除的数据"); proxy.$modal.confirm(`是否确认删除老人档案编号为“${elderlyIds.join(",")}”的数据项？`).then(() => delElderly(elderlyIds.join(","))).then(() => { proxy.$modal.msgSuccess("删除成功"); getList() }).catch(() => {}) }
function cancel() { open.value = false; reset() }
function handleExport() { proxy.download("care/elderly/export", { ...queryParams.value }, `elderly_${Date.now()}.xlsx`) }
function managementStatusLabel(value) { return managementStatusOptions.find((i) => i.value === String(value ?? "0"))?.label || "未纳管" }
function managementStatusTagType(value) { const v = String(value ?? "0"); if (v === "2") return "danger"; if (v === "1") return "warning"; if (v === "3") return "success"; return "info" }
function formatDateTime(value) { return value ? proxy.parseTime(value, "{y}-{m}-{d} {h}:{i}") : "--" }
function goToDevice(row) { router.push({ path: "/smartcare/device", query: { elderlyId: row.elderlyId, elderlyName: row.elderlyName } }) }
function goToAlertRecord(row) { router.push({ path: "/smartcare/alertRecord", query: { elderlyId: row.elderlyId, elderlyName: row.elderlyName } }) }
function goToWorkOrder(row) { router.push({ path: "/smartcare/workOrder", query: { elderlyId: row.elderlyId, elderlyName: row.elderlyName } }) }
function goToFocusManagement(row) { router.push({ path: "/smartcare/focusManagement", query: { elderlyId: row.elderlyId, elderlyName: row.elderlyName } }) }
function applyRouteQuery() { queryParams.value.elderlyName = route.query.elderlyName ?? null; queryParams.value.managementStatus = route.query.managementStatus ?? null }
watch(() => route.query, () => { applyRouteQuery(); getList() }, { deep: true })
reset(); applyRouteQuery(); getList()
</script>
