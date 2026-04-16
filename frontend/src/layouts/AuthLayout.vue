<template>
  <div class="auth-layout">
    <div class="auth-container">
      <div class="auth-header">
        <router-link to="/" class="logo">
          <span class="logo-icon">📊</span>
          <span class="logo-text">AI PPT Generator</span>
        </router-link>
      </div>
      <div class="auth-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 认证页布局
</script>

<style lang="scss" scoped>
.auth-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.auth-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.auth-header {
  margin-bottom: 40px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  color: #fff;
  font-size: 24px;
  font-weight: bold;
}

.logo-icon {
  font-size: 32px;
}

.auth-content {
  max-width: 450px;
  margin: 60px auto;
}

@media (max-width: 768px) {
  .auth-content {
    margin: 20px auto;
  }
}
</style>
