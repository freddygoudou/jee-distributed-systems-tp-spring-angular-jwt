# ===== Étape 1 : build Angular =====
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# ===== Étape 2 : service statique via nginx =====
FROM nginx:alpine
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/digital-banking-frontend/browser /usr/share/nginx/html
EXPOSE 80
