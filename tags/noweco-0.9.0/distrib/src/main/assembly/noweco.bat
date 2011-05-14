@echo off

setLocal EnableExtensions EnableDelayedExpansion

set DIRNAME=%~dp0

if "%DIRNAME:~-1%" == "\" (set DIRNAME=%DIRNAME:~0,-1%)

if "%JAVA_HOME%" == "" (set JAVA=java) else (set JAVA=%JAVA_HOME%\bin\java.exe)

set CLASSPATH=
for %%j in ("%DIRNAME%\lib\*.jar") do (
  if "!CLASSPATH!" == "" (set CLASSPATH=%%~fj) else (set CLASSPATH=!CLASSPATH!;%%~fj)
)

if "%NOWECO_DEBUG%" neq "" (set JAVA_OPTS=%JAVA_OPTS% -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000)
  
setLocal DisableDelayedExpansion

%JAVA% %JAVA_OPTS% -cp "%CLASSPATH%" -Dlogback.configurationFile="%DIRNAME%\logback.xml" com.googlecode.noweco.cli.NowecoCLI "%DIRNAME%" 1>"%DIRNAME%\logs\out.console" 2>"%DIRNAME%\logs\err.console"
