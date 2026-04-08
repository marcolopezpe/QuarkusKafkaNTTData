#!/bin/bash

echo ''
echo '###########################################################'
echo 'Eliminacion del stack de contenedores"'
echo '###########################################################'

docker compose -f 01-kafka-server.yml \
							-f 02-postgres.yml \
				-p votes-app \
				down

rm -rf ./zoo/data/* ./zoo/log/*

echo ''
echo '#########################################################################'
echo 'Red "votes-net" eliminada'
echo '#########################################################################'

docker network rm votes-net
