#!/usr/bin/env bash

printenv

export SPARK_APPLICATION_ARGS="
    -i ${APP_ARGS_INPUT}
    -o ${APP_ARGS_OUTPUT}
    -s ${APP_ARGS_SERVICE}
    --write-data-csv ${APP_ARGS_WRITE_DATA_CSV}
    time
    -d ${APP_ARGS_TIME_MAX_DETAIL}
    ${APP_ARGS_DYNAMIC}"

sh /template.sh