<template>
  <div class="verify-email-page">
    <el-card class="verify-card">
      <div class="verify-content">
        <el-icon v-if="loading" size="60" class="is-loading"><loading /></el-icon>
        <el-icon v-else-if="success" size="60" color="#67c23a"><circle-check /></el-icon>
        <el-icon v-else size="60" color="#f56c6c"><circle-close /></el-icon>
        
        <h2 v-if="loading">验证中...</h2>
        <h2 v-else-if="success">邮箱验证成功</h2>
        <h2 v-else>验证失败</h2>
        
        <p v-if="loading">请稍候，正在验证您的邮箱...</p>
        <p v-else-if="success">您的邮箱已成功验证，可以开始使用 AI PPT Generator 了！</p>
        <p v-else>{{ errorMessage }}</p>
        
        <el-button
          v-if="success"
          type="primary"
          @click="goToLogin"
          class="mt-20"
        >
          前往登录
        </el-button>
        
        <el-button
          v-if="!success && !loading"
          type="primary"
          @click="goToRegister"
          class="mt-20"
        >
          重新注册
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { verifyEmailApi } from '@/api/auth'
import { CircleCheck, CircleClose, Loading } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const success = ref(false)
const errorMessage = ref('')

onMounted(async () => {
  const token = route.query.token as string
  
  if (!token) {
    loading.value = false
    success.value = false
    errorMessage.value = '验证令牌缺失'
    return
  }
  
  try {
    await verifyEmailApi(token)
    success.value = true
  } catch (error) {
    success.value = false
    errorMessage.value = '验证失败，令牌可能已过期或无效'
  } finally {
    loading.value = false
  }
})

const goToLogin = () => {
  router.push('/auth/login')
}

const goToRegister = () => {
  router.push('/auth/register')
}
</script>

<style lang="scss" scoped>
.verify-email-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.verify-card {
  max-width: 500px;
  border-radius: 16px;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.2);
}

.verify-content {
  text-align: center;
  padding: 20px;
  
  h2 {
    margin: 20px 0 10px;
    color: #333;
  }
  
  p {
    color: #666;
    line-height: 1.6;
  }
}
</style>
