@ECHO OFF

SET WORKING=C:\Users\benoit\dev\IDEA\mathops_persistence
SET JARS=%WORKING%\lib

REM ---------------------------------------------------------------------------
ECHO =
ECHO = Deploying to Tomcat server on LOCALHOST
ECHO =
REM ---------------------------------------------------------------------------

CD %JARS%
DIR ROOT.*

COPY ROOT.war C:\opt\tomcat\webapps\.

ECHO.
PAUSE
