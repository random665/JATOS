rem JATOS loader for Windows
@echo off
setlocal EnableDelayedExpansion

rem Change IP address and port here
set address=127.0.0.1
set port=9000

rem Don't change after here unless you know what you're doing
rem ###################################

set JATOS_HOME=%~dp0
set JATOS_HOME=%JATOS_HOME:~0,-1%

rem Detect if we were double clicked
for %%x in (%cmdcmdline%) do if %%~x==/c set DOUBLECLICKED=1
if defined DOUBLECLICKED (
  call :start
  exit
)

rem If we were started from CMD, evaluate start parameter
if "%1"=="start" (
  call :start
  exit /b
) else if "%1"=="stop" (
  call :stop
  exit /b
) else (
  @echo "Usage: loader.bat start|stop"
  exit /b
)

exit /b

rem ### Functions ###

:start
  IF EXIST "%JATOS_HOME%\RUNNING_PID" (
    echo JATOS already running
	if defined DOUBLECLICKED pause
    goto:eof
  )

  echo Starting JATOS
  rem # Generate application secret for the Play framework
  rem # If it's the first start, create a new secret, otherwise load it from the file.
  IF NOT EXIST "%JATOS_HOME%\application.secret" (
    set rand=%RANDOM%%RANDOM%%RANDOM%
    echo !rand!>"%JATOS_HOME%\application.secret"
  )
  set /p SECRET=<"%JATOS_HOME%\application.secret"

  IF NOT EXIST "%JATOS_HOME%\bin\jatos.bat" (
    echo %JATOS_HOME%\bin\jatos.bat doesn't exist!
    exit /b 1
  )
  
  call :checkjava

  rem # Start JATOS with configuration file and application secret
  set JATOS_OPTS=-Dconfig.file="%JATOS_HOME%\conf\production.conf" -Dapplication.secret=!SECRET! -Dhttp.port=%port% -Dhttp.address=%address%
  if defined DOUBLECLICKED (
    set JATOS_OPTS=-Dpidfile.path="NUL" %JATOS_OPTS%
  ) else (
    set JATOS_OPTS=-Dpidfile.path="%JATOS_HOME%\RUNNING_PID" %JATOS_OPTS%
  )
  set "APP_CLASSPATH=%JATOS_HOME%\lib\*"
  set "APP_MAIN_CLASS=play.core.server.NettyServer"
  set CMD=%JAVACMD% %JATOS_OPTS% -cp "%APP_CLASSPATH%" %APP_MAIN_CLASS%
  cd %JATOS_HOME%
  if defined DOUBLECLICKED (
    start /b %CMD%
  ) else (
    start /b %CMD% > nul
  )
  
  goto:eof

:stop
  if not exist "%JATOS_HOME%\RUNNING_PID" (
    echo JATOS isn't running
    goto:eof
  )
  echo Stopping JATOS
  set /p PID=<"%JATOS_HOME%\RUNNING_PID"
  taskkill /pid %PID% /f
  if errorlevel 1 (
    echo ...failed
  ) else (
    del "%JATOS_HOME%\RUNNING_PID"
    echo ...stopped
  )
  goto:eof
  
:checkjava
  if not "%JAVA_HOME%"=="" (
    if exist "%JAVA_HOME%\bin\java.exe" set "JAVACMD=%JAVA_HOME%\bin\java.exe"
  )
  
  if "%JAVACMD%"=="" set JAVACMD=java

  rem Detect if this java is ok to use.
  for /F %%j in ('"%JAVACMD%" -version  2^>^&1') do (
    if %%~j==Java set JAVAINSTALLED=1
  )
  
  if defined %JAVAINSTALLED% (
    echo.
    echo A Java JDK or JRE is not installed or can't be found.
    if not "%JAVA_HOME%"=="" (
      echo JAVA_HOME = "%JAVA_HOME%"
    )
    echo.
    echo Please go to
    echo   http://www.oracle.com/technetwork/java/javase/downloads/index.html
    echo and download a valid Java JRE and install before running JATOS.
    echo.
    echo If you think this message is in error, please check
    echo your environment variables to see if "java.exe" is
    echo available via JAVA_HOME or PATH.
    echo.
    if defined DOUBLECLICKED pause
    exit /B 1
  )
  goto:eof
  

