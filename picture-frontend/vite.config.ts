import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import { visualizer } from 'rollup-plugin-visualizer'
// import AutoImport from 'unplugin-auto-import/vite'
// import { resolve } from 'path'
// import Components from 'unplugin-vue-components/vite'
// import { AntDesignVueResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig({
  // server: {
  //   proxy: {
  //     "/api": 'http:localhost//8143'
  //   }
  // },
  base: '/',
  server: {
    port: 5173, // 设置开发服务器端口
    proxy: {
      '/cos-image': {
        target: 'https://rorojump-1334044609.cos.ap-shanghai.myqcloud.com',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/cos-image/, ''),
      },
    },
  },
  plugins: [
    vue(),
    vueDevTools(),
    visualizer({
      open: true, // 打包完成后自动打开浏览器查看报告
      gzipSize: true, // 查看 gzip 压缩后的体积
      brotliSize: true, // 查看 brotli 压缩后的体积
      filename: 'stats.html', // 生成的分析报告文件名，默认在项目根目录下
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  // 将压缩配置放入 build 字段
  build: {
    minify: 'terser',
    cssMinify: true,
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true,
        pure_funcs: ['console.log'],
        passes: 2,
      },
      mangle: {
        toplevel: true,
      },
      format: {
        comments: false,
      },
    },
    chunkSizeWarningLimit: 2000, // 单位：KB（注意！Vite 中是 KB，不是字节）
  },
})
