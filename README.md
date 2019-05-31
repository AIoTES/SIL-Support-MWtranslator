# mwtranslator


# JVM


mvn clean compile assembly:single


java -jar target\MWTranslator-0.0.2-SNAPSHOT-jar-with-dependencies.jar {TCP port}


# Docker


docker build -t docker-activage.satrd.es/syntactic-translator:0.0.1 .

docker run -d -p 4568:4568 --name syntactic-translator docker-activage.satrd.es/syntactic-translator:0.0.1

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
