FROM node:18-alpine
WORKDIR /usr/src/app
RUN npm install -g http-server
COPY . .
EXPOSE 5001
ENTRYPOINT ["http-server", "-p", "5001", "."]