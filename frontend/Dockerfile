FROM node:latest

WORKDIR /app
COPY . .

RUN npm install
RUN npm run build --prod

CMD ["export", "NODE_OPTIONS=--max-old-space-size=8192"]
CMD ["npm", "start"]
