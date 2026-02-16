import axios from "axios";
import { message } from "ant-design-vue";

// 自动判断环境
const isProduction = process.env.NODE_ENV === 'production';

// 开发环境：本地后端（HTTP）
const DEV_BASE_URL = "http://localhost:8143";
// 生产环境：相对路径（关键！让请求走 Nginx 代理，自动继承页面的 HTTPS 协议）
// ❌ 原错误：直接访问后端 HTTP 端口；✅ 新写法：相对路径，由 Nginx 代理
const PROD_BASE_URL = "/";

const myAxios = axios.create({
  // 生产环境：baseURL 为 /，请求会拼接成 https://visuva.site/api/xxx
  // 开发环境：baseURL 为 http://localhost:8143，本地调试正常
  baseURL: isProduction ? PROD_BASE_URL : DEV_BASE_URL,
  timeout: 10000,
  withCredentials: true,
});

// 请求拦截器（保持不变）
myAxios.interceptors.request.use(
  function (config) {
    // 可选优化：统一给所有请求加 /api 前缀（避免前端每个请求手动写）
    if (isProduction && !config.url.startsWith('/api')) {
      config.url = `/api${config.url}`;
    }
    return config;
  },
  function (error) {
    return Promise.reject(error);
  }
);

// 响应拦截器（保持不变）
myAxios.interceptors.response.use(
  function (response) {
    const { data } = response;
    if (data.code === 40100) {
      if (
        !response.request.responseURL.includes('user/get/login') &&
        !window.location.pathname.includes('/user/login')
      ) {
        message.warning('请先登录');
        window.location.href = `/user/login?redirect=${window.location.href}`;
      }
    }
    return response;
  },
  function (error) {
    return Promise.reject(error);
  }
);

export default myAxios;
