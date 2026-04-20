#!/bin/bash

# 启动后端
echo "Starting backend server..."
cd /workspace/backend
nohup java -jar target/ppt-ai-backend-0.0.1-SNAPSHOT.jar > /tmp/backend.log 2>&1 &
BACKEND_PID=$!
echo "Backend started with PID: $BACKEND_PID"

# 等待后端启动
echo "Waiting for backend to start..."
sleep 20

# 启动前端
echo "Starting frontend server..."
cd /workspace/frontend
npm run dev
