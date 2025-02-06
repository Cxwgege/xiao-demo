<template>
  <el-button
      v-bind="$attrs"
      :class="[
        type && `x-${type}-button`,
        'x-button'
      ]"
  >
    <slot name="icon"></slot>
    <slot></slot>
  </el-button>
</template>

<script setup>
defineProps({
  type: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'success', 'info', 'warning', 'danger'].includes(value)
  }
})
</script>

<style lang="scss" scoped>
.x-button {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 15px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.x-primary-button {
  background: linear-gradient(135deg, #2193b0 0%, #6dd5ed 100%);
  border: none;
  color: white;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(33, 147, 176, 0.3);
    background: linear-gradient(135deg, #6dd5ed 0%, #2193b0 100%);
  }
}

.x-success-button {
  background: linear-gradient(135deg, #56ab2f 0%, #a8e063 100%);
  border: none;
  color: white;
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(86, 171, 47, 0.3);
    background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%);
  }
}

.x-info-button {
  background: linear-gradient(135deg, #8e9eab 0%, #eef2f3 100%);
  border: none;
  color: #2c3e50;
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(142, 158, 171, 0.3);
    background: linear-gradient(135deg, #eef2f3 0%, #8e9eab 100%);
  }
}

.x-warning-button {
  background: linear-gradient(135deg, #f7971e 0%, #ffd200 100%);
  border: none;
  color: white;
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(247, 151, 30, 0.3);
    background: linear-gradient(135deg, #ffd200 0%, #f7971e 100%);
  }
}

.x-danger-button {
  background: linear-gradient(135deg, #e74c3c 0%, #ff6b6b 100%);
  border: none;
  color: white;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
    background: linear-gradient(135deg, #ff6b6b 0%, #e74c3c 100%);
  }
}
</style> 