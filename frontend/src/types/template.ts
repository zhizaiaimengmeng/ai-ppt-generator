// 模板布局定义
export interface TemplateLayout {
  layoutType: string
  name: string
  backgroundColor?: string
  backgroundImage?: string
  gradient?: GradientConfig
  elements: LayoutElement[]
  placeholders?: Record<string, any>
}

// 渐变配置
export interface GradientConfig {
  type: string
  direction: string
  colors: string[]
  stops?: string[]
}

// 布局元素
export interface LayoutElement {
  type: string
  position: string
  fontSize?: number
  color?: string
  fontWeight?: string
  width?: string
  height?: string
  textAlign?: string
  className?: string
  styles?: Record<string, any>
  placeholderKey?: string
  required?: boolean
}
