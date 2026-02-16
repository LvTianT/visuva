<template>
  <div id="addPictureBatchPage">
    <h2 style="margin-bottom: 16px">多图上传</h2>
    <!-- 图片信息表单 -->
    <a-form name="formData" layout="vertical" :model="formData" @finish="handleSubmit">
      <a-form-item name="searchText" label="告诉我你想要什么样的图片？">
        <a-input v-model:value="formData.searchText" placeholder="（例如：樱花树下的少女、未来城市夜景、卡通小猫）" allow-clear />
      </a-form-item>
      <a-form-item name="count" label="图片数量">
        <a-input-number
          v-model:value="formData.count"
          placeholder="输入您需要上传的图片数量"
          style="min-width: 180px"
          :min="1"
          :max="30"
          allow-clear
        />
      </a-form-item>
      <a-form-item name="namePrefix" label="为您的图片创建名称">
        <a-input
          v-model:value="formData.namePrefix"
          placeholder="示例：cat_→ cat_01, cat_02, ..."
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">
          开始生成
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  uploadPictureByBatchUsingPost,
} from '@/api/pictureController.ts'
import { useRouter } from 'vue-router'

const formData = reactive<API.PictureUploadByBatchRequest>({
  count: 10,
})
// 提交任务状态
const loading = ref(false)

const router = useRouter()

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async () => {
  loading.value = true
  const res = await uploadPictureByBatchUsingPost({
    ...formData,
  })
  // 操作成功
  if (res.data.code === 0 && res.data.data) {
    message.success(`创建成功，共 ${res.data.data} 条`)
    // 跳转到主页
    router.push({
      path: `/`,
    })
  } else {
    message.error('创建失败，' + res.data.message)
  }
  loading.value = false
}
</script>

<style scoped>
#addPictureBatchPage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
