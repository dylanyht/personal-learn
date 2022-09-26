# 环境
FROM  openjdk:8-jdk-alpine

ARG pkgname
ENV pkgname=${pkgname}

#拷贝jar
COPY ${pkgname} /app.jar

#端口号

EXPOSE 8080
# 执行命令
ENTRYPOINT ["java","-jar","/app.jar", "--server.port=8080"]
