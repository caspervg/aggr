#!/usr/bin/env bash

export SPARK_APPLICATION_ARGS="
    -i ${APP_ARGS_INPUT}
    -o ${APP_ARGS_OUTPUT}
    -s ${APP_ARGS_SERVICE}
    --write-data-csv ${APP_ARGS_WRITE_DATA_CSV}
    grid
    -g ${APP_ARGS_GRID_GRIDSIZE}
    ${APP_ARGS_DYNAMIC}"

sh /template.sh