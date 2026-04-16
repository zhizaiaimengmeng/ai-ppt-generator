<template>
  <el-card class="forgot-password-card">
    <template #header>
      <div class="card-header">
        <h2>忘记密码</h2>
        <p class="subtitle">输入您的邮箱，我们将发送重置密码链接</p>
      </div>
    </template>
    
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      @submit.prevent="handleForgotPassword"
    >
      <el-form-item prop="email">
        <el-input
          v-model="form.email"
          type="email"
          placeholder="请输入邮箱"
          prefix-icon="Message"
          size="large"
        />
      </el-form-item>
      
      <el-form-item>
        <el-button
          type="primary"
          native-type="submit"
          size="large"
          :loading="loading"
          class="w-100"
        >
          发送重置链接
        </el-button>
      </el-form-item>
      
      <div class="text-center mt-20">
        <router-link to="/auth/login" class="link">
          返回登录
        </router-link>
      </div>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { forgotPasswordApi } from '@/api/auth'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  email: ''
})

const rules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const handleForgotPassword = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    
    try {
      await forgotPasswordApi(form.email)
      ElMessage.success('重置链接已发送到您的邮箱，请查收')
    } catch (error) {
      console.error('发送重置链接失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.forgot-password-card {
  border-radius: 16px;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.card-header {
  text-align: center;
  
  h2 {
    margin-bottom: 8px;
    color: #333;
  }
  
  .subtitle {
    color: #909399;
    font-size: 14px;
  }
}

.link {
  color: #409eff;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  
  &:hover {
    text-decoration: underline;
  }
}
</style>
