# 🖼️ Visuva

> 轻量级图片协作平台 · 私有图库 · 团队共享 · 开放素材

[🌐 在线体验](https://visuva.site) 

---

## 🎯 快速体验

| 项目 | 信息 |
|------|------|
| **网址** | https://visuva.site |
| **账号** | `user` |
| **密码** | `12345678` |

---

## 📌 项目简介

Visuva 是一个面向个人与企业的智能协同图库平台，提供三大核心场景：

| 场景 | 说明 |
|------|------|
| 🔒 **私有图库** | 个人专属空间，安全存储私密内容，支持权限控制 |
| 👥 **团队共享** | 企业协作空间，多人实时编辑，支持成员管理 |
| 🌍 **公共素材** | 开放素材社区，上传/下载表情包、壁纸等公共资源 |

---

## ✨ 核心功能

| 功能           | 说明 |
|--------------|------|
| 🎨 **颜色检索**  | 基于 RGB 值的图片主色调智能搜索 |
| 🤖 **AI 扩图** | 集成阿里云百炼大模型，自动优化扩展图片 |
| ⚡  **实时协作**  | WebSocket 多人同时编辑，操作实时同步 |
| 🚀 **性能优化**  | 多级缓存 + WebP 压缩 + 预加载 + 缩略图 |
| 🛡️ **安全隔离** | 基于 Space 的多租户架构，防止越权访问 |

---

## 🛠 技术栈

<table>
<tr>
<th>后端</th>
<th>前端</th>
<th>基础设施</th>
</tr>
<tr>
<td>

- Spring Boot
- MyBatis-Plus
- Sa-Token
- WebSocket
- Disruptor

</td>
<td>

- Vue 3 + Vite
- Pinia
- Ant Design Vue
- Axios

</td>
<td>

- MySQL + ShardingSphere
- Redis + Caffeine
- 腾讯云 COS
- 阿里云百炼 AI

</td>
</tr>
</table>

---

## 🚀 快速启动

> **前置要求**：JDK 17+ · Node.js 18+ · MySQL · Redis

```bash
# 1. 克隆项目
git clone https://github.com/LvTianT/visuva.git

# 2. 启动后端
cd picture-master
# 配置数据库后运行主类

# 3. 启动前端
cd picture-frontend
npm install
npm run dev