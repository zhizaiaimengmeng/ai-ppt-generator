import { http } from '@/api/request'
import type { ApiResponse } from '@/types/api'

// 导出 PPT
export const exportPPTApi = (projectId: number, format: string): Promise<ApiResponse<{
  exportId: number
  status: string
  downloadUrl?: string
}>> => {
  return http.post(`/export/${projectId}`, { format })
}

// 获取导出状态
export const getExportStatusApi = (exportId: number): Promise<ApiResponse<{
  status: string
  progress: number
  downloadUrl?: string
}>> => {
  return http.get(`/export/${exportId}/status`)
}

// 生成分享链接
export const createShareLinkApi = (projectId: number, data: {
  password?: string
  expirationDays?: number
}): Promise<ApiResponse<{
  shareUrl: string
  expirationTime: string
}>> => {
  return http.post(`/export/${projectId}/share-link`, data)
}
