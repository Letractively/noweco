cls
@ECHO OFF

set nomAffichage="Noweco Service"
set description="Service de Gestion du Serveur de mail Noweco"

sc delete Noweco > nul
set currectFolder=%CD%

sc create Noweco binPath= "%currectFolder%\noweco.exe start" start= auto DisplayName= %nomAffichage%
sc description Noweco %description%
sc failure Noweco command= "%currectFolder%\noweco.exe stop"