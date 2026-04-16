import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/projects'
  },
  {
    path: '/auth',
    component: () => import('@/layouts/AuthLayout.vue'),
    children: [
      {
        path: 'login',
        name: 'Login',
        component: () => import('@/views/auth/Login.vue')
      },
      {
        path: 'register',
        name: 'Register',
        component: () => import('@/views/auth/Register.vue')
      },
      {
        path: 'forgot-password',
        name: 'ForgotPassword',
        component: () => import('@/views/auth/ForgotPassword.vue')
      },
      {
        path: 'reset-password',
        name: 'ResetPassword',
        component: () => import('@/views/auth/ResetPassword.vue')
      },
      {
        path: 'verify-email',
        name: 'VerifyEmail',
        component: () => import('@/views/auth/VerifyEmail.vue')
      }
    ]
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'projects',
        name: 'ProjectList',
        component: () => import('@/views/projects/ProjectList.vue')
      },
      {
        path: 'projects/create',
        name: 'CreatePPT',
        component: () => import('@/views/ppt/CreatePPT.vue')
      },
      {
        path: 'projects/:id/edit',
        name: 'ProjectEditor',
        component: () => import('@/views/projects/ProjectEditor.vue')
      },
      {
        path: 'templates',
        name: 'TemplateList',
        component: () => import('@/views/templates/TemplateList.vue')
      }
    ]
  },
  {
    path: '/share/:shareId',
    name: 'ShareView',
    component: () => import('@/views/share/ShareView.vue')
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 需要登录的路由
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next({
      path: '/auth/login',
      query: { redirect: to.fullPath }
    })
  } 
  // 已登录用户访问认证页面，重定向到首页
  else if (to.path.startsWith('/auth') && authStore.isLoggedIn) {
    next({ path: '/' })
  }
  // 其他情况正常访问
  else {
    next()
  }
})

export default router
