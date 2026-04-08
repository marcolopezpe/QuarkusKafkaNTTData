#!/bin/bash

echo ''
echo '########################################################################'
echo 'Creacion de la red para todos los contenedores "votes-net"'
echo '########################################################################'

docker network create --driver=bridge --subnet=172.15.0.0/16 votes-net

echo ''
echo '###########################################################'
echo 'Creacion del stack de contenedores'
echo '###########################################################'

docker compose -f 01-kafka-server.yml \
				-f 02-postgres.yml \
				-p votes-app \
				up -d

echo ''
echo '###########################################################'
echo 'Creacion de los topicos de kafka'
echo '###########################################################'

docker exec -it kafka-broker-1 kafka-topics --bootstrap-server localhost:19092 --if-not-exists --create --topic votes-topic --partitions 1 --replication-factor 1
docker exec -it kafka-broker-1 kafka-topics --bootstrap-server localhost:19092 --if-not-exists --create --topic votes-confirmed-topic --partitions 1 --replication-factor 1

echo ''
