@echo off
REM =====================================================================
REM VDEMY - Switch Environment Script
REM =====================================================================
REM Usage:
REM   switch-env.bat dev       → Copy .env.dev thành .env
REM   switch-env.bat staging   → Copy .env.staging thành .env
REM   switch-env.bat prod      → Giữ nguyên .env (dùng cho Docker)
REM =====================================================================

if "%1"=="" (
    echo.
    echo  Usage: switch-env.bat [dev^|staging^|prod]
    echo.
    echo  Environments:
    echo    dev      - Local development (localhost)
    echo    staging  - Staging environment (Docker)
    echo    prod     - Production environment (Docker)
    echo.
    echo  Current .env:
    findstr /B "SPRING_PROFILES_ACTIVE" .env 2>nul
    echo.
    exit /b 1
)

if "%1"=="dev" (
    if not exist ".env.dev" (
        echo [ERROR] File .env.dev not found!
        exit /b 1
    )
    copy /Y .env.dev .env >nul
    echo [OK] Switched to DEV environment
    echo     - Hosts: localhost
    echo     - MySQL: localhost:3307
    echo     - Redis: localhost:6379
    echo     - Kafka: localhost:9094
    echo.
    echo Run: ./mvnw spring-boot:run
    exit /b 0
)

if "%1"=="staging" (
    if not exist ".env.staging" (
        echo [ERROR] File .env.staging not found!
        exit /b 1
    )
    copy /Y .env.staging .env >nul
    echo [OK] Switched to STAGING environment
    echo     - Hosts: Docker containers
    echo     - Profile: staging (verbose logging)
    echo.
    echo Run: docker-compose up -d
    exit /b 0
)

if "%1"=="prod" (
    echo [OK] Production environment is the default .env
    echo     - Hosts: Docker containers
    echo     - Profile: prod (minimal logging)
    echo.
    echo Run: docker-compose up -d
    exit /b 0
)

echo [ERROR] Unknown environment: %1
echo Use: dev, staging, or prod
exit /b 1
