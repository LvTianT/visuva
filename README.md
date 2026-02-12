# visuva.site —— 一个支持个人私有图库、企业团队共享与开放素材平台的高性能轻量级图片协作平台


## 📌 项目简介

`visuva.site` 是一个面向多角色（普通用户、企业成员、管理员）的智能图库平台，提供三大核心能力：

- **私有图库**：用户可创建专属空间，安全存储个人照片、学习资料等敏感内容，支持权限控制与空间分析。
- **团队共享图库**：企业管理员可创建协作空间，邀请成员共同管理团建相册、设计素材等集体资源，支持实时协同编辑。
- **公共图库平台**：开放的素材社区，用户可上传/下载表情包、壁纸等内容，由管理员审核保障内容质量。

系统深度融合 AI 能力与工程优化，兼顾安全性、性能与用户体验。

## ✨ 核心功能亮点

- **智能搜索**：支持基于颜色、标签、以图搜图等多维检索
- **AI 扩图**：利用生成式 AI 自动扩展图片边界，提升创作效率
- **实时协作**：多人同时编辑同一图片，操作实时同步
- **全链路性能优化**：
  - 图片上传：分片 + 断点续传 + WebP 压缩 + 秒传
  - 图片加载：懒加载 + 缩略图 + CDN 加速
  - 数据查询：多级缓存（Caffeine + Redis）+ 分库分表
- **安全架构**：基于空间（Space）的多租户隔离，防止越权访问

## 🛠 技术栈

### 后端
- **框架**：Spring Boot + MyBatis-Plus
- **数据库**：MySQL（ShardingSphere 分库分表）
- **缓存**：Redis（分布式） + Caffeine（本地）
- **存储**：腾讯云 COS 对象存储
- **权限**：Sa-Token
- **实时通信**：WebSocket
- **异步处理**：Disruptor 高性能队列
- **AI 集成**：Stable Diffusion 微调模型（通过 HTTP API 调用）

### 前端
- **框架**：Vue 3 + Vite
- **状态管理**：Pinia
- **UI 组件库**：Ant Design Vue
- **网络请求**：Axios
- **协同编辑**：Yjs（可选集成）

## 🚀 快速启动（开发环境）

> 前置要求：JDK 17+、Node.js 18+、MySQL、Redis

```bash
# 1. 克隆项目
git clone https://github.com/LvTianT/visuva.git
cd visuva

# 2. 启动后端
cd backend
./mvnw spring-boot:run

# 3. 启动前端
cd ../frontend
npm install
npm run dev
