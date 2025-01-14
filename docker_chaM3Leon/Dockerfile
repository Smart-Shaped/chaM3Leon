FROM apache/hadoop-runner:jdk11-u2204
ARG HADOOP_URL=https://dlcdn.apache.org/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz
USER root

COPY envtoconf.py /opt/envtoconf.py
RUN chmod a+x /opt/envtoconf.py

COPY starter.sh /opt/starter.sh
RUN chmod a+x /opt/starter.sh

WORKDIR /opt

RUN sudo rm -rf /opt/hadoop && curl -LSs -o hadoop.tar.gz $HADOOP_URL && tar zxf hadoop.tar.gz && rm hadoop.tar.gz && mv hadoop* hadoop && rm -rf /opt/hadoop/share/doc
WORKDIR /opt/hadoop
ADD log4j.properties /opt/hadoop/etc/hadoop/log4j.properties
RUN sudo chown -R hadoop:users /opt/hadoop/etc/hadoop/*
ENV HADOOP_CONF_DIR /opt/hadoop/etc/hadoop

RUN id -u spark &>/dev/null || useradd spark -G hadoop

ENTRYPOINT ["/usr/local/bin/dumb-init", "--", "/opt/starter.sh"]
