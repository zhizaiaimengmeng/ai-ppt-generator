<template>
  <div class="create-ppt">
    <h2>创建新的 PPT</h2>
    
    <el-steps :active="currentStep" finish-status="success" align-center class="steps">
      <el-step title="选择生成方式" />
      <el-step title="输入内容" />
      <el-step title="选择模板" />
      <el-step title="生成 PPT" />
    </el-steps>
    
    <div class="step-content">
      <!-- 步骤 1: 选择生成方式 -->
      <div v-show="currentStep === 0" class="step">
        <h3>选择生成方式</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card
              :class="['mode-card', { active: form.mode === 'topic' }]"
              @click="form.mode = 'topic'"
            >
              <el-icon size="48" color="#409eff"><chat-line-round /></el-icon>
              <h4>主题驱动</h4>
              <p>输入主题，AI 自动生成完整 PPT 内容</p>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card
              :class="['mode-card', { active: form.mode === 'web' }]"
              @click="form.mode = 'web'"
            >
              <el-icon size="48" color="#67c23a"><search /></el-icon>
              <h4>网络内容聚合</h4>
              <p>AI 根据关键词检索网络内容生成 PPT</p>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <!-- 步骤 2: 输入内容 -->
      <div v-show="currentStep === 1" class="step">
        <h3>{{ form.mode === 'topic' ? '输入主题' : '输入关键词' }}</h3>
        <el-input
          v-model="form.topic"
          type="textarea"
          :rows="5"
          placeholder="请详细描述您想要生成的 PPT 主题或关键词..."
        />
      </div>
      
      <!-- 步骤 3: 选择模板 -->
      <div v-show="currentStep === 2" class="step">
        <h3>选择模板（开发中）</h3>
        <el-empty description="模板库功能开发中" />
      </div>
      
      <!-- 步骤 4: 生成 PPT -->
      <div v-show="currentStep === 3" class="step">
        <h3>开始生成</h3>
        <div class="generate-info">
          <p><strong>生成方式：</strong>{{ form.mode === 'topic' ? '主题驱动' : '网络内容聚合' }}</p>
          <p><strong>主题/关键词：</strong>{{ form.topic }}</p>
        </div>
      </div>
    </div>
    
    <div class="step-actions">
      <el-button @click="prevStep" :disabled="currentStep === 0">上一步</el-button>
      <el-button
        v-if="currentStep < 3"
        type="primary"
        @click="nextStep"
        :disabled="!canNext"
      >
        下一步
      </el-button>
      <el-button
        v-else
        type="primary"
        @click="handleGenerate"
        :loading="generating"
      >
        开始生成
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ChatLineRound, Search } from '@element-plus/icons-vue'

const router = useRouter()

const currentStep = ref(0)
const generating = ref(false)

const form = ref({
  mode: 'topic' as 'topic' | 'web',
  topic: '',
  templateId: 1
})

const canNext = computed(() => {
  if (currentStep.value === 0) return true
  if (currentStep.value === 1) return form.value.topic.trim() !== ''
  if (currentStep.value === 2) return true
  return true
})

const nextStep = () => {
  if (currentStep.value < 3) {
    currentStep.value++
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const handleGenerate = async () => {
  generating.value = true
  // TODO: 调用生成 API
  console.log('生成 PPT:', form.value)
  setTimeout(() => {
    generating.value = false
    router.push('/projects')
  }, 2000)
}
</script>

<style lang="scss" scoped>
.create-ppt {
  max-width: 800px;
  margin: 0 auto;
  
  h2 {
    margin-bottom: 30px;
    color: #333;
  }
  
  .steps {
    margin-bottom: 40px;
  }
  
  .step-content {
    min-height: 300px;
    margin-bottom: 40px;
    
    .step {
      h3 {
        margin-bottom: 20px;
        color: #333;
      }
    }
  }
  
  .mode-card {
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;
    border: 2px solid transparent;
    
    &:hover {
      border-color: #409eff;
      transform: translateY(-4px);
    }
    
    &.active {
      border-color: #409eff;
      background-color: #ecf5ff;
    }
    
    h4 {
      margin: 12px 0 8px;
      color: #333;
    }
    
    p {
      color: #909399;
      font-size: 14px;
    }
  }
  
  .generate-info {
    background: #f5f7fa;
    padding: 20px;
    border-radius: 8px;
    
    p {
      margin-bottom: 12px;
      color: #606266;
    }
  }
  
  .step-actions {
    display: flex;
    justify-content: center;
    gap: 20px;
  }
}
</style>
