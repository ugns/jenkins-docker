FROM jenkins/jenkins:lts-alpine

MAINTAINER Jeremy T. Bouse <Jeremy.Bouse@UnderGrid.net>

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

USER root
RUN apk upgrade --no-cache && rm -rf /var/cache/apk/*

USER jenkins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
COPY init.groovy.d/ /usr/share/jenkins/ref/init.groovy.d/
