乐学课堂在线教育平台
本项目是一个面向大众用户职业技能学习的在线教育平台，提供了职业技能培训相关课程。项目基于B2B2C的业务模式，主要包括认证授权、内容管理、媒体资源管理、课程搜索、订单支付管理、选课管理等六大模块。
采用SpringCloud微服务架构，使用Nacos实现服务的注册和发现，统一配置文件管理，Gateway网关实现动态路由以及负载均衡，使用Redis和Elasticsearch提高数据读取和检索效率，使用MinIO进行分布式文件存储，同时使用XXL-Job完成分布式任务调度。
最终版本在分支final中
