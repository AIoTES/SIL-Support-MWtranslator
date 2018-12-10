# mwtranslator


# JVM


mvn clean compile assembly:single


java -jar target\MWTranslator-0.0.1-SNAPSHOT-jar-with-dependencies.jar {TCP port}


# Docker


docker build -t giantswarm/sparkexample .

docker run -d -p 4568:4568 giantswarm/sparkexample

curl -X POST \
  http://localhost:4568/translate/fiware \
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

* POST http://localhost:4568/translate/fiware

* POST http://localhost:4568/translate/sofia

* POST http://localhost:4568/translate/universaal



TRANSLATION FROM INTER-IOT FORMAT:

* POST http://localhost:4568/formatx/fiware

* POST http://localhost:4568/formatx/sofia

* POST http://localhost:4568/formatx/universaal
