<template>
  <el-card class="login-card">
    <template #header>
      <div class="card-header">
        <h2>登录</h2>
        <p class="subtitle">欢迎回来，请登录您的账号</p>
      </div>
    </template>
    
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      @submit.prevent="handleLogin"
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
      
      <el-form-item prop="password">
        <el-input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          prefix-icon="Lock"
          size="large"
          show-password
        />
      </el-form-item>
      
      <el-form-item>
        <div class="form-footer">
          <el-checkbox v-model="form.remember">记住我</el-checkbox>
          <router-link to="/auth/forgot-password" class="forgot-link">
            忘记密码？
          </router-link>
        </div>
      </el-form-item>
      
      <el-form-item>
        <el-button
          type="primary"
          native-type="submit"
          size="large"
          :loading="loading"
          class="w-100"
        >
          登录
        </el-button>
      </el-form-item>
      
      <div class="text-center mt-20">
        <span class="text-gray">还没有账号？</span>
        <router-link to="/auth/register" class="link">
          立即注册
        </router-link>
      </div>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { loginApi } from '@/api/auth'
import type { LoginRequest } from '@/types/api'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive<LoginRequest & { remember: boolean }>({
  email: '',
  password: '',
  remember: false
})

const rules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为 6 位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    
    try {
      const response = await loginApi({
        email: form.email,
        password: form.password
      })
      
      const { token, user } = response
      
      authStore.setToken(token)
      authStore.setUser(user)
      
      ElMessage.success('登录成功')
      
      // 跳转到重定向页面或首页
      const redirect = route.query.redirect as string
      router.push(redirect || '/')
    } catch (error) {
      console.error('登录失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.login-card {
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

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.forgot-link {
  color: #409eff;
  text-decoration: none;
  font-size: 14px;
  
  &:hover {
    text-decoration: underline;
  }
}

.text-gray {
  color: #909399;
  font-size: 14px;
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
