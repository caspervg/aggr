FROM bde2020/spark-java-template:1.6.2-hadoop2.6

MAINTAINER Casper Van Gheluwe <caspervg@gmail.com>

ENV SPARK_APPLICATION_MAIN_CLASS net.caspervg.aggr.exec.AggrMasterMain
ENV SPARK_APPLICATION_JAR_NAME aggr-0.3.0-with-dependencies

ENV APP_ARGS_SERVICE=http://database:8890/sparql/

ADD run/ run

RUN chmod -R +x run/

CMD ["mkdir -p", "/tmp/spark-events"]
CMD ["/bin/bash", "run/master.sh"]