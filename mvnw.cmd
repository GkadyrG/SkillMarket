@ECHO OFF
SETLOCAL

SET BASE_DIR=%~dp0
SET WRAPPER_DIR=%BASE_DIR%.mvn\wrapper
SET MAVEN_VERSION=3.9.9
SET ARCHIVE_NAME=apache-maven-%MAVEN_VERSION%-bin.zip
SET ARCHIVE_PATH=%WRAPPER_DIR%\%ARCHIVE_NAME%
SET MAVEN_HOME=%WRAPPER_DIR%\apache-maven-%MAVEN_VERSION%

IF NOT EXIST "%MAVEN_HOME%" (
  IF NOT EXIST "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"
  IF NOT EXIST "%ARCHIVE_PATH%" (
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/%ARCHIVE_NAME%' -OutFile '%ARCHIVE_PATH%'"
  )
  powershell -Command "Expand-Archive -Path '%ARCHIVE_PATH%' -DestinationPath '%WRAPPER_DIR%' -Force"
)

"%MAVEN_HOME%\bin\mvn" %*

ENDLOCAL
