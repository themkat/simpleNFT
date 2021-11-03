#!/bin/bash

# TODO: more here

# download openzeppelin contracts directly so we don't have to use npm
wget -qO- https://github.com/OpenZeppelin/openzeppelin-contracts/archive/refs/tags/v4.3.2.tar.gz | tar zxvf - 
mv openzeppelin-contracts-4.3.2 @openzeppelin

# TODO: compile 
