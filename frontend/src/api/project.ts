import { http } from '@/api/request'
import type { ApiResponse, PageResponse, PPTProject, Slide } from '@/types/api'

// 获取项目列表
export const getProjectsApi = (params: {
  page?: number
  size?: number
  keyword?: string
}): Promise<ApiResponse<PageResponse<PPTProject>>> => {
  return http.get('/projects', { params })
}

// 获取项目详情
export const getProjectDetailApi = (id: number): Promise<ApiResponse<PPTProject & { slides: Slide[] }>> => {
  return http.get(`/projects/${id}`)
}

// 创建项目
export const createProjectApi = (data: { title: string }): Promise<ApiResponse<PPTProject>> => {
  return http.post('/projects', data)
}

// 更新项目
export const updateProjectApi = (id: number, data: { title?: string }): Promise<ApiResponse<void>> => {
  return http.put(`/projects/${id}`, data)
}

// 删除项目
export const deleteProjectApi = (id: number): Promise<ApiResponse<void>> => {
  return http.delete(`/projects/${id}`)
}

// 复制项目
export const duplicateProjectApi = (id: number): Promise<ApiResponse<PPTProject>> => {
  return http.post(`/projects/${id}/duplicate`)
}

// 更新幻灯片
export const updateSlidesApi = (id: number, slides: Slide[]): Promise<ApiResponse<void>> => {
  return http.put(`/projects/${id}/slides`, slides)
}

// 获取生成进度
export const getGenerationStatusApi = (projectId: number): Promise<ApiResponse<{
  status: string
  progress: number
  message: string
}>> => {
  return http.get(`/ppt/generation/${projectId}/status`)
}
