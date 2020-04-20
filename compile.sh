#!/bin/bash

info () {
  echo -e "\e[32m$1"
}

error () {
  echo -e "\e[31m$1"
}

####
# VRP Runner

# Copy Dockerfile to project root
cp jmetal-example/docker/Dockerfile .

# Compile jMetal
JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/ mvn clean javadoc:jar package

# Dockerize VRPRunner
docker build --no-cache -f Dockerfile -t vrp:v1 .

# Remove Dockerfile
rm Dockerfile

info "Execute VRP using:"
info "\tmkdir output && docker run -it --rm -v \"\$(pwd)\"/output:/data/output vrp:v1"
