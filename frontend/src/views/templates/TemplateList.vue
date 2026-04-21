<template>
  <div class="template-list-page">
    <div class="header">
      <div class="header-left">
        <h2>模板库</h2>
        <el-button 
          type="primary" 
          size="small"
          @click="showStyleSelector = true"
        >
          <el-icon><grid /></el-icon>
          选择风格
        </el-button>
      </div>
      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索模板..."
          prefix-icon="Search"
          clearable
          @input="handleSearch"
        />
        <el-select
          v-model="category"
          placeholder="全部分类"
          clearable
          @change="handleSearch"
        >
          <el-option label="商务" value="business" />
          <el-option label="科技感" value="tech" />
          <el-option label="教育风" value="education" />
          <el-option label="简约风" value="minimal" />
        </el-select>
      </div>
    </div>
    
    <div class="template-grid">
      <el-card
        v-for="template in templates"
        :key="template.id"
        class="template-card"
        @click="selectTemplate(template)"
      >
        <div class="template-preview">
          <img 
            v-if="template.previewUrl && !template.previewUrl.startsWith('/api/')"
            :src="template.previewUrl" 
            :alt="template.name"
            class="preview-image"
          />
          <img 
            v-else-if="template.previewUrl"
            :src="template.previewUrl + '?token=' + localStorage.getItem('token')"
            :alt="template.name"
            class="preview-image"
          />
          <div v-else class="placeholder-preview">
            <el-icon size="60" color="#909399"><picture /></el-icon>
          </div>
          <div class="template-overlay">
            <el-tag v-if="template.isPremium" type="warning" size="small">付费</el-tag>
            <el-tag v-else type="success" size="small">免费</el-tag>
          </div>
        </div>
        
        <div class="template-info">
          <h3>{{ template.name }}</h3>
          <p class="description">{{ template.description }}</p>
          <div class="template-footer">
            <span class="stats">
              <el-icon><download /></el-icon>
              {{ template.downloadCount || 0 }}
            </span>
            <span class="stats">
              <el-icon><star /></el-icon>
              {{ template.favoriteCount || 0 }}
            </span>
            <el-button
              :icon="template.isFavorited ? 'Star' : 'Star'"
              :type="template.isFavorited ? 'warning' : ''"
              size="small"
              circle
              @click.stop="toggleFavorite(template)"
            />
          </div>
        </div>
      </el-card>
    </div>
    
    <el-empty v-if="templates.length === 0" description="暂无模板" />
    
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next"
        @current-change="loadTemplates"
        @size-change="loadTemplates"
      />
    </div>
    
    <!-- 风格选择器对话框 -->
    <el-dialog
      v-model="showStyleSelector"
      title="选择模板风格"
      width="800px"
    >
      <div class="style-selector">
        <div
          v-for="style in templateStyles"
          :key="style.id"
          class="style-card"
          :class="{ active: selectedStyle === style.id }"
          @click="selectedStyle = style.id"
        >
          <div class="style-icon">
            <el-icon size="48" :color="getStyleColor(style.id)">
              <component :is="getStyleIcon(style.id)" />
            </el-icon>
          </div>
          <h4>{{ style.name }}</h4>
          <p>{{ style.description }}</p>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showStyleSelector = false">取消</el-button>
        <el-button type="primary" @click="applyStyleFilter">应用</el-button>
      </template>
    </el-dialog>
    
    <!-- 模板布局预览对话框 -->
    <el-dialog
      v-model="showLayoutPreview"
      :title="selectedTemplate?.name + ' - 布局预览'"
      width="1000px"
    >
      <div class="layout-preview" v-if="selectedLayouts.length > 0">
        <div 
          v-for="(layout, index) in selectedLayouts" 
          :key="index"
          class="layout-card"
        >
          <div class="layout-header">
            <span class="layout-number">{{ index + 1 }}</span>
            <span class="layout-type">{{ layout.layoutType }}</span>
            <span class="layout-name">{{ layout.name }}</span>
          </div>
          <div 
            class="layout-thumbnail"
            :style="{ backgroundColor: layout.backgroundColor || '#fff' }"
          >
            <div 
              v-if="layout.gradient"
              class="gradient-overlay"
              :style="getGradientStyle(layout.gradient)"
            ></div>
            <div class="layout-elements">
              <div
                v-for="(element, elemIndex) in layout.elements"
                :key="elemIndex"
                class="element-placeholder"
                :class="['element-' + element.type]"
              >
                <span class="element-label">{{ getElementTypeLabel(element.type) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showLayoutPreview = false">关闭</el-button>
        <el-button type="primary" @click="confirmUseTemplate">使用此模板</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  getTemplatesApi, 
  favoriteTemplateApi, 
  unfavoriteTemplateApi,
  getTemplateLayoutsApi,
  getTemplateStylesApi
} from '@/api/template'
import type { Template } from '@/types/api'
import type { TemplateLayout } from '@/types/template'
import { 
  Picture, 
  Download, 
  Star, 
  Search, 
  Grid,
  OfficeBuilding,
  Monitor,
  Reading,
  Delete
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

const templates = ref<Template[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(12)
const keyword = ref('')
const category = ref('')

const showStyleSelector = ref(false)
const selectedStyle = ref('')
const templateStyles = ref<any[]>([])

const showLayoutPreview = ref(false)
const selectedTemplate = ref<Template | null>(null)
const selectedLayouts = ref<TemplateLayout[]>([])

const loadTemplates = async () => {
  try {
    const response = await getTemplatesApi({
      page: page.value,
      size: size.value,
      category: category.value || undefined,
      keyword: keyword.value || undefined
    })
    
    templates.value = response.records
    total.value = response.total
  } catch (error: any) {
    ElMessage.error(error.message || '加载模板失败')
  }
}

const handleSearch = () => {
  page.value = 1
  loadTemplates()
}

const selectTemplate = async (template: Template) => {
  try {
    // 获取模板布局预览 - 使用 template.category（后端返回的是英文）
    const layouts = await getTemplateLayoutsApi(template.category || 'business')
    selectedTemplate.value = template
    selectedLayouts.value = layouts
    showLayoutPreview.value = true
  } catch (error: any) {
    ElMessage.error('加载模板布局失败：' + (error.message || '未知错误'))
  }
}

const toggleFavorite = async (template: Template) => {
  try {
    if (template.isFavorited) {
      await unfavoriteTemplateApi(template.id)
      template.isFavorited = false
      template.favoriteCount = Math.max(0, (template.favoriteCount || 0) - 1)
      ElMessage.success('取消收藏成功')
    } else {
      await favoriteTemplateApi(template.id)
      template.isFavorited = true
      template.favoriteCount = (template.favoriteCount || 0) + 1
      ElMessage.success('收藏成功')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

const loadTemplateStyles = async () => {
  try {
    const response = await getTemplateStylesApi()
    templateStyles.value = response
  } catch (error) {
    console.error('加载模板风格失败', error)
  }
}

const getStyleIcon = (styleId: string) => {
  const iconMap: Record<string, any> = {
    business: OfficeBuilding,
    tech: Monitor,
    education: Reading,
    minimal: Delete
  }
  return iconMap[styleId] || OfficeBuilding
}

const getStyleColor = (styleId: string) => {
  const colorMap: Record<string, string> = {
    business: '#667eea',
    tech: '#00d2ff',
    education: '#56ab2f',
    minimal: '#333333'
  }
  return colorMap[styleId] || '#667eea'
}

const applyStyleFilter = () => {
  category.value = selectedStyle.value
  showStyleSelector.value = false
  loadTemplates()
}

const getGradientStyle = (gradient: any) => {
  if (!gradient || !gradient.colors || gradient.colors.length === 0) {
    return {}
  }
  
  const direction = gradient.direction || '45deg'
  const colors = gradient.colors.join(', ')
  
  return {
    background: `linear-gradient(${direction}, ${colors})`
  }
}

const getElementTypeLabel = (type: string) => {
  const labelMap: Record<string, string> = {
    title: '标题',
    subtitle: '副标题',
    content: '内容',
    list: '列表',
    'numbered-list': '编号列表',
    image: '图片',
    chart: '图表',
    icon: '图标',
    'thank-you': '致谢',
    contact: '联系信息',
    logo: 'Logo',
    footer: '页脚'
  }
  return labelMap[type] || type
}

const confirmUseTemplate = () => {
  showLayoutPreview.value = false
  // 跳转到创建 PPT 页面，带上模板 ID
  if (selectedTemplate.value) {
    router.push({
      path: '/ppt/create',
      query: { templateId: selectedTemplate.value.id }
    })
  }
}

onMounted(() => {
  loadTemplates()
  loadTemplateStyles()
})
</script>

<style scoped>
.template-list-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header h2 {
  margin: 0;
  font-size: 24px;
}

.search-bar {
  display: flex;
  gap: 10px;
}

.search-bar .el-input {
  width: 300px;
}

.search-bar .el-select {
  width: 150px;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.template-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.template-card:hover {
  transform: translateY(-5px);
}

.template-preview {
  position: relative;
  height: 180px;
  background: #f5f7fa;
  border-radius: 4px;
  overflow: hidden;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder-preview {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.template-overlay {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  gap: 5px;
}

.template-info h3 {
  margin: 10px 0 5px;
  font-size: 16px;
}

.description {
  font-size: 13px;
  color: #606266;
  margin: 8px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.template-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.stats {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #909399;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.style-selector {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.style-card {
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.style-card:hover {
  border-color: #409EFF;
  background: #f5f7fa;
}

.style-card.active {
  border-color: #409EFF;
  background: #ecf5ff;
}

.style-icon {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}

.style-card h4 {
  margin: 10px 0 5px;
}

.style-card p {
  font-size: 13px;
  color: #606266;
}

.layout-preview {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
  max-height: 600px;
  overflow-y: auto;
}

.layout-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.layout-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  background: #f5f7fa;
  font-size: 12px;
}

.layout-number {
  font-weight: bold;
  color: #409EFF;
}

.layout-type {
  color: #606266;
}

.layout-name {
  margin-left: auto;
  color: #909399;
}

.layout-thumbnail {
  height: 150px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0.8;
}

.layout-elements {
  position: relative;
  width: 90%;
  height: 80%;
  z-index: 1;
}

.element-placeholder {
  position: absolute;
  background: rgba(255, 255, 255, 0.3);
  border: 1px dashed rgba(255, 255, 255, 0.6);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: rgba(255, 255, 255, 0.8);
}

.element-title {
  top: 5%;
  left: 10%;
  width: 80%;
  height: 15%;
}

.element-subtitle {
  top: 25%;
  left: 20%;
  width: 60%;
  height: 10%;
}

.element-content,
.element-list {
  top: 45%;
  left: 10%;
  width: 80%;
  height: 40%;
}

.element-image,
.element-chart {
  top: 45%;
  left: 10%;
  width: 40%;
  height: 40%;
}

.element-thank-you {
  top: 40%;
  left: 20%;
  width: 60%;
  height: 20%;
}

.element-label {
  padding: 2px 6px;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 3px;
}
</style>
