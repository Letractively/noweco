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
		
		
		
		SETLOCAL enableDelayedExpansion
		set lalire=
		echo lalire is ----%lalire%;
		set tmp=
		FOR %%F IN (lib/*.jar) DO (
			set fichier=%%F
			set tmp=;lib/!fichier!
			echo tmp is %tmp%
			set lalire=!lalire!%tmp%
			echo lalire is %lalire%
		)
		ENDLOCAL
		
		echo ----%lalire%
		%JAVA% -cp %lalire% -Dlogback.configurationFile="%currectFolder%/logback.xml" com.googlecode.noweco.cli.NowecoCLI
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