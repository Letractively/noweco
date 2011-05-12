cls
@echo off
echo Ce batch ne fonctionne pas, en effet l'affectation de variable dans la boucle for construisant le classpath de la commande Java ne fonctionne pas !!!

if "%1"=="start" (
		echo start
		set currectFolder=%CD%
		
		if "%JAVA_HOME%" == "" (
		   set JAVA=java
		)   else (
		   set JAVA=%JAVA_HOME%\bin\java.exe
		)
		echo ----%JAVA%
		
		set LC="%JAVA%\jre\lib\rt.jar"
		echo %LC%
		for %%j in (lib\*.jar) do set LC=%LC%;%%j
		echo ----LC=%LC%
		
		rem if exist lcp.bat del lcp.bat
		rem for %%j in (lib\*.jar) do echo set LC=%%LC%%;%%j >> lcp.bat
		rem echo echo %%LC%% >> lcp.bat
		rem call lcp.bat
		rem echo %LC%
		
		rem SETLOCAL enableDelayedExpansion
		rem set lalire=
		rem echo lalire is ----%lalire%;
		
		rem set tmp=
		rem FOR %%F IN (lib/*.jar) DO (
		rem 	set fichier=%%F
		rem 	set tmp=;lib/!fichier!
		rem 	echo tmp is %tmp%
		rem 	set lalire=!lalire!%tmp%
		rem 	echo lalire is %lalire%
		rem )
		rem ENDLOCAL
		
		rem echo ----%lalire%
		%JAVA% -cp %LC% -Dlogback.configurationFile="%currectFolder%/logback.xml" com.googlecode.noweco.cli.NowecoCLI
) ELSE (
	if "%1"=="stop" (
		echo stop;
	) ELSE (
		CALL :usage %0;
	)
)

GOTO :EOF

:usage
	echo Preciser une action a effectuer sur le service :
	echo noweco start ou noweco stop
GOTO :EOF