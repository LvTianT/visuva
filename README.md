# visuva.site —— 一个支持个人私有图库、企业团队共享与开放素材平台的轻量级图片协作平台
# 项目网址：https://visuva.site

## 📌 项目简介

`visuva.site` 是一个面向企业级的智能协同图库平台，提供三大核心能力：

- **私有图库**：用户可创建专属空间，安全存储个人照片、学习资料等敏感内容，支持权限控制与空间分析。
- **团队共享图库**：企业管理员可创建协作空间，邀请成员共同管理团建相册、设计素材等集体资源，支持实时协同编辑。
- **公共图库平台**：开放的素材社区，用户可上传/下载表情包、壁纸等内容，由管理员审核保障内容质量。

系统深度融合 AI 能力与工程优化，兼顾安全性、性能与用户体验。

## ✨ 核心功能亮点

- **颜色检索**：基于欧几里得距离算法使用RGB值进行图片主色调检索
- **AI 扩图**：利用生成式 AI 自动优化图片，扩展图片素材
- **实时协作**：多人同时编辑同一图片，操作实时同步
- **性能优化**：
  - 图片查询：分布式缓存、本地缓存、多级缓存
  - 图片上传：WebP 压缩
  - 图片加载：预加载 + 缩略图 + 浏览器缓存
  - 图片清理：立即清理 + 手动清理 + 定期清理
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
- **AI 集成**：集成阿里云百炼的AI绘图大模型

### 前端
- **框架**：Vue 3 + Vite
- **状态管理**：Pinia
- **UI 组件库**：Ant Design Vue
- **网络请求**：Axios
- **协同编辑**：WebSocket

## 🚀 快速启动（开发环境）

> 前置要求：JDK 17+、Node.js 18+、MySQL、Redis

```bash
# 1. 克隆项目
git clone https://github.com/LvTianT/visuva.git
cd picture-master

# 2. 启动后端
cd picture-master/picture-master

# 3. 启动前端
cd picture-master/picture-frontend
npm install
npm run dev
