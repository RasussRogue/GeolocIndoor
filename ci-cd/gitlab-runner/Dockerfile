FROM ubuntu:18.04

###########
# GRADLE #
###########
ARG GRADLE_VERSION=6.1
ARG GRADLE_DIST=bin
ENV GRADLE_HOME /opt/gradle/gradle-6.1
ENV PATH ${PATH}:${GRADLE_HOME}/bin
RUN  apt-get update \
  && apt-get install -y wget \
  && apt-get install -y unzip

RUN mkdir /opt/gradle && \
    cd /opt/gradle && \
    wget -q https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-${GRADLE_DIST}.zip && \
    unzip gradle*.zip && \
    ls -d */ | sed 's/\/*$//g' | xargs -I{} mv {} gradle && \
    rm gradle*.zip

#########
# JDK 8 #
#########
RUN apt-get -y install openjdk-8-jdk
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV PATH ${PATH}:$JAVA_HOME/bin

###########
# ANDROID #
###########
ARG ANDROID_SDK_VERSION=4333796
ENV ANDROID_HOME /opt/android-sdk
ENV ANDROID_SDK_ROOT /opt/android-sdk
RUN mkdir -p ${ANDROID_HOME} && cd ${ANDROID_HOME} && \
    wget -q https://dl.google.com/android/repository/sdk-tools-linux-${ANDROID_SDK_VERSION}.zip && \
    unzip *tools*linux*.zip && \
    rm *tools*linux*.zip
RUN mkdir -p ${ANDROID_HOME}/licenses/ \
  && echo "8933bad161af4178b1185d1a37fbf41ea5269c55\nd56f5187479451eabf01fb78af6dfcb131a6481e\n24333f8a63b6825ea9c5514f83c2829b004d1fee" > ${ANDROID_HOME}/licenses/android-sdk-license \
  && echo "84831b9409646a918e30573bab4c9c91346d8abd\n504667f4c0de7af1a06de9f4b1727b84351f2910" > ${ANDROID_HOME}/licenses/android-sdk-preview-license
RUN ${ANDROID_HOME}/tools/bin/sdkmanager "platform-tools" "platforms;android-29" "build-tools;29.0.2" "ndk;21.0.6113669" "lldb;3.1" "cmake;3.6.4111459"
ENV PATH ${PATH}:${ANDROID_HOME}/emulator:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/tools/bin

#######
# ADB #
#######
#RUN apt-get -y install adb
#RUN $ANDROID_HOME/emulator/emulator -avd Nexus_5X_API_29_x86 -wipe-data

#######
# NPM #
#######
#curl -sL https://deb.nodesource.com/setup_12.x | sudo -E bash -
RUN wget https://deb.nodesource.com/setup_12.x
RUN apt-get install -y nodejs

##########
# JDK 11 #
##########
RUN apt-get -y install openjdk-11-jdk
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV PATH ${PATH}:$JAVA_HOME/bin

########
# DATA #
########
RUN mkdir -p /data/spring-app/
RUN mkdir -p /data/android-app/

#ENTRYPOINT [ "switch", "shell=/bin/bash", "--", "codep", "/bin/docker daemon" ]