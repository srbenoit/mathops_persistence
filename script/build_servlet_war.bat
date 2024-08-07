@ECHO OFF

cd C:\Users\benoit\dev\IDEA\mathops_persistence

java -classpath C:\Users\benoit\dev\IDEA\mathops_persistence\jars\mathops_commons.jar;C:\Users\benoit\dev\IDEA\mathops_persistence\build\classes\java\main dev.mathops.persistence.deploy.ServletWarBuilder

cd jars
dir ROOT.*

pause