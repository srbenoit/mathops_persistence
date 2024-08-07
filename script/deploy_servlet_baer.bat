@ECHO OFF

SET WORKING=C:\Users\benoit\dev\IDEA\mathops_persistence
SET JARS=%WORKING%\jars
SET SCP=\bin\winscp /console
SET HOST=online@baer.math.colostate.edu

REM ---------------------------------------------------------------------------
ECHO =
ECHO = Deploying to Tomcat server on BAER
ECHO =
REM ---------------------------------------------------------------------------

CD %JARS%
DIR ROOT.*

%SCP% "%HOST%" /command "lcd %JARS%" "cd /home/online" "put -nopreservetime ROOT.war" "mv ROOT.war /opt/tomcat/webapps/." "exit"

ECHO.
PAUSE
