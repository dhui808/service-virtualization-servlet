# Cloud-ready Mobile Banking with Virtualized Services based on Servlet

## Technology Stack
This projects illustrates the usages fo the following technologies:

![Angular](images/webservice-mock-server-springboot.png)



## Full Service Virtualization
All services  are virtualized to support UI layer testing for all business and technical scenarios.

## Dependency
This project depends on my other project [mobileweb-angular-mvc-poc](https://github.com/dhui808/mobileweb-angular-mvc-poc).

## Build
cd webservicemockserver
mvn clean package

## Run locally
./start.sh

## Run locally with Spring Boot
./mvnw clean spring-boot:run

## Run Docker image
docker run -d -p 8080:8080 -p 5005:5005 -t yourdockerid/webservicemockserver

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

Create an application from Docker image docker.io/yourdockerid/webservicemockserver\
oc new-app docker.io/yourdockerid/webservicemockserver --name webservicemockserver

Expose your server at port 8080\
oc expose dc/webservicemockserver --port=8080

Create a route:\
oc expose service/webservicemockserver

Import Docker image:\
oc import-image webservicemockserver

Create ConfigMap for the Spring logging configuration file:\
oc create configmap springbootlogconfig --from-file=/usr/springbootlogging/logback-spring.xml

oc get pods -w -n lab3-product-catalog-dc

oc get route webservicemockserver

From the route from the above command, point your browser at the corresponding URL. A sample browser URL should look like below:

http://webservicemockserver-lab3-product-catalog-dc.apps.na311.openshift.opentlc.com/banking/
 