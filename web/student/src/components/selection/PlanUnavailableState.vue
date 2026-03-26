<template>
  <div class="plan-unavailable-state">
    <div class="plan-unavailable-state__media" aria-hidden="true">
      <svg viewBox="0 0 320 220" class="plan-unavailable-state__illustration">
        <defs>
          <linearGradient id="selection-card-bg" x1="0%" x2="100%" y1="0%" y2="100%">
            <stop offset="0%" stop-color="#ffffff" />
            <stop offset="100%" stop-color="#eef5ff" />
          </linearGradient>
          <linearGradient id="selection-accent" x1="0%" x2="100%" y1="0%" y2="100%">
            <stop offset="0%" stop-color="#7aa8ff" />
            <stop offset="100%" stop-color="#376cff" />
          </linearGradient>
          <linearGradient id="selection-warm" x1="0%" x2="100%" y1="0%" y2="100%">
            <stop offset="0%" stop-color="#ffd9a8" />
            <stop offset="100%" stop-color="#ff9f5a" />
          </linearGradient>
        </defs>
        <ellipse cx="160" cy="186" rx="92" ry="14" fill="rgba(59, 130, 246, 0.08)" />
        <path d="M84 58c12-18 32-28 57-28 27 0 49 14 61 36 3 6 5 13 6 19 22 1 40 19 40 41 0 24-20 43-44 43H93c-24 0-43-19-43-43 0-21 15-38 35-42 1-9 4-18 9-26Z" fill="#f3f7ff" />
        <rect x="84" y="70" width="152" height="96" rx="20" fill="url(#selection-card-bg)" stroke="#d9e6ff" />
        <rect x="102" y="92" width="114" height="12" rx="6" fill="#dce8ff" />
        <rect x="102" y="114" width="86" height="10" rx="5" fill="#e7efff" />
        <rect x="102" y="132" width="64" height="10" rx="5" fill="#e7efff" />
        <rect x="198" y="112" width="20" height="20" rx="6" fill="url(#selection-warm)" />
        <circle cx="244" cy="88" r="20" fill="#fff4e8" stroke="#ffd5ad" />
        <path d="M244 79c-5 0-9 4-9 9v4h18v-4c0-5-4-9-9-9Zm-12 13h24v16a8 8 0 0 1-8 8h-8a8 8 0 0 1-8-8V92Z" fill="url(#selection-accent)" />
        <path d="M110 52l8-10m102 10 8-10m-140 18 11 5m136-5 11 5" stroke="#9ebeff" stroke-linecap="round" stroke-width="4" />
      </svg>
    </div>

    <div class="plan-unavailable-state__body">
      <span class="plan-unavailable-state__badge">{{ badgeText }}</span>
      <h4>{{ title }}</h4>
      <p>{{ description }}</p>
      <div v-if="tips.length" class="plan-unavailable-state__tips">
        <div v-for="item in tips" :key="item.label" class="plan-unavailable-state__tip">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
        </div>
      </div>
      <div class="plan-unavailable-state__actions">
        <slot name="actions" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  title: string
  description: string
  badgeText?: string
  tips?: Array<{ label: string; value: string }>
}>(), {
  badgeText: '未开启',
  tips: () => [],
})
</script>

<style scoped>
.plan-unavailable-state {
  display: grid;
  grid-template-columns: minmax(220px, 280px) minmax(0, 1fr);
  align-items: center;
  gap: 28px;
  padding: 32px 24px 28px;
}

.plan-unavailable-state__media {
  display: flex;
  justify-content: center;
}

.plan-unavailable-state__illustration {
  width: min(280px, 100%);
  height: auto;
  filter: drop-shadow(0 18px 28px rgba(59, 130, 246, 0.12));
}

.plan-unavailable-state__body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.plan-unavailable-state__badge {
  display: inline-flex;
  align-items: center;
  align-self: flex-start;
  height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  background: #edf4ff;
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
}

.plan-unavailable-state__body h4 {
  margin: 0;
  color: #0f172a;
  font-size: 28px;
  line-height: 1.3;
}

.plan-unavailable-state__body p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
  line-height: 1.8;
}

.plan-unavailable-state__tips {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.plan-unavailable-state__tip {
  min-width: 180px;
  padding: 12px 14px;
  border-radius: 14px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.plan-unavailable-state__tip span {
  display: block;
  color: #64748b;
  font-size: 12px;
}

.plan-unavailable-state__tip strong {
  display: block;
  margin-top: 6px;
  color: #0f172a;
  font-size: 14px;
  line-height: 1.6;
}

.plan-unavailable-state__actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 4px;
}

@media (max-width: 860px) {
  .plan-unavailable-state {
    grid-template-columns: 1fr;
    justify-items: center;
    text-align: center;
    gap: 18px;
  }

  .plan-unavailable-state__body {
    align-items: center;
  }

  .plan-unavailable-state__badge {
    align-self: center;
  }

  .plan-unavailable-state__tips,
  .plan-unavailable-state__actions {
    justify-content: center;
  }

  .plan-unavailable-state__tip {
    text-align: left;
  }
}
</style>
