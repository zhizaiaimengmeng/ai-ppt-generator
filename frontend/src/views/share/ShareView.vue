<template>
  <div class="share-view-page">
    <div class="share-header">
      <h1>{{ project?.title || '加载中...' }}</h1>
      <el-tag v-if="isExpired" type="info" size="small">链接已过期</el-tag>
    </div>
    
    <div class="share-content" v-if="!isExpired && project">
      <!-- 幻灯片展示 -->
      <div class="slide-show">
        <div class="current-slide" v-if="slides.length > 0">
          <div class="slide-content">
            <h2>{{ slides[currentSlideIndex]?.title }}</h2>
            <div class="slide-body">
              <div v-if="slides[currentSlideIndex]?.content">
                <ul v-if="Array.isArray(slides[currentSlideIndex]?.content?.points)">
                  <li v-for="(point, idx) in slides[currentSlideIndex]?.content?.points" :key="idx">
                    {{ point }}
                  </li>
                </ul>
                <pre v-else>{{ JSON.stringify(slides[currentSlideIndex]?.content, null, 2) }}</pre>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 控制栏 -->
      <div class="controls">
        <el-button @click="prevSlide" :disabled="currentSlideIndex <= 0">
          <el-icon><arrow-left /></el-icon>
          上一页
        </el-button>
        
        <span class="slide-number">
          {{ currentSlideIndex + 1 }} / {{ slides.length }}
        </span>
        
        <el-button @click="nextSlide" :disabled="currentSlideIndex >= slides.length - 1">
          下一页
          <el-icon><arrow-right /></el-icon>
        </el-button>
      </div>
      
      <!-- 缩略图导航 -->
      <div class="thumbnails-nav">
        <el-scrollbar>
          <div class="thumbnails-list">
            <div
              v-for="(slide, index) in slides"
              :key="index"
              :class="['thumbnail', { active: index === currentSlideIndex }]"
              @click="currentSlideIndex = index"
            >
              <div class="thumbnail-number">{{ index + 1 }}</div>
              <div class="thumbnail-title">{{ slide.title || '无标题' }}</div>
            </div>
          </div>
        </el-scrollbar>
      </div>
    </div>
    
    <div class="share-content" v-else-if="isExpired">
      <el-result icon="error" title="链接已过期" sub-title="该分享链接已失效，请联系分享者重新生成">
        <template #extra>
          <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { Slide } from '@/types/api'

const route = useRoute()
const shareId = route.params.shareId as string

const project = ref<any>(null)
const slides = ref<Slide[]>([])
const currentSlideIndex = ref(0)
const isExpired = ref(false)

// 加载分享数据
const loadShareData = async () => {
  try {
    // TODO: 调用获取分享数据的 API
    console.log('加载分享数据:', shareId)
    
    // 模拟数据
    project.value = {
      title: '示例 PPT',
      createdAt: new Date()
    }
    
    slides.value = [
      {
        id: 1,
        projectId: 1,
        slideNumber: 1,
        layoutType: 'title',
        title: '人工智能技术应用',
        content: { subtitle: '探索 AI 的未来' },
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      },
      {
        id: 2,
        projectId: 1,
        slideNumber: 2,
        layoutType: 'content',
        title: '目录',
        content: { points: ['背景介绍', '技术架构', '应用场景', '未来展望'] },
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      },
      {
        id: 3,
        projectId: 1,
        slideNumber: 3,
        layoutType: 'content',
        title: '背景介绍',
        content: { points: ['AI 发展历史', '当前技术现状', '市场需求分析'] },
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
    ]
    
    // 检查是否过期
    // const response = await getShareDataApi(shareId)
    // if (response.data.expired) {
    //   isExpired.value = true
    // }
    
  } catch (error) {
    console.error('加载分享数据失败:', error)
    ElMessage.error('加载失败')
    isExpired.value = true
  }
}

const prevSlide = () => {
  if (currentSlideIndex.value > 0) {
    currentSlideIndex.value--
  }
}

const nextSlide = () => {
  if (currentSlideIndex.value < slides.value.length - 1) {
    currentSlideIndex.value++
  }
}

onMounted(() => {
  loadShareData()
  
  // 监听键盘事件
  const handleKeyDown = (e: KeyboardEvent) => {
    if (e.key === 'ArrowLeft') {
      prevSlide()
    } else if (e.key === 'ArrowRight') {
      nextSlide()
    }
  }
  
  window.addEventListener('keydown', handleKeyDown)
})
</script>

<style lang="scss" scoped>
.share-view-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.share-header {
  text-align: center;
  margin-bottom: 30px;
  
  h1 {
    font-size: 28px;
    color: #333;
    margin-bottom: 10px;
  }
}

.share-content {
  max-width: 1000px;
  margin: 0 auto;
}

.slide-show {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  margin-bottom: 20px;
  min-height: 500px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  
  .slide-content {
    h2 {
      font-size: 32px;
      color: #333;
      margin-bottom: 40px;
      text-align: center;
    }
    
    .slide-body {
      font-size: 18px;
      line-height: 2;
      color: #606266;
      
      ul {
        margin: 20px 0;
        padding-left: 40px;
        
        li {
          margin: 15px 0;
        }
      }
      
      pre {
        background: #f5f7fa;
        padding: 20px;
        border-radius: 8px;
        overflow-x: auto;
      }
    }
  }
}

.controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  
  .slide-number {
    font-size: 16px;
    color: #606266;
    min-width: 100px;
    text-align: center;
  }
}

.thumbnails-nav {
  background: #fff;
  border-radius: 8px;
  padding: 15px;
  
  .thumbnails-list {
    display: flex;
    gap: 10px;
    
    .thumbnail {
      flex-shrink: 0;
      width: 150px;
      padding: 10px;
      border: 2px solid #e6e6e6;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        border-color: #409eff;
      }
      
      &.active {
        border-color: #409eff;
        background: #ecf5ff;
      }
      
      .thumbnail-number {
        font-size: 12px;
        color: #909399;
        margin-bottom: 5px;
      }
      
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
</style>
