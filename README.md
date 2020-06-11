# mwtranslator

Syntactic translation web service for FIWARE, SOFIA2 and universAAL. Data conversion between the specific format of the selected platform and the common JSON-LD format of AIoTES.


This component is used by the Data Lake to perform the syntactic conversion of the data obtained from the data retrieval web services.


# API
TRANSLATION TO INTER-IOT FORMAT:

* POST http://localhost:4568/fiware/translate

* POST http://localhost:4568/sofia/translate

* POST http://localhost:4568/universaal/translate



TRANSLATION FROM INTER-IOT FORMAT:

* POST http://localhost:4568/fiware/formatx

* POST http://localhost:4568/sofia/formatx

* POST http://localhost:4568/universaal/formatx



GET ASSOCIATED PLATFORM TYPES:

* GET http://localhost:4568/fiware/type

* GET http://localhost:4568/sofia/type

* GET http://localhost:4568/universaal/type


# Build from sources
## JVM

Build using Maven:

`mvn clean compile assembly:single`


Run in JVM:

`java -jar target\MWTranslator-0.0.3-SNAPSHOT-jar-with-dependencies.jar {TCP port}`


## Docker
Build docker image:

`docker build -t docker-activage.satrd.es/syntactic-translator:<version> .`


Run in Docker:

`docker run -d -p 4568:4568 --name syntactic-translator docker-activage.satrd.es/syntactic-translator:<version>`

```
curl -X POST \
  http://localhost:4568/fiware/translate \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: d220cf92-f012-9b8e-5fa4-97d826e2db5a' \
  -d '{
    "id": "Room1",
    "pressure": {
        "metadata": {},
        "type": "Integer",
        "value": 720
    },
    "temperature": {
        "metadata": {},
        "type": "Float",
        "value": 23
    },
    "type": "Room"
}
'
```


# Versions
* 0.0.1: single message translation
* 0.0.2: array or single message translation

