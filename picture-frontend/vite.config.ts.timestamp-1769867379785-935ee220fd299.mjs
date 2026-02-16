// vite.config.ts
import { fileURLToPath, URL } from "node:url";
import { defineConfig } from "file:///E:/picture-master/picture-frontend/node_modules/vite/dist/node/index.js";
import vue from "file:///E:/picture-master/picture-frontend/node_modules/@vitejs/plugin-vue/dist/index.mjs";
import vueDevTools from "file:///E:/picture-master/picture-frontend/node_modules/vite-plugin-vue-devtools/dist/vite.mjs";
import { visualizer } from "file:///E:/picture-master/node_modules/rollup-plugin-visualizer/dist/plugin/index.js";
var __vite_injected_original_import_meta_url = "file:///E:/picture-master/picture-frontend/vite.config.ts";
var vite_config_default = defineConfig({
  // server: {
  //   proxy: {
  //     "/api": 'http:localhost//8143'
  //   }
  // },
  server: {
    proxy: {
      "/cos-image": {
        target: "https://rorojump-1334044609.cos.ap-shanghai.myqcloud.com",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/cos-image/, "")
      }
    }
  },
  plugins: [
    vue(),
    vueDevTools(),
    visualizer({
      open: true,
      // 打包完成后自动打开浏览器查看报告
      gzipSize: true,
      // 查看 gzip 压缩后的体积
      brotliSize: true,
      // 查看 brotli 压缩后的体积
      filename: "stats.html"
      // 生成的分析报告文件名，默认在项目根目录下
    })
  ],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", __vite_injected_original_import_meta_url))
    }
  },
  // 将压缩配置放入 build 字段
  build: {
    minify: "terser",
    cssMinify: true,
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true,
        pure_funcs: ["console.log"],
        passes: 2
      },
      mangle: {
        toplevel: true
      },
      format: {
        comments: false
      }
    },
    chunkSizeWarningLimit: 2e3
    // 单位：KB（注意！Vite 中是 KB，不是字节）
  }
});
export {
  vite_config_default as default
};
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcudHMiXSwKICAic291cmNlc0NvbnRlbnQiOiBbImNvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9kaXJuYW1lID0gXCJFOlxcXFxwaWN0dXJlLW1hc3RlclxcXFxwaWN0dXJlLWZyb250ZW5kXCI7Y29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2ZpbGVuYW1lID0gXCJFOlxcXFxwaWN0dXJlLW1hc3RlclxcXFxwaWN0dXJlLWZyb250ZW5kXFxcXHZpdGUuY29uZmlnLnRzXCI7Y29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2ltcG9ydF9tZXRhX3VybCA9IFwiZmlsZTovLy9FOi9waWN0dXJlLW1hc3Rlci9waWN0dXJlLWZyb250ZW5kL3ZpdGUuY29uZmlnLnRzXCI7aW1wb3J0IHsgZmlsZVVSTFRvUGF0aCwgVVJMIH0gZnJvbSAnbm9kZTp1cmwnXG5pbXBvcnQgeyBkZWZpbmVDb25maWcgfSBmcm9tICd2aXRlJ1xuaW1wb3J0IHZ1ZSBmcm9tICdAdml0ZWpzL3BsdWdpbi12dWUnXG5pbXBvcnQgdnVlRGV2VG9vbHMgZnJvbSAndml0ZS1wbHVnaW4tdnVlLWRldnRvb2xzJ1xuaW1wb3J0IHsgdmlzdWFsaXplciB9IGZyb20gJ3JvbGx1cC1wbHVnaW4tdmlzdWFsaXplcidcbi8vIGltcG9ydCBBdXRvSW1wb3J0IGZyb20gJ3VucGx1Z2luLWF1dG8taW1wb3J0L3ZpdGUnXG4vLyBpbXBvcnQgeyByZXNvbHZlIH0gZnJvbSAncGF0aCdcbi8vIGltcG9ydCBDb21wb25lbnRzIGZyb20gJ3VucGx1Z2luLXZ1ZS1jb21wb25lbnRzL3ZpdGUnXG4vLyBpbXBvcnQgeyBBbnREZXNpZ25WdWVSZXNvbHZlciB9IGZyb20gJ3VucGx1Z2luLXZ1ZS1jb21wb25lbnRzL3Jlc29sdmVycydcblxuLy8gaHR0cHM6Ly92aXRlLmRldi9jb25maWcvXG5leHBvcnQgZGVmYXVsdCBkZWZpbmVDb25maWcoe1xuICAvLyBzZXJ2ZXI6IHtcbiAgLy8gICBwcm94eToge1xuICAvLyAgICAgXCIvYXBpXCI6ICdodHRwOmxvY2FsaG9zdC8vODE0MydcbiAgLy8gICB9XG4gIC8vIH0sXG4gIHNlcnZlcjoge1xuICAgIHByb3h5OiB7XG4gICAgICAnL2Nvcy1pbWFnZSc6IHtcbiAgICAgICAgdGFyZ2V0OiAnaHR0cHM6Ly9yb3JvanVtcC0xMzM0MDQ0NjA5LmNvcy5hcC1zaGFuZ2hhaS5teXFjbG91ZC5jb20nLFxuICAgICAgICBjaGFuZ2VPcmlnaW46IHRydWUsXG4gICAgICAgIHJld3JpdGU6IChwYXRoKSA9PiBwYXRoLnJlcGxhY2UoL15cXC9jb3MtaW1hZ2UvLCAnJyksXG4gICAgICB9LFxuICAgIH0sXG4gIH0sXG4gIHBsdWdpbnM6IFtcbiAgICB2dWUoKSxcbiAgICB2dWVEZXZUb29scygpLFxuICAgIHZpc3VhbGl6ZXIoe1xuICAgICAgb3BlbjogdHJ1ZSwgLy8gXHU2MjUzXHU1MzA1XHU1QjhDXHU2MjEwXHU1NDBFXHU4MUVBXHU1MkE4XHU2MjUzXHU1RjAwXHU2RDRGXHU4OUM4XHU1NjY4XHU2N0U1XHU3NzBCXHU2MkE1XHU1NDRBXG4gICAgICBnemlwU2l6ZTogdHJ1ZSwgLy8gXHU2N0U1XHU3NzBCIGd6aXAgXHU1MzhCXHU3RjI5XHU1NDBFXHU3Njg0XHU0RjUzXHU3OUVGXG4gICAgICBicm90bGlTaXplOiB0cnVlLCAvLyBcdTY3RTVcdTc3MEIgYnJvdGxpIFx1NTM4Qlx1N0YyOVx1NTQwRVx1NzY4NFx1NEY1M1x1NzlFRlxuICAgICAgZmlsZW5hbWU6ICdzdGF0cy5odG1sJywgLy8gXHU3NTFGXHU2MjEwXHU3Njg0XHU1MjA2XHU2NzkwXHU2MkE1XHU1NDRBXHU2NTg3XHU0RUY2XHU1NDBEXHVGRjBDXHU5RUQ4XHU4QkE0XHU1NzI4XHU5ODc5XHU3NkVFXHU2ODM5XHU3NkVFXHU1RjU1XHU0RTBCXG4gICAgfSksXG4gIF0sXG4gIHJlc29sdmU6IHtcbiAgICBhbGlhczoge1xuICAgICAgJ0AnOiBmaWxlVVJMVG9QYXRoKG5ldyBVUkwoJy4vc3JjJywgaW1wb3J0Lm1ldGEudXJsKSksXG4gICAgfSxcbiAgfSxcbiAgLy8gXHU1QzA2XHU1MzhCXHU3RjI5XHU5MTREXHU3RjZFXHU2NTNFXHU1MTY1IGJ1aWxkIFx1NUI1N1x1NkJCNVxuICBidWlsZDoge1xuICAgIG1pbmlmeTogJ3RlcnNlcicsXG4gICAgY3NzTWluaWZ5OiB0cnVlLFxuICAgIHRlcnNlck9wdGlvbnM6IHtcbiAgICAgIGNvbXByZXNzOiB7XG4gICAgICAgIGRyb3BfY29uc29sZTogdHJ1ZSxcbiAgICAgICAgZHJvcF9kZWJ1Z2dlcjogdHJ1ZSxcbiAgICAgICAgcHVyZV9mdW5jczogWydjb25zb2xlLmxvZyddLFxuICAgICAgICBwYXNzZXM6IDIsXG4gICAgICB9LFxuICAgICAgbWFuZ2xlOiB7XG4gICAgICAgIHRvcGxldmVsOiB0cnVlLFxuICAgICAgfSxcbiAgICAgIGZvcm1hdDoge1xuICAgICAgICBjb21tZW50czogZmFsc2UsXG4gICAgICB9LFxuICAgIH0sXG4gICAgY2h1bmtTaXplV2FybmluZ0xpbWl0OiAyMDAwLCAvLyBcdTUzNTVcdTRGNERcdUZGMUFLQlx1RkYwOFx1NkNFOFx1NjEwRlx1RkYwMVZpdGUgXHU0RTJEXHU2NjJGIEtCXHVGRjBDXHU0RTBEXHU2NjJGXHU1QjU3XHU4MjgyXHVGRjA5XG4gIH0sXG59KVxuIl0sCiAgIm1hcHBpbmdzIjogIjtBQUE4UixTQUFTLGVBQWUsV0FBVztBQUNqVSxTQUFTLG9CQUFvQjtBQUM3QixPQUFPLFNBQVM7QUFDaEIsT0FBTyxpQkFBaUI7QUFDeEIsU0FBUyxrQkFBa0I7QUFKc0osSUFBTSwyQ0FBMkM7QUFXbE8sSUFBTyxzQkFBUSxhQUFhO0FBQUE7QUFBQTtBQUFBO0FBQUE7QUFBQTtBQUFBLEVBTTFCLFFBQVE7QUFBQSxJQUNOLE9BQU87QUFBQSxNQUNMLGNBQWM7QUFBQSxRQUNaLFFBQVE7QUFBQSxRQUNSLGNBQWM7QUFBQSxRQUNkLFNBQVMsQ0FBQyxTQUFTLEtBQUssUUFBUSxnQkFBZ0IsRUFBRTtBQUFBLE1BQ3BEO0FBQUEsSUFDRjtBQUFBLEVBQ0Y7QUFBQSxFQUNBLFNBQVM7QUFBQSxJQUNQLElBQUk7QUFBQSxJQUNKLFlBQVk7QUFBQSxJQUNaLFdBQVc7QUFBQSxNQUNULE1BQU07QUFBQTtBQUFBLE1BQ04sVUFBVTtBQUFBO0FBQUEsTUFDVixZQUFZO0FBQUE7QUFBQSxNQUNaLFVBQVU7QUFBQTtBQUFBLElBQ1osQ0FBQztBQUFBLEVBQ0g7QUFBQSxFQUNBLFNBQVM7QUFBQSxJQUNQLE9BQU87QUFBQSxNQUNMLEtBQUssY0FBYyxJQUFJLElBQUksU0FBUyx3Q0FBZSxDQUFDO0FBQUEsSUFDdEQ7QUFBQSxFQUNGO0FBQUE7QUFBQSxFQUVBLE9BQU87QUFBQSxJQUNMLFFBQVE7QUFBQSxJQUNSLFdBQVc7QUFBQSxJQUNYLGVBQWU7QUFBQSxNQUNiLFVBQVU7QUFBQSxRQUNSLGNBQWM7QUFBQSxRQUNkLGVBQWU7QUFBQSxRQUNmLFlBQVksQ0FBQyxhQUFhO0FBQUEsUUFDMUIsUUFBUTtBQUFBLE1BQ1Y7QUFBQSxNQUNBLFFBQVE7QUFBQSxRQUNOLFVBQVU7QUFBQSxNQUNaO0FBQUEsTUFDQSxRQUFRO0FBQUEsUUFDTixVQUFVO0FBQUEsTUFDWjtBQUFBLElBQ0Y7QUFBQSxJQUNBLHVCQUF1QjtBQUFBO0FBQUEsRUFDekI7QUFDRixDQUFDOyIsCiAgIm5hbWVzIjogW10KfQo=
