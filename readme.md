# Description
This project is a middleware that interacts with cisco duo api in order to authorize accesses. 
It exposes an api that can be used multi tenant with several cisco duo accounts.
Tere are several use cases. The one that we used was to make this api available to buildings physycal access mananagement software buildings has a 2 factor auth. A person with a card, fingerprint or facial recognition, must then accept the cisco push notification, to confirm his presence and open the door/access.

# Technical
The application is witten in Java 11 with springboot framework.
All the configurations are based on property files and can be easily shifted to env vars.

# To run this application:
* create a cisco duo free account ```https://signup.duo.com/```
* create a folder ```customers```
* create a file named ```<customer_domain>.properties``` with your Cisco Duo keys
``` api.host=api-<your cisco duo id>.duosecurity.com
api.key=<your cisco duo key>
api.secret=<your cisco duo secret>
```
* edit the application,properties property to point to your custoemr folder:
```property.path=<your_path>\customers```
* To run the app:
   * mvn clean install && java -jar target/ciscoduo-0.0.1-SNAPSHOT.jar 

# New customer/domain
* To add a new customer/domain just create a new file in folder ```customers` with extension ```<new_customer domain>.properties```

# Secure your endpoint
* To secure your endpoint in a production environment we strong recommned to use client certificates. They work like a pair public/private key.
* To configure client certificates with a nginx in front of the springboot tomcate, check ssl\nginx.conf
* More info on how to setup in docs\clientCertificates.md

# Licensing
All code is licenced under GPLV3 ```https://www.gnu.org/licenses/gpl-3.0.en.html```

# API documentation
* You can find api documentation in docs\dxnet-api-doc-v1.md