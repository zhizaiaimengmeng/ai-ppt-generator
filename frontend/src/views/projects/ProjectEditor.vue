<template>
  <div class="project-editor">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button @click="goBack">
          <el-icon><arrow-left /></el-icon>
          返回
        </el-button>
        <el-input
          v-model="projectTitle"
          placeholder="请输入标题"
          class="title-input"
          @blur="saveProject"
        />
      </div>
      
      <div class="toolbar-center">
        <el-button-group>
          <el-button @click="addSlide" title="添加幻灯片">
            <el-icon><plus /></el-icon>
          </el-button>
          <el-button @click="duplicateSlide" title="复制幻灯片">
            <el-icon><copy-document /></el-icon>
          </el-button>
          <el-button @click="deleteSlide" title="删除幻灯片" :disabled="currentSlideIndex < 0">
            <el-icon><delete /></el-icon>
          </el-button>
        </el-button-group>
        
        <el-divider direction="vertical" />
        
        <el-select v-model="currentSlide.layoutType" placeholder="选择布局" size="small" @change="updateSlide">
          <el-option label="标题页" value="title" />
          <el-option label="内容页" value="content" />
          <el-option label="图文页" value="image-content" />
          <el-option label="列表页" value="list" />
          <el-option label="结束页" value="end" />
        </el-select>
      </div>
      
      <div class="toolbar-right">
        <el-button @click="autoSave" :loading="saving">
          <el-icon><save /></el-icon>
          {{ saving ? '保存中...' : '保存' }}
        </el-button>
        <el-button type="primary" @click="showExportDialog = true">
          <el-icon><download /></el-icon>
          导出
        </el-button>
      </div>
    </div>
    
    <div class="editor-content">
      <!-- 左侧幻灯片列表 -->
      <div class="slide-thumbnails">
        <div class="thumbnails-header">
          <span>幻灯片 ({{ slides.length }})</span>
        </div>
        
        <el-scrollbar>
          <div
            v-for="(slide, index) in slides"
            :key="slide.id || index"
            :class="['thumbnail-item', { active: currentSlideIndex === index }]"
            @click="selectSlide(index)"
          >
            <div class="thumbnail-number">{{ index + 1 }}</div>
            <div class="thumbnail-preview">
              <div class="thumbnail-title">{{ slide.title || '无标题' }}</div>
            </div>
          </div>
        </el-scrollbar>
      </div>
      
      <!-- 中间编辑区域 -->
      <div class="slide-editor">
        <el-scrollbar>
          <div class="slide-canvas">
            <div class="slide-content" v-if="currentSlide">
              <el-input
                v-model="currentSlide.title"
                placeholder="幻灯片标题"
                class="slide-title-input"
                @input="debouncedUpdate"
              />
              
              <div class="slide-body">
                <div v-if="currentSlide.content">
                  <el-input
                    v-model="currentSlide.content.points"
                    type="textarea"
                    :rows="10"
                    placeholder="幻灯片内容（每行一个要点）"
                    @input="debouncedUpdate"
                  />
                </div>
              </div>
            </div>
            
            <el-empty v-else description="请选择幻灯片" />
          </div>
        </el-scrollbar>
      </div>
      
      <!-- 右侧属性面板 -->
      <div class="properties-panel">
        <h4>幻灯片属性</h4>
        
        <el-form label-width="80px" size="small">
          <el-form-item label="布局">
            <el-select v-model="currentSlide.layoutType" @change="updateSlide">
              <el-option label="标题页" value="title" />
              <el-option label="内容页" value="content" />
              <el-option label="图文页" value="image-content" />
              <el-option label="列表页" value="list" />
              <el-option label="结束页" value="end" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="背景色">
            <el-color-picker v-model="currentSlide.backgroundColor" @change="updateSlide" />
          </el-form-item>
          
          <el-form-item label="幻灯片">
            <span>{{ currentSlideIndex + 1 }} / {{ slides.length }}</span>
          </el-form-item>
        </el-form>
        
        <el-divider />
        
        <h4>操作</h4>
        <el-space direction="vertical" fill style="width: 100%">
          <el-button @click="moveSlideUp" :disabled="currentSlideIndex <= 0" style="width: 100%">
            <el-icon><arrow-up /></el-icon>
            上移
          </el-button>
          <el-button @click="moveSlideDown" :disabled="currentSlideIndex >= slides.length - 1" style="width: 100%">
            <el-icon><arrow-down /></el-icon>
            下移
          </el-button>
        </el-space>
      </div>
    </div>
    
    <!-- 导出对话框 -->
    <el-dialog v-model="showExportDialog" title="导出 PPT" width="500px">
      <el-form label-width="100px">
        <el-form-item label="导出格式">
          <el-radio-group v-model="exportFormat">
            <el-radio label="pptx">PPTX</el-radio>
            <el-radio label="pdf">PDF</el-radio>
            <el-radio label="link">在线链接</el-radio>
            <el-radio label="image">长图</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="有效期" v-if="exportFormat === 'link'">
          <el-select v-model="expirationDays">
            <el-option label="7 天" :value="7" />
            <el-option label="14 天" :value="14" />
            <el-option label="30 天" :value="30" />
          </el-select>
        </el-form-item>
      </el-form>
      
          <template #footer>
        <el-button @click="showExportDialog = false">取消</el-button>
        <el-button type="primary" @click="handleExport" :loading="exporting">
          导出
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 导出进度对话框 -->
    <el-dialog v-model="showExportProgress" title="导出进度" width="500px" :close-on-click-modal="false">
      <ExportProgress
        :export-id="currentExportId"
        @complete="handleExportComplete"
        @close="showExportProgress = false"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getProjectDetailApi, updateSlidesApi } from '@/api/project'
