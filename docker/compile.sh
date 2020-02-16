#!/bin/bash

info () {
  echo -e "\e[32m$1"
}

error () {
  echo -e "\e[31m$1"
}

# Compile jMetal
mvn package

# Dockerize VRPRunner
docker build --no-cache -f docker/Dockerfile-vrp -t vrp:v1 .

info "Execute VRP using:"
info "\tmkdir output && docker run -it --rm -v \"\$(pwd)\"/output:/data/output vrp:v1"
