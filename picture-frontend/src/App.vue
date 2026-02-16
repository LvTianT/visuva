<template>
  <div id='app'>
    <BasicLayout></BasicLayout>
  </div>
</template>

<script lang='ts'>
import BasicLayout from './layouts/BasicLayout.vue'
import { defineComponent, onMounted } from 'vue'

export default defineComponent({
  components: { BasicLayout },
  // setup() {
  //   const loginUserStore = useLoginUserStore();
  //   onMounted(() => {
  //     console.log('Component mounted, fetching login user...');
  //     loginUserStore.fetchLoginUser();
  //   });
  //   return { loginUserStore };
  // }
});
</script>

<style scoped>

</style>
