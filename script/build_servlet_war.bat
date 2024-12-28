@ECHO OFF

cd C:\Users\benoit\dev\IDEA\mathops_persistence

java -classpath C:\Users\benoit\dev\IDEA\mathops_persistence\lib\mathops_commons.jar;C:\Users\benoit\dev\IDEA\mathops_persistence\build\classes\java\main dev.mathops.persistence.deploy.ServletWarBuilder

cd lib
dir ROOT.*

pause