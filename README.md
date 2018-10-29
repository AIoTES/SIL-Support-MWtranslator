# mwtranslator

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

