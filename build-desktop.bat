@echo off
REM ==============================================================================
REM Script de build et packaging complet de l'application Desktop Standard Naast (Windows)
REM ==============================================================================

setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
set "ELECTRON_DIR=%SCRIPT_DIR%standardnaast-electron"
set "NG_DIR=%SCRIPT_DIR%standardnaast-ng"
set "BACKEND_DIR=%SCRIPT_DIR%standardnaast-backend"

set "TARGET=%~1"
if "%TARGET%"=="" set "TARGET=win"

echo =================================================================
echo   Build ^& Packaging Desktop - Standard de Naast (Windows)
echo =================================================================

REM 1. Verification de Node.js et pnpm
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERREUR] Node.js est introuvable dans le PATH.
    pause
    exit /b 1
)

where pnpm >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERREUR] pnpm est introuvable dans le PATH.
    pause
    exit /b 1
)

REM 2. Verification de Maven
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERREUR] Maven (mvn) est introuvable dans le PATH.
    pause
    exit /b 1
)

REM 3. Installation des dependances si necessaire
if not exist "%ELECTRON_DIR%\node_modules" (
    echo [INFO] Installation des dependances Electron...
    cd /d "%ELECTRON_DIR%" && call pnpm install
)

if not exist "%NG_DIR%\node_modules" (
    echo [INFO] Installation des dependances Angular...
    cd /d "%NG_DIR%" && call pnpm install
)

REM 4. Lancement du packaging
cd /d "%ELECTRON_DIR%"

if /i "%TARGET%"=="win" (
    echo [INFO] Packaging Windows NSIS (x64)...
    call pnpm run dist:win
) else if /i "%TARGET%"=="all" (
    echo [INFO] Packaging multi-plateformes...
    call pnpm run dist:all
) else (
    echo [INFO] Packaging selon cible: %TARGET%...
    call pnpm run dist
)

if %errorlevel% neq 0 (
    echo [ERREUR] Le packaging a echoue.
    pause
    exit /b %errorlevel%
)

echo =================================================================
echo   Build termine avec succes !
echo   Dossier de sortie : %ELECTRON_DIR%\dist-package\
echo =================================================================
endlocal
