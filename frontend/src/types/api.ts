// API 响应格式
export interface ApiResponse<T> {
  code: string
  message: string
  data: T
  timestamp: number
}

// 分页请求参数
export interface PageRequest {
  page: number
  size: number
  keyword?: string
}

// 分页响应
export interface PageResponse<T> {
  records: T[]
  total: number
  page: number
  size: number
}

// 用户相关类型
export interface User {
  id: number
  email: string
  nickname?: string
  avatarUrl?: string
  emailVerified: boolean
  createdAt: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  token: string
  refreshToken: string
  user: User
}

export interface RegisterRequest {
  email: string
  password: string
  confirmPassword: string
}

// PPT 项目相关类型
export interface PPTProject {
  id: number
  userId: number
  title: string
  templateId?: number
  generationMode?: string
  status: string
  generationProgress: number
  generationMessage?: string
  createdAt: string
  updatedAt: string
}

export interface GenerationRequest {
  mode: 'topic' | 'web' | 'outline'
  topic?: string
  templateId: number
  options?: {
    slideCount?: number
    includeReferences?: boolean
  }
}

export interface GenerationResponse {
  projectId: number
  status: string
  estimatedTime: number
}

export interface Slide {
  id: number
  projectId: number
  slideNumber: number
  layoutType?: string
  title?: string
  content: any
  backgroundColor?: string
  createdAt: string
  updatedAt: string
}

// 模板相关类型
export interface Template {
  id: number
  name: string
  category?: string
  description?: string
  previewUrl: string
  templateFileUrl?: string
  colorScheme?: any
  fontScheme?: any
  slideLayouts?: any
  isPremium: boolean
  downloadCount: number
  favoriteCount: number
  createdAt: string
  updatedAt: string
}

// 导出相关类型
export interface ExportRequest {
  format: 'pptx' | 'pdf' | 'link' | 'image'
}

export interface ExportResponse {
  exportId: number
  status: string
  downloadUrl?: string
}

export interface ShareLinkRequest {
  password?: string
  expirationDays?: number
}

export interface ShareLinkResponse {
  shareUrl: string
  expirationTime: string
}
