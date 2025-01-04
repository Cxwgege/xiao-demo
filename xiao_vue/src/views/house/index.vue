<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="home-card">
          <template #header>
            <div class="card-header">
              <span>用户house总数</span>
              <el-icon><User /></el-icon>
            </div>
          </template>
          <div class="card-number">8848</div>
        </el-card>
      </el-col>

      <!-- 其他统计卡片... -->
    </el-row>
  </div>
</template>

<script setup>
import { User } from '@element-plus/icons-vue'
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.home-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .card-number {
    font-size: 28px;
    font-weight: bold;
    color: #409EFF;
    text-align: center;
    padding: 20px 0;
  }
}
</style>