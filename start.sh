#!/bin/bash

echo "======================================"
echo "AI PPT Generator - 快速启动脚本"
echo "======================================"

# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo "错误：Docker 未安装，请先安装 Docker"
    exit 1
fi

# 检查 Node.js
if ! command -v node &> /dev/null; then
    echo "错误：Node.js 未安装，请先安装 Node.js 18+"
    exit 1
fi

# 检查 Java
if ! command -v java &> /dev/null; then
    echo "错误：Java 未安装，请先安装 Java 17+"
    exit 1
fi

echo ""
echo "1. 启动 Docker 容器 (MySQL, Redis, MailHog)..."
cd backend
docker-compose up -d

if [ $? -ne 0 ]; then
    echo "错误：Docker 容器启动失败"
    exit 1
fi

echo "等待数据库启动..."
sleep 10

echo ""
echo "2. 启动后端服务..."
cd ..

# 检查 Maven
if ! command -v mvn &> /dev/null; then
    echo "Maven 未安装，尝试使用 Maven Wrapper..."
    if [ -f "mvnw" ]; then
        ./mvnw spring-boot:run &
    else
        echo "错误：Maven 未安装且找不到 Maven Wrapper"
        exit 1
    fi
else
    cd backend
    mvn spring-boot:run &
    cd ..
fi

BACKEND_PID=$!
echo "后端服务启动中... (PID: $BACKEND_PID)"

# 等待后端启动
echo "等待后端服务就绪..."
sleep 30

echo ""
echo "3. 启动前端服务..."
cd frontend

# 检查 node_modules
if [ ! -d "node_modules" ]; then
    echo "安装前端依赖..."
    npm install
fi

npm run dev &
FRONTEND_PID=$!
echo "前端服务启动中... (PID: $FRONTEND_PID)"

echo ""
echo "======================================"
echo "启动完成！"
echo "======================================"
echo ""
echo "服务访问地址:"
echo "  前端：http://localhost:3000"
echo "  后端：http://localhost:8080"
echo "  Swagger: http://localhost:8080/swagger-ui.html"
echo "  MailHog: http://localhost:8025"
echo ""
echo "按 Ctrl+C 停止所有服务"

# 等待用户中断
wait
