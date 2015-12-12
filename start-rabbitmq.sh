#!/usr/bin/env bash

docker stop rabbitmq
echo "rabbitmq stopped"

docker rm rabbitmq
echo "rabbitmq removed"

docker run --name rabbitmq -it --rm -p 5672:5672 rabbitmq:3.5.6
