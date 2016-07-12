#!/bin/bash

_nepi_version="652bc2e46cfe"

function installNepiEC {
 curl -s http://nepi.inria.fr/code/nepi/archive/${_nepi_version}.tar.gz|tar xzf -
 mv nepi-${_nepi_version} nepi
 echo "Setup the environment:"
 echo "  export PYTHONPATH=\$PYTHONPATH:$(pwd)/nepi/src"
}

function getNepiExample {
 curl -s -O https://raw.github.com/FITeagle/bootstrap/master/resources/nepi3/nepi3_example_ping.py
 mv nepi3_example_ping.py nepi
 echo "Run the first experiment:"
 echo "  python nepi/nepi3_example_ping.py"  
}

installNepiEC
getNepiExample