import { exportPPTApi } from '@/api/export'
import ExportProgress from '@/components/ExportProgress.vue'
import type { Slide } from '@/types/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  Plus,
  CopyDocument,
  Delete,
  Save,
  Download,
  ArrowUp,
  ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const projectId = computed(() => Number(route.params.id))

// 项目数据
const projectTitle = ref('')
const slides = ref<Slide[]>([])
const currentSlideIndex = ref(-1)

// 编辑状态
const saving = ref(false)
const hasUnsavedChanges = ref(false)
let autoSaveTimer: any = null

// 导出相关
const showExportDialog = ref(false)
const exportFormat = ref('pptx')
const expirationDays = ref(7)
const exporting = ref(false)
const showExportProgress = ref(false)
const currentExportId = ref(0)

// 当前幻灯片
const currentSlide = computed(() => {
  if (currentSlideIndex.value >= 0 && currentSlideIndex.value < slides.value.length) {
    return slides.value[currentSlideIndex.value]
  }
  return null
})

// 加载项目
const loadProject = async () => {
  try {
    const response = await getProjectDetailApi(projectId.value)
    const project = response.data
    
    projectTitle.value = project.title
    slides.value = project.slides || []
    
    if (slides.value.length > 0) {
      currentSlideIndex.value = 0
    }
  } catch (error) {
    console.error('加载项目失败:', error)
    ElMessage.error('加载项目失败')
  }
}

// 选择幻灯片
const selectSlide = (index: number) => {
  currentSlideIndex.value = index
}

// 添加幻灯片
const addSlide = () => {
  const newSlide: Slide = {
    id: 0,
    projectId: projectId.value,
    slideNumber: slides.value.length + 1,
    layoutType: 'content',
    title: '新幻灯片',
    content: { points: ['要点 1', '要点 2', '要点 3'] },
    backgroundColor: '#ffffff',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  }
  
  slides.value.push(newSlide)
  currentSlideIndex.value = slides.value.length - 1
  hasUnsavedChanges.value = true
  
  ElMessage.success('已添加幻灯片')
}

// 复制幻灯片
const duplicateSlide = () => {
  if (currentSlideIndex.value < 0) {
    ElMessage.warning('请先选择要复制的幻灯片')
    return
  }
  
  const sourceSlide = slides.value[currentSlideIndex.value]
  const newSlide: Slide = {
    ...sourceSlide,
    id: 0,
    slideNumber: slides.value.length + 1,
    title: sourceSlide.title + ' (副本)'
  }
  
  slides.value.splice(currentSlideIndex.value + 1, 0, newSlide)
  currentSlideIndex.value++
  hasUnsavedChanges.value = true
  
  ElMessage.success('已复制幻灯片')
}

// 删除幻灯片
const deleteSlide = () => {
  if (currentSlideIndex.value < 0) {
    ElMessage.warning('请先选择要删除的幻灯片')
    return
  }
  
  ElMessageBox.confirm('确定要删除这张幻灯片吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    slides.value.splice(currentSlideIndex.value, 1)
    
    if (currentSlideIndex.value >= slides.value.length) {
      currentSlideIndex.value = Math.max(0, slides.value.length - 1)
    }
    
    hasUnsavedChanges.value = true
    ElMessage.success('已删除幻灯片')
  }).catch(() => {})
}

// 上移幻灯片
const moveSlideUp = () => {
  if (currentSlideIndex.value <= 0) return
  
  const temp = slides.value[currentSlideIndex.value]
  slides.value[currentSlideIndex.value] = slides.value[currentSlideIndex.value - 1]
  slides.value[currentSlideIndex.value - 1] = temp
  
  currentSlideIndex.value--
  hasUnsavedChanges.value = true
}

