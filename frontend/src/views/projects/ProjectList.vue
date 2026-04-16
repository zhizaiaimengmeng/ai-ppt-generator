<template>
  <div class="project-list-page">
    <div class="header">
      <h2>我的项目</h2>
      <div class="header-actions">
        <el-input
          v-model="keyword"
          placeholder="搜索项目..."
          prefix-icon="Search"
          clearable
          style="width: 250px; margin-right: 10px;"
          @input="handleSearch"
        />
        <el-button type="primary" @click="goToCreate">
          <el-icon><plus /></el-icon>
          创建 PPT
        </el-button>
      </div>
    </div>
    
    <div class="project-grid" v-if="projects.length > 0">
      <el-card
        v-for="project in projects"
        :key="project.id"
        class="project-card"
      >
        <div class="project-preview" @click="goToEdit(project.id)">
          <div class="slides-preview" v-if="project.slideCount > 0">
            <div 
              v-for="i in Math.min(project.slideCount, 4)" 
              :key="i"
              class="slide-thumbnail"
              :style="getSlideStyle(i)"
            >
              <span class="slide-number">{{ i }}</span>
            </div>
          </div>
          <div v-else class="empty-preview">
            <el-icon size="60" color="#909399"><folder /></el-icon>
          </div>
        </div>
        
        <div class="project-info">
          <h3 :title="project.title">{{ project.title }}</h3>
          <div class="project-meta">
            <span class="meta-item">
              <el-icon><document /></el-icon>
              {{ project.slideCount || 0 }} 页
            </span>
            <span class="meta-item">
              <el-icon><clock /></el-icon>
              {{ formatRelativeTime(project.updatedAt) }}
            </span>
          </div>
        </div>
        
        <div class="project-actions">
          <el-button size="small" @click="goToEdit(project.id)">
            <el-icon><edit /></el-icon>
            编辑
          </el-button>
          <el-button size="small" @click="handleDuplicate(project)">
            <el-icon><copy-document /></el-icon>
            复制
          </el-button>
          <el-popconfirm
            title="确定要删除该项目吗？"
            @confirm="handleDelete(project)"
          >
            <template #reference>
              <el-button size="small" type="danger">
                <el-icon><delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-popconfirm>
        </div>
      </el-card>
    </div>
    
    <el-empty v-else description="暂无项目，快去创建第一个 PPT 吧">
      <el-button type="primary" @click="goToCreate">创建 PPT</el-button>
    </el-empty>
    
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next"
        @current-change="loadProjects"
        @size-change="loadProjects"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  getProjectsApi, 
  deleteProjectApi, 
  duplicateProjectApi 
} from '@/api/project'
import type { PPTProject } from '@/types/api'
import { 
  Plus, 
  Folder, 
  Search, 
  Document, 
  Clock, 
  Edit, 
  CopyDocument, 
  Delete 
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

const projects = ref<PPTProject[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(12)
const keyword = ref('')

const loadProjects = async () => {
  try {
    const response = await getProjectsApi({
      page: page.value,
      size: size.value,
      keyword: keyword.value || undefined
    })
    
    projects.value = response.records
    total.value = response.total
  } catch (error: any) {
    ElMessage.error(error.message || '加载项目失败')
  }
}

const handleSearch = () => {
  page.value = 1
  loadProjects()
}

const goToCreate = () => {
  router.push('/ppt/create')
}

const goToEdit = (id: number) => {
  router.push(`/projects/${id}/edit`)
}

const handleDuplicate = async (project: PPTProject) => {
  try {
    const duplicated = await duplicateProjectApi(project.id)
    ElMessage.success('项目已复制')
    loadProjects()
  } catch (error: any) {
    ElMessage.error(error.message || '复制失败')
  }
}

const handleDelete = async (project: PPTProject) => {
  try {
    await deleteProjectApi(project.id)
    ElMessage.success('项目已删除')
    loadProjects()
  } catch (error: any) {
    ElMessage.error(error.message || '删除失败')
  }
}

const formatRelativeTime = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 60) {
    return `${minutes} 分钟前`
  } else if (hours < 24) {
    return `${hours} 小时前`
  } else if (days < 7) {
    return `${days} 天前`
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}

const getSlideStyle = (index: number) => {
  const colors = ['#667eea', '#764ba2', '#f093fb', '#f5576c']
  return {
    backgroundColor: colors[(index - 1) % colors.length]
  }
}

onMounted(() => {
  loadProjects()
})
</script>

<style scoped lang="scss">
.project-list-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  
  h2 {
    font-size: 24px;
    color: #333;
  }
  
  .header-actions {
    display: flex;
    align-items: center;
  }
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.project-card {
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  }
  
  .project-preview {
    height: 140px;
    background: #f5f7fa;
    border-radius: 8px;
    margin-bottom: 16px;
    cursor: pointer;
    overflow: hidden;
    
    .slides-preview {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 4px;
      padding: 8px;
      height: 100%;
    }
    
    .slide-thumbnail {
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 4px;
      position: relative;
      
      .slide-number {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.8);
        font-weight: bold;
      }
    }
    
    .empty-preview {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
  
  .project-info {
    margin-bottom: 12px;
    
    h3 {
      font-size: 16px;
      color: #333;
      margin-bottom: 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .project-meta {
      display: flex;
      gap: 15px;
      
      .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 13px;
        color: #909399;
      }
    }
  }
  
  .project-actions {
    display: flex;
    gap: 8px;
    
    .el-button {
      flex: 1;
    }
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
