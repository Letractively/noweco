@echo off

setLocal EnableExtensions EnableDelayedExpansion

set DIRNAME=%~dp0

if "%JAVA_HOME%" == "" (set JAVA=java) else (set JAVA=%JAVA_HOME%\bin\java.exe)

set CLASSPATH=
for %%j in (%DIRNAME%\lib\*.jar) do (
  if "!CLASSPATH!" == "" (set CLASSPATH=%%j) else (set CLASSPATH=!CLASSPATH!;%%j)
)

if "%NOWECO_DEBUG%" neq "" (set JAVA_OPTS=%JAVA_OPTS% -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000)
  
%JAVA% %JAVA_OPTS% -cp %CLASSPATH% -Dlogback.configurationFile=%DIRNAME%\logback.xml com.googlecode.noweco.cli.NowecoCLI %DIRNAME% 1>%DIRNAME%\logs\out.console 2>%DIRNAME%\logs\err.console

setLocal DisableDelayedExpansion
