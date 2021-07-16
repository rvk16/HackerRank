FROM 10.232.52.103:5000/fnd-base-images/ocp-os-java:2.1.6

ENV APP_HOME=/opt/app
RUN mkdir -p ${APP_HOME}
COPY maven/aia-il-deployer.jar maven/application.yaml docker-entrypoint.sh ${APP_HOME}/
RUN chmod +x ${APP_HOME}/docker-entrypoint.sh

ENTRYPOINT ["/opt/app/docker-entrypoint.sh"]
