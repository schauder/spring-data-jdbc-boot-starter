#!/bin/bash -x

if [ "${TRAVIS_PULL_REQUEST}" = "false" -a "${TRAVIS_BRANCH}" = "master" ]; then
	mvn -Pdistribute,snapshot clean deploy
else
	mvn clean dependency:list test -Dsort
fi