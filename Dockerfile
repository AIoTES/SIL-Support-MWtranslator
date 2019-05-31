FROM java:8 

# Install maven
RUN rm /etc/apt/sources.list.d/jessie-backports.list
RUN echo "deb http://deb.debian.org/debian jessie main\ndeb http://security.debian.org jessie/updates main" > /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y maven

WORKDIR /code

# Prepare by downloading dependencies
ADD pom.xml /code/pom.xml
# RUN ["mvn", "dependency:resolve"]
# RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD src /code/src
RUN ["mvn", "clean", "compile", "assembly:single"]

EXPOSE 4568
CMD ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-jar", "/code/target/MWTranslator-0.0.2-SNAPSHOT-jar-with-dependencies.jar"]