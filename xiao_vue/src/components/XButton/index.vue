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
  background: linear-gradient(135deg, #457fca 0%, #5691c8 100%);
  border: none;
  color: white;
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(69, 127, 202, 0.3);
    background: linear-gradient(135deg, #5691c8 0%, #457fca 100%);
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
  background: linear-gradient(135deg, #cb2d3e 0%, #ef473a 100%);
  border: none;
  color: white;
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(203, 45, 62, 0.3);
    background: linear-gradient(135deg, #ef473a 0%, #cb2d3e 100%);
  }
}
</style> 