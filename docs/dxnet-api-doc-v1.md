# DXNet API to interact with CiscoDuo
* Requests a Multi Factor Authentication to Cisco Duo for the customer username who tries to open a door

## Request
* Synchronous request with a 60 sec timeout

```curl   --key dxnet.key   --cert dxnet.crt   --cacert ca.crt --resolve 'localhost:443:127.0.0.1' -X POST    https://<your_ip>:8080/ciscoduo/dxnet.io/access --data-raw '{"username": "miguelespiga"}' --header 'Content-Type: application/json'  --insecure```


## Responses:

### HTTP CODE 200 "Login Ok"
* This response will trigger opening door 

`{
    "response": {
        "result": "allow",
        "status": "allow",
        "status_msg": "Success. Logging you in..."
    },
    "stat": "OK"
}`


### HTTP CODE 200 "Login Denied"

`{
    "response": {
        "result": "deny",
        "status": "deny",
        "status_msg": "Login request denied."
    },
    "stat": "OK"
}`

### HTTP CODE 200 "Login Timeout"

`{
    "response": {
        "result": "deny",
        "status": "timeout",
        "status_msg": "Login timed out."
    },
    "stat": "OK"
}`

### HTTP CODE 400 Bad request "username inválido"

`{
    "code": 40002,
    "message": "Invalid request parameters",
    "message_detail": "username",
    "stat": "FAIL"
}`

### HTTP CODE 400 Bad request "Missing or empty username input"

`{
    "username": null,
    "uuid": null,
    "doorId": null,
    "domain": null,
    "message": "Username field in body is mandatory"
}`

### HTTP 401 Bad Timestamp

`{
	"code": 40105, 
	"message": "Bad request timestamp", 
	"stat": "FAIL"
}`

### HTTP CODE 401 Invalid Credentials

`{
    "code": 40103,
    "message": "Invalid signature in request credentials",
    "stat": "FAIL"
}`


### HTTP CODE 404 Domain not found "Missing or empty domain input"
{
    "timestamp": "2020-06-09T14:02:41.443+00:00",
    "status": 404,
    "error": "Not Found",
    "message": "",
    "path": "/ciscoduo//access/"
}

### HTTP CODE 404 Domain not found "Domain not configured"

`{
    "username": null,
    "uuid": null,
    "doorId": null,
    "domain": null,
    "message": "Domain is not configured"
}`

### HTTP CODE 500 Erro de Sistema

{
    "timestamp": "2020-06-09T12:05:59.003+00:00",
    "status": 500,
    "error": "Internal Server Error",
    "message": "",
    "path": "/ciscoduo/gmail.com/access/"
}

© Copyrights to DXNet Lda, 2021