// 下移幻灯片
const moveSlideDown = () => {
  if (currentSlideIndex.value >= slides.value.length - 1) return
  
  const temp = slides.value[currentSlideIndex.value]
  slides.value[currentSlideIndex.value] = slides.value[currentSlideIndex.value + 1]
  slides.value[currentSlideIndex.value + 1] = temp
  
  currentSlideIndex.value++
  hasUnsavedChanges.value = true
}

// 更新幻灯片
const updateSlide = () => {
  hasUnsavedChanges.value = true
}

// 防抖更新
let updateTimer: any = null
const debouncedUpdate = () => {
  if (updateTimer) {
    clearTimeout(updateTimer)
  }
  updateTimer = setTimeout(() => {
    hasUnsavedChanges.value = true
  }, 500)
}

// 自动保存
const autoSave = async () => {
  if (!hasUnsavedChanges.value) {
    ElMessage.info('没有需要保存的更改')
    return
  }
  
  saving.value = true
  
  try {
    // 更新幻灯片编号
    slides.value.forEach((slide, index) => {
      slide.slideNumber = index + 1
    })
    
    await updateSlidesApi(projectId.value, slides.value)
    hasUnsavedChanges.value = false
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 保存项目
const saveProject = async () => {
  // TODO: 实现更新项目标题的 API
  console.log('保存项目:', projectTitle.value)
}

// 导出
const handleExport = async () => {
  exporting.value = true
  
  try {
    const response = await exportPPTApi(projectId.value, exportFormat.value)
    
    if (response.data.exportId) {
      currentExportId.value = response.data.exportId
      showExportDialog.value = false
      showExportProgress.value = true
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

const handleExportComplete = () => {
  // 导出完成后的处理
  showExportProgress.value = false
  ElMessage.success('导出完成，文件已开始下载')
}

// 返回
const goBack = () => {
  if (hasUnsavedChanges.value) {
    ElMessageBox.confirm('有未保存的更改，确定要离开吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      router.push('/projects')
    }).catch(() => {})
  } else {
    router.push('/projects')
  }
}

// 自动保存定时器
const startAutoSave = () => {
  autoSaveTimer = setInterval(() => {
    if (hasUnsavedChanges.value) {
      autoSave()
    }
  }, 30000) // 30 秒
}

onMounted(() => {
  loadProject()
  startAutoSave()
})

onUnmounted(() => {
  if (autoSaveTimer) {
    clearInterval(autoSaveTimer)
  }
})
</script>

<style lang="scss" scoped>
.project-editor {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.toolbar {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  
  .toolbar-left,
  .toolbar-center,
  .toolbar-right {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .title-input {
    width: 300px;
    margin-left: 10px;
  }
}

.editor-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.slide-thumbnails {
  width: 220px;
  background: #fff;
  border-right: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
  
  .thumbnails-header {
    height: 40px;
    display: flex;
    align-items: center;
    padding: 0 15px;
    border-bottom: 1px solid #e6e6e6;
    font-weight: 500;
  }
  
  .thumbnail-item {
    padding: 10px;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    transition: all 0.2s;
    
    &:hover {
      background: #f5f7fa;
    }
    
    &.active {
      background: #ecf5ff;
      border-left: 3px solid #409eff;
    }
    
    .thumbnail-number {
      font-size: 12px;
      color: #909399;
      margin-bottom: 5px;
    }
    
    .thumbnail-preview {
      background: #fff;
      border: 1px solid #e6e6e6;
      border-radius: 4px;
      padding: 10px;
      min-height: 100px;
      
      .thumbnail-title {
        font-size: 12px;
        color: #333;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}

.slide-editor {
  flex: 1;
  padding: 20px;
  overflow: hidden;
  
  .slide-canvas {
    background: #fff;
    border: 1px solid #e6e6e6;
    border-radius: 8px;
    padding: 40px;
    min-height: 500px;
    max-width: 1000px;
    margin: 0 auto;
  }
  
  .slide-content {
    .slide-title-input {
      font-size: 24px;
      font-weight: bold;
      margin-bottom: 30px;
    }
    
    .slide-body {
      font-size: 16px;
      line-height: 1.8;
    }
  }
}

.properties-panel {
  width: 250px;
  background: #fff;
  border-left: 1px solid #e6e6e6;
  padding: 20px;
  overflow-y: auto;
  
  h4 {
    margin-bottom: 15px;
    color: #333;
    font-size: 14px;
  }
}
</style>
