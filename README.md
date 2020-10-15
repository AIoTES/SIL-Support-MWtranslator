# mwtranslator

Syntactic translation web service for FIWARE, SOFIA2 and universAAL. Data conversion between the specific format of the selected platform and the common JSON-LD format of AIoTES.


This component is used by the Data Lake to perform the syntactic conversion of the data obtained from the data retrieval web services.


## Getting started

This component is deployed as part of the Data Lake stack. The syntactic translation functions are called automatically by the [Data Lake Query Execution service](https://github.com/AIoTES/DataLayer-DataLake-QueryExecution) if the proper platform type has been specified in the [service registry prototype](https://github.com/AIoTES/DataLayer-DataLake-QueryExecution/wiki/Service-Registry-prototype).


### API
TRANSLATION TO AIoTES JSON-LD FORMAT:

* POST http://localhost:4568/fiware/translate

* POST http://localhost:4568/sofia/translate

* POST http://localhost:4568/universaal/translate



TRANSLATION FROM AIoTES JSON-LD FORMAT:

* POST http://localhost:4568/fiware/formatx

* POST http://localhost:4568/sofia/formatx

* POST http://localhost:4568/universaal/formatx



GET ASSOCIATED PLATFORM TYPES:

* GET http://localhost:4568/fiware/type

* GET http://localhost:4568/sofia/type

* GET http://localhost:4568/universaal/type


Example (translation from FIWARE format to AIoTES JSON-LD):

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


## Build from sources

Build docker image:

`docker build -t aiotesdocker/sil-support-mwtranslator:<version> .`



## Testing

Build using Maven:

`mvn clean compile assembly:single`


Run in JVM:

`java -jar target\MWTranslator-0.0.3-SNAPSHOT-jar-with-dependencies.jar {TCP port}`


You can run locally the Docker image using:

`docker run -d -p 4568:4568 --name syntactic-translator aiotesdocker/sil-support-mwtranslator:<version>`


Default TCP port for the REST API: 4568


## Further information

[Available Docker images](https://hub.docker.com/r/aiotesdocker/sil-support-mwtranslator)


## License
The syntactic translation web service is licensed under [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).
