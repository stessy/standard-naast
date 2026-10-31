@echo off
setlocal
set "ROOT=%~dp0"
set "TYPE=%~1"
if "%TYPE%"=="" set "TYPE=app-image"
if /i "%TYPE%"=="win" set "TYPE=exe"
if /i "%TYPE%"=="app-image" goto typeok
if /i "%TYPE%"=="exe" goto typeok
if /i "%TYPE%"=="msi" goto typeok
echo Usage: build-desktop.bat [app-image^|exe^|msi]
exit /b 1

:typeok
set "PATH=%ROOT%standardnaast-ng\node;%PATH%"
for %%T in (java jpackage mvn node pnpm) do (
    where %%T >nul 2>nul
    if errorlevel 1 (
        echo Missing build tool: %%T. Use JDK 25, Maven, Node and pnpm.
        exit /b 1
    )
)
pushd "%ROOT%standardnaast-ng"
if not exist node_modules (
    call pnpm install
    if errorlevel 1 goto fail
)
call pnpm run build
if errorlevel 1 goto fail
if not exist dist\standardnaast-ng\browser\index.html goto fail
popd

pushd "%ROOT%"
call mvn -f standardnaast-backend\pom.xml -Pdesktop clean package
if errorlevel 1 goto fail
mkdir "standardnaast-backend\target\jpackage-input"
copy /y "standardnaast-backend\target\standardnaast-backend-1.0.0-SNAPSHOT.jar" "standardnaast-backend\target\jpackage-input\standardnaast.jar" >nul
if errorlevel 1 goto fail
> "standardnaast-backend\target\stop-launcher.properties" echo arguments=--desktop-stop
set "INSTALL_OPTIONS="
if /i not "%TYPE%"=="app-image" set "INSTALL_OPTIONS=--win-menu --win-shortcut --win-dir-chooser"
if /i "%TYPE%"=="app-image" if exist "%ROOT%dist-desktop\Standard Naast" (
    if not exist "%ROOT%dist-desktop\Standard Naast\app\Standard Naast.cfg" (
        echo Refusing to remove an unexpected app image.
        goto fail
    )
    echo Removing previous app image: "%ROOT%dist-desktop\Standard Naast"
    rmdir /s /q "%ROOT%dist-desktop\Standard Naast"
    if exist "%ROOT%dist-desktop\Standard Naast" goto fail
)
call jpackage --type "%TYPE%" --name "Standard Naast" --app-version 1.0.0 --vendor "Standard de Naast" --dest dist-desktop --input standardnaast-backend\target\jpackage-input --main-jar standardnaast.jar --arguments "--desktop" --java-options "-Dfile.encoding=UTF-8" --add-modules java.se,jdk.unsupported,jdk.crypto.ec,jdk.charsets,jdk.management,jdk.zipfs --add-launcher "Quitter Standard Naast=standardnaast-backend\target\stop-launcher.properties" %INSTALL_OPTIONS%
if errorlevel 1 goto fail
echo Package created in dist-desktop. Java is included.
popd
exit /b 0

:fail
popd
echo Desktop build failed.
exit /b 1
