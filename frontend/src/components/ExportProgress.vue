<template>
  <div class="export-progress">
    <h3>导出进度</h3>
    
    <div class="progress-content">
      <el-progress
        :percentage="progress"
        :status="status"
        :stroke-width="20"
        :text-inside="true"
      />
      
      <div class="progress-message mt-20">
        <span v-if="status === 'pending'">准备导出...</span>
        <span v-else-if="status === 'processing'">正在生成文件，请稍候...</span>
        <span v-else-if="status === 'completed'" class="success">导出完成！</span>
        <span v-else-if="status === 'failed'" class="error">导出失败</span>
      </div>
      
      <div class="progress-actions mt-20" v-if="status === 'completed' && downloadUrl">
        <el-button type="primary" @click="handleDownload">
          <el-icon><download /></el-icon>
          下载文件
        </el-button>
      </div>
      
      <div class="progress-tips mt-10" v-if="status === 'processing'">
        <el-alert
          title="导出正在进行中"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <p>导出过程可能需要几分钟，您可以：</p>
            <ul>
              <li>关闭对话框，稍后在项目列表中查看导出结果</li>
              <li>继续编辑 PPT，导出会在后台完成</li>
            </ul>
          </template>
        </el-alert>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { getExportStatusApi } from '@/api/export'
import { ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'

const props = defineProps<{
  exportId: number
}>()

const emit = defineEmits<{
  (e: 'complete'): void
  (e: 'close'): void
}>()

const progress = ref(0)
const status = ref<pending | processing | completed | failed>('pending')
const downloadUrl = ref('')

let pollTimer: any = null

// 轮询导出状态
const pollStatus = async () => {
  try {
    const response = await getExportStatusApi(props.exportId)
    const data = response.data
    
    progress.value = data.progress || 0
    status.value = data.status as any
    downloadUrl.value = data.downloadUrl || ''
    
    if (data.status === 'completed' || data.status === 'failed') {
      if (pollTimer) {
        clearInterval(pollTimer)
      }
      
      if (data.status === 'completed') {
        ElMessage.success('导出完成')
        emit('complete')
      } else {
        ElMessage.error('导出失败')
      }
    }
  } catch (error) {
    console.error('获取导出状态失败:', error)
    ElMessage.error('获取导出状态失败')
  }
}

// 下载文件
const handleDownload = () => {
  if (downloadUrl.value) {
    window.open(downloadUrl.value, '_blank')
    ElMessage.success('开始下载')
  }
}

onMounted(() => {
  pollStatus()
  
  // 每 2 秒轮询一次
  pollTimer = setInterval(pollStatus, 2000)
})

onUnmounted(() => {
  if (pollTimer) {
    clearInterval(pollTimer)
  }
})
</script>

<style lang="scss" scoped>
.export-progress {
  padding: 20px;
  
  h3 {
    margin-bottom: 20px;
    color: #333;
  }
  
  .progress-content {
    text-align: center;
  }
  
  .progress-message {
    font-size: 14px;
    color: #606266;
    
    .success {
      color: #67c23a;
      font-weight: 500;
    }
    
    .error {
      color: #f56c6c;
      font-weight: 500;
    }
  }
  
  .progress-actions {
    display: flex;
    justify-content: center;
  }
  
  .progress-tips {
    ul {
      margin: 10px 0 0 20px;
      padding: 0;
      
      li {
        margin: 5px 0;
        color: #606266;
      }
    }
  }
}
</style>
