#!/usr/bin/env bash

export SPARK_APPLICATION_ARGS="
    -i ${APP_ARGS_INPUT}
    -o ${APP_ARGS_OUTPUT}
    -s ${APP_ARGS_SERVICE}
    --write-data-csv ${APP_ARGS_WRITE_DATA_CSV}
    kmeans
    -n ${APP_ARGS_KMEANS_ITERATIONS}
    -k ${APP_ARGS_KMEANS_CENTROIDS}
    -m ${APP_ARGS_KMEANS_METRIC}
    ${APP_ARGS_DYNAMIC}"

sh /template.sh