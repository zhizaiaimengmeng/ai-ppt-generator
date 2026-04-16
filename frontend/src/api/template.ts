import { http } from '@/api/request'
import type { ApiResponse, PageResponse, Template } from '@/types/api'
import type { TemplateLayout } from '@/types/template'

// 获取模板列表
export const getTemplatesApi = (params: {
  page?: number
  size?: number
  category?: string
  keyword?: string
}): Promise<ApiResponse<PageResponse<Template>>> => {
  return http.get('/templates', { params })
}

// 获取模板详情
export const getTemplateDetailApi = (id: number): Promise<ApiResponse<Template>> => {
  return http.get(`/templates/${id}`)
}

// 收藏模板
export const favoriteTemplateApi = (id: number): Promise<ApiResponse<void>> => {
  return http.post(`/templates/${id}/favorite`)
}

// 取消收藏模板
export const unfavoriteTemplateApi = (id: number): Promise<ApiResponse<void>> => {
  return http.delete(`/templates/${id}/favorite`)
}

// 获取模板布局
export const getTemplateLayoutsApi = (style: string): Promise<ApiResponse<TemplateLayout[]>> => {
  return http.get(`/templates/layouts/${style}`)
}

// 获取模板风格列表
export const getTemplateStylesApi = (): Promise<ApiResponse<any[]>> => {
  return http.get('/templates/styles')
}

// 初始化预定义模板
export const initPresetTemplatesApi = (): Promise<ApiResponse<void>> => {
  return http.post('/templates/init-presets')
}
