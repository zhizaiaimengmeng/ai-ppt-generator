import { http } from '@/api/request'
import type {
  ApiResponse,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  User
} from '@/types/api'

// 用户登录
export const loginApi = (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
  return http.post<LoginResponse>('/auth/login', data)
}

// 用户注册
export const registerApi = (data: RegisterRequest): Promise<ApiResponse<{ userId: number }>> => {
  return http.post<{ userId: number }>('/auth/register', data)
}

// 发送邮箱验证码
export const sendVerificationCodeApi = (email: string): Promise<ApiResponse<void>> => {
  return http.post('/auth/send-verification-code', { email })
}

// 验证邮箱
export const verifyEmailApi = (token: string): Promise<ApiResponse<void>> => {
  return http.get(`/auth/verify-email?token=${token}`)
}

// 忘记密码
export const forgotPasswordApi = (email: string): Promise<ApiResponse<void>> => {
  return http.post('/auth/forgot-password', { email })
}

// 重置密码
export const resetPasswordApi = (token: string, newPassword: string): Promise<ApiResponse<void>> => {
  return http.post('/auth/reset-password', { token, newPassword })
}

// 获取当前用户信息
export const getCurrentUserApi = (): Promise<ApiResponse<User>> => {
  return http.get<User>('/auth/me')
}

// 登出
export const logoutApi = (): Promise<ApiResponse<void>> => {
  return http.post('/auth/logout')
}
