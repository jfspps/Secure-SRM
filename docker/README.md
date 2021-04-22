# Instructions

With Dockerfile and the JAR (typically found in /target) in the same directory, build the CentOS based image with

```bash
docker build -t secure-srm-docker .
```

Don't miss out the period above! Then run from the console (add -d for daemon) with

```bash
docker run -p 5000:5000 secure-srm-docker
```

The host port (you) is the first port number. Secure-SRM is set to publish from port 5000 (the second port number). The current build is set to use the in-memory H2 database and hence MySQL is not required at this time.
