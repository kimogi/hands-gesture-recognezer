FROM ubuntu:12.04
MAINTAINER stas.kraev - aka mail@kraev.me

RUN apt-get update && apt-get install -y git openjdk-7-jdk wget unzip freeglut3-dev libusb-1.0.0-dev && \
    mkdir -p /root/deps && \
    wget https://simple-openni.googlecode.com/files/OpenNI_NITE_Installer-Linux64-0.27.zip -O /root/deps/OpenNI_NITE_Installer-Linux64-0.27.zip && \
    cd /root/deps && unzip *.zip  && \
    cd /root/deps/OpenNI_NITE_Installer-Linux64-0.27 && cd OpenNI-Bin-Dev-Linux-x64-v1.5.4.0 && ./install.sh && \
    cd /root/deps/OpenNI_NITE_Installer-Linux64-0.27 && cd NITE-Bin-Dev-Linux-x64-v1.5.2.21 && ./install.sh && \
    cd /root/deps/OpenNI_NITE_Installer-Linux64-0.27 && cd Sensor-Bin-Linux-x64-v5.1.2.1 && ./install.sh && \
    cd /root/deps/OpenNI_NITE_Installer-Linux64-0.27 && cd kinect/Sensor-Bin-Linux-x64-v5.1.2.1 && ./install.sh

ADD . /root/hands-gesture-recognezer
RUN cd /root/hands-gesture-recognezer  && \
       find . -name "*.java" > files && \
       javac -cp /usr/share/java/org.OpenNI.jar:/usr/share/java/com.primesense.NITE.jar:/usr/share/java/java-atk-wrapper.jar:/root/hands-gesture-recognezer/lib/Jama-1.0.2.jar @files
