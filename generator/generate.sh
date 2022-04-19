#!/bin/bash

fakeit() {
  docker run --rm -v ${PWD}:/data -w /data --network host bentonam/fakeit "$@"
}

cbq() {
  docker run --rm --network host couchbase:7.0.3 cbq "$@"
}

# Generate fake data
if [ -z "$1" ] || [ "$1" = "local" ]; then
  echo "Cleaning old generated data"
  rm -rf results
  echo "Generating data to folder"
  fakeit folder -v results models/*.yml "$( (( "$2" )) && printf %s "-c $2" )"
elif [ "$1" = "couchbase" ]; then
  echo "Cleaning old generated data"
  cbq -q -e "http://localhost:8091" -s "$(cat requests/clean.n1ql)" -u Administrator -p password

  echo "Generate new data"
  fakeit couchbase -v -b jhipster -u Administrator -p password models/*.yml "$( (( "$2" )) && printf %s "-c $2" )"
fi
