# Sử dụng Node.js 23 làm base image cho giai đoạn build
FROM node:23-slim AS build

# Đặt thư mục làm việc trong container
WORKDIR /app

# Sao chép file package.json và package-lock.json vào thư mục làm việc
COPY package.json /app/package.json
COPY package-lock.json /app/package-lock.json

# Cài đặt các dependencies
RUN npm install --force

# Cấp quyền thực thi cho các file trong thư mục node_modules/.bin
RUN find /app/node_modules/.bin -type f -exec chmod +x {} \;

# Sao chép toàn bộ mã nguồn vào thư mục làm việc trong container
COPY . /app

# Kiểm tra xem Vite đã được cài đặt đúng chưa (tuỳ chọn, có thể bỏ qua nếu không cần)
RUN npx vite --version

# Build ứng dụng Vue.js (sản xuất)
RUN npm run build

# Sử dụng Nginx làm base image cho giai đoạn production
FROM nginx:1.27.5-alpine-slim

# Sao chép các file đã được build từ giai đoạn trước vào thư mục của Nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Sao chép file cấu hình Nginx (nếu có)
COPY /nginx/nginx.conf /etc/nginx/nginx.conf

# Khởi động Nginx và giữ container chạy ở foreground
CMD ["nginx", "-g", "daemon off;"]
