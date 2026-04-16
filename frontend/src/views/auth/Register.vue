<template>
  <el-card class="register-card">
    <template #header>
      <div class="card-header">
        <h2>注册</h2>
        <p class="subtitle">创建您的账号，开始制作专业 PPT</p>
      </div>
    </template>
    
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      @submit.prevent="handleRegister"
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
      
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="form.confirmPassword"
          type="password"
          placeholder="请确认密码"
          prefix-icon="Lock"
          size="large"
          show-password
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
          注册
        </el-button>
      </el-form-item>
      
      <div class="text-center mt-20">
        <span class="text-gray">已有账号？</span>
        <router-link to="/auth/login" class="link">
          立即登录
        </router-link>
      </div>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { registerApi } from '@/api/auth'
import type { RegisterRequest } from '@/types/api'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

const router = useRouter()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive<RegisterRequest>({
  email: '',
  password: '',
  confirmPassword: ''
})

// 自定义验证密码匹配
const validateConfirmPassword = (rule: any, value: string, callback: Function) => {
  if (value === '') {
    callback(new Error('请确认密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    
    try {
      await registerApi({
        email: form.email,
        password: form.password,
        confirmPassword: form.confirmPassword
      })
      
      ElMessage.success('注册成功！请查收邮箱中的验证链接')
      router.push('/auth/login')
    } catch (error) {
      console.error('注册失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.register-card {
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
