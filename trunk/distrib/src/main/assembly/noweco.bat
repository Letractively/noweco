@echo off

Rem ######################
Rem # Arguments
Rem #
Rem # <empty> : start noweco
Rem # stop : stop current noweco instance
Rem #
Rem ######################
Rem # System properties
Rem # 
Rem # JAVA_HOME : the location of the JVM
Rem # NOWECO_DEBUG : is this variable has not empty value, the debug mode is active
Rem # JAVA_OPTS : java system properties to be set
Rem # NOWECO_OPTS : java system properties to be set, if set JAVA_OPTS is ignored
Rem ######################

setLocal EnableExtensions EnableDelayedExpansion

set DIRNAME=%~dp0
if "%DIRNAME:~-1%" == "\" (set DIRNAME=%DIRNAME:~0,-1%)

if "%JAVA_HOME%" == "" (set JAVA=java.exe) else (set JAVA=%JAVA_HOME%\bin\java.exe)

set CLASSPATH=
for %%j in ("%DIRNAME%\lib\*.jar") do (
  if "!CLASSPATH!" == "" (set CLASSPATH=%%~fj) else (set CLASSPATH=!CLASSPATH!;%%~fj)
)

set MAIN_CLASS=com.googlecode.noweco.cli.StartNoweco
if "%1" == "stop" (set MAIN_CLASS=com.googlecode.noweco.cli.StopNoweco)

if "%NOWECO_OPTS%" == "" (set NOWECO_OPTS=%JAVA_OPTS%)

if "%NOWECO_DEBUG%" neq "" (set NOWECO_OPTS=%NOWECO_OPTS% -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000)

set NOWECO_OPTS=%NOWECO_OPTS% -Dcom.sun.management.jmxremote -Dlogback.configurationFile="%DIRNAME%/logback.xml" -Dnoweco.home="%DIRNAME%"

setLocal DisableDelayedExpansion

"%JAVA%" %NOWECO_OPTS% -cp "%CLASSPATH%" %MAIN_CLASS%
