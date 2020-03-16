FROM nginx:1.17.7-alpine
LABEL maintainer="https://github.com/adorsys/open-banking-gateway"
COPY nginx.conf /etc/nginx/nginx.conf
COPY dist/consent-ui /app/dist
CMD ["nginx", "-g", "daemon off;"]
EXPOSE 4200
