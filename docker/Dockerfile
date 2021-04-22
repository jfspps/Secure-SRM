FROM centos 
 RUN yum install -y java-11-openjdk-devel 
 VOLUME /tmp
 ADD /SecureSRM-0.0.1-SNAPSHOT.jar secureSRM.jar
 RUN sh -c 'touch /secureSRM.jar'
 ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/secureSRM.jar"]
