<template>
  <div />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import usePortalUserStore from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = usePortalUserStore()

onMounted(() => {
  const firstSegment = route.path.split('/')[1]
  const role = userStore.availablePortalRoles.includes(firstSegment) ? firstSegment : userStore.preferredPortalRole || 'student'
  router.replace(`/${role}/messages?tab=notice`)
})
</script>
