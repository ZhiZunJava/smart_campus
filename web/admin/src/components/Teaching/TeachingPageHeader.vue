<template>
  <section class="teaching-page-header">
    <div class="teaching-page-header__main">
      <div class="teaching-page-header__icon">
        <i :class="icon"></i>
      </div>
      <div class="teaching-page-header__copy">
        <div class="teaching-page-header__eyebrow">教学基础配置中心</div>
        <h2>{{ title }}</h2>
        <p>{{ subtitle }}</p>
      </div>
    </div>
    <div class="teaching-page-header__stats">
      <div v-for="item in stats" :key="item.label" class="teaching-stat">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.tip }}</small>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  subtitle: string
  icon?: string
  stats: Array<{ label: string; value: string | number; tip: string }>
}>()
</script>

<style scoped>
.teaching-page-header {
  display: grid;
  grid-template-columns: minmax(0, 1.3fr) minmax(340px, 1fr);
  gap: 16px;
  margin-bottom: 16px;
  padding: 18px;
  border: 1px solid #dbe3f0;
  border-radius: 18px;
  background:
    radial-gradient(circle at top right, rgba(76, 120, 255, 0.14), transparent 28%),
    linear-gradient(180deg, #ffffff 0%, #f7fbff 100%);
}

.teaching-page-header__main {
  display: flex;
  gap: 14px;
  align-items: flex-start;
}

.teaching-page-header__icon {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e9f1ff 0%, #dce9ff 100%);
  color: #2656c8;
  font-size: 24px;
  flex-shrink: 0;
}

.teaching-page-header__eyebrow {
  color: #5b6b86;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.teaching-page-header__copy h2 {
  margin: 6px 0 10px;
  color: #172033;
  font-size: 24px;
  line-height: 1.2;
}

.teaching-page-header__copy p {
  margin: 0;
  color: #526076;
  font-size: 13px;
  line-height: 1.8;
}

.teaching-page-header__stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.teaching-stat {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 14px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid rgba(219, 227, 240, 0.9);
}

.teaching-stat span,
.teaching-stat small {
  color: #667085;
  font-size: 12px;
}

.teaching-stat strong {
  color: #172033;
  font-size: 22px;
  line-height: 1.1;
}

@media (max-width: 1100px) {
  .teaching-page-header {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .teaching-page-header__stats {
    grid-template-columns: 1fr;
  }
}
</style>
