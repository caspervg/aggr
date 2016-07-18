FROM caspervg/docker-spark-java-template:latest

MAINTAINER Casper Van Gheluwe <caspervg@gmail.com>

ENV SPARK_APPLICATION_MAIN_CLASS net.caspervg.aggr.exec.AggrMain
ENV SPARK_APPLICATION_JAR_NAME aggr-0.1.1-with-dependencies

# Global
ENV APP_ARGS_INPUT=/user/test/localhost.csv
ENV APP_ARGS_OUTPUT=/user/test/output
ENV APP_ARGS_SERVICE=http://database:8890/sparql/
ENV APP_ARGS_WRITE_DATA_CSV=true

# Time
ENV APP_ARGS_TIME_MAX_DETAIL=4

# KMeans
ENV APP_ARGS_KMEANS_ITERATIONS=50
ENV APP_ARGS_KMEANS_CENTROIDS=25
ENV APP_ARGS_KMEANS_METRIC=EUCLIDEAN

# Grid
ENV APP_ARGS_GRID_GRIDSIZE=0.005

ENV APP_ARGS_DYNAMIC=""

ADD run/ run

RUN chmod -R +x run/

CMD ["/bin/bash", "run/time.sh"]