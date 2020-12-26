# Service Virtualization with Servlet

## Technology Stack
This projects illustrates the usages fo the following technologies:

![Angular](images/webservice-mock-server-springboot.png)



## Full Service Virtualization
This application provides the service virtualization to test the mobile web banking UI projects:
[mobileweb-angular-mvc-poc](https://github.com/dhui808/mobileweb-angular-mvc-poc)
and [mobileweb-angular-redux-poc](https://github.com/dhui808/mobileweb-angular-redux-poc).

The design of of this tool is completely different from any of the Service Virtualization tools currently 
available in the market. It allows the user to test multiple scenarios for the same transaction without 
the need for the user to configure the services or use any specific input data. The user simply selects 
the scenario he or she wants to test first, then proceed to test it.  This approach simple and intuitive.
It greatly increases the developer productivity and is invaluable for quality assurance.

## Dependency

This project depends on my two other projects:

[service virtualization UI application](https://github.com/dhui808/service-virtualization-ui)

[service virtualization data](https://github.com/dhui808/service-virtualization-data)

## Application Configuration
There are two configuration files: application.properties and logback-spring.xml, under resources folder.

The application.properties looks like below:

servicevirtualizationdata_home=/usr/service-virtualization-data/servicevirtualizationdata\
server.servlet.context-path=/banking\
logging.config=file:/usr/springbootlogging/logback-spring.xml\
configpath=/config

The logback-spring.xml must be copied to the location as specified by logging.config property in application.properties.

The content of project webservicemockdata must be copied to the location as specified by webservicemockdata.home property
in application.properties.

## Build
cd servicevirtualizationservlet

mvn clean install

## Build Docker image
mvn clean install -Pdocker

## Push the image to Docker Hub registry
mvn deploy -Pdocker

## Start server from command line - Windows
start.cmd

## Start server from command line - Unix/Linux
./start.sh

## Run Docker image with Fabric8
mvn install -Pfabric8

## Run Docker image with Docker directly
docker run -d -p 8080:8080 -p 5005:5005 -t dannyhui/servicevirtualizationservlet

## Stop and kill Docker container
docker ps\
(Look for the container ID)

dcoker stop <container_id>\
docker rm <container_id>

## Deploy to OpenShift
Log in to your OpenShift account with the proper server url, username and passowrd:\
oc login --insecure-skip-tls-verify --server=https://master.na311.openshift.opentlc.com:443 

Create OpenShift project myproject\
oc new-project myproject

Create an application from Docker image docker.io/yourdockerid/servicevirtualizationservlet\
oc new-app docker.io/yourdockerid/servicevirtualizationservlet --name servicevirtualizationservlet

Expose your server at port 8080\
oc expose dc/servicevirtualizationservlet --port=8080

Create a route:\
oc expose service/servicevirtualizationservlet

Import Docker image:\
oc import-image servicevirtualizationservlet

Create ConfigMap for the Spring logging configuration file:\
oc create configmap springbootlogconfig --from-file=/usr/springbootlogging/logback-spring.xml

oc get pods -w -n lab3-product-catalog-dc

oc get route servicevirtualizationservlet

From the route from the above command, point your browser at the corresponding URL. A sample browser URL should look like below:

http://servicevirtualizationservlet-lab3-product-catalog-dc.apps.na311.openshift.opentlc.com/banking/
 