#!/bin/bash

echo "=== Invoice Backend 실행 스크립트 ==="
echo

# Java 설치 확인
if ! command -v java &> /dev/null; then
    echo "❌ Java가 설치되어 있지 않습니다."
    echo "다음 명령어로 Java를 설치해주세요:"
    echo "sudo apt update && sudo apt install -y openjdk-17-jdk"
    exit 1
fi

# Maven 설치 확인
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven이 설치되어 있지 않습니다."
    echo "다음 명령어로 Maven을 설치해주세요:"
    echo "sudo apt update && sudo apt install -y maven"
    exit 1
fi

echo "✅ Java 버전: $(java -version 2>&1 | head -n1)"
echo "✅ Maven 버전: $(mvn -version | head -n1)"
echo

# 프로젝트 빌드
echo "📦 프로젝트 빌드 중..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "❌ 빌드 실패"
    exit 1
fi

echo "✅ 빌드 완료"
echo

# 데이터베이스 선택
echo "사용할 데이터베이스를 선택하세요:"
echo "1) H2 Database (기본, 개발용)"
echo "2) MySQL"
echo "3) PostgreSQL"
read -p "선택 (1-3, 기본값: 1): " db_choice

case $db_choice in
    2)
        echo "🚀 MySQL 프로파일로 애플리케이션 시작..."
        mvn spring-boot:run -Dspring-boot.run.profiles=mysql
        ;;
    3)
        echo "🚀 PostgreSQL 프로파일로 애플리케이션 시작..."
        mvn spring-boot:run -Dspring-boot.run.profiles=postgresql
        ;;
    *)
        echo "🚀 H2 Database로 애플리케이션 시작..."
        echo "📝 H2 콘솔: http://localhost:8080/h2-console"
        echo "📝 JDBC URL: jdbc:h2:mem:testdb"
        echo "📝 Username: sa"
        echo "📝 Password: (비워두기)"
        echo
        mvn spring-boot:run
        ;;
esac