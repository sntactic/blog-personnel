FROM node:20-alpine AS build

WORKDIR /app

COPY blog-frontend/package*.json ./
RUN npm ci

COPY blog-frontend/. .
RUN npm run build -- --configuration production

FROM nginx:alpine

COPY --from=build /app/dist/blog-frontend/browser /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
