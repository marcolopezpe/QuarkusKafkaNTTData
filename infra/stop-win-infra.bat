@echo off

echo.
echo ###########################################################
echo Eliminacion del stack de contenedores
echo ###########################################################

docker compose -f 01-kafka-server.yml ^
               -f 02-postgres.yml ^
               -p votes-app ^
               down

REM Eliminar datos de ZooKeeper
rmdir /s /q .\zoo\data
rmdir /s /q .\zoo\log

mkdir .\zoo\data
mkdir .\zoo\log

echo.
echo #########################################################################
echo Red "votes-net" eliminada
echo #########################################################################

docker network rm votes-net

pause