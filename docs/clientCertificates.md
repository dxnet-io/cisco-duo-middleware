
# SOURCE (https://fardog.io/blog/2017/12/30/client-side-certificate-authentication-with-nginx/)



## Generate CA

```openssl genrsa -des3 -out ca.key 4096```
* Password: <your_desired_pass>

```openssl req -new -x509 -days 365 -key ca.key -out ca.crt -subj "/C=PT/ST=DXNET/O=DXNET/CN="```
* Password: <your_desired_pass>
* (executar to get new certificate)

## Generate client certificates

```openssl genrsa -des3 -out dxnet.key 4096```
* password: <your_desired_pass>

```openssl req -new -key dxnet.key -out dxnet.csr -subj "/C=PT/ST=DXNET/O=DXNET/CN=localhost"```
* Password: <your_desired_pass>

# sign the csr to a certificate valid for 365 days
```openssl x509 -req -days 359 -in dxnet.csr -CA ca.crt -CAkey ca.key -set_serial 01 -out dxnet.crt```

# Generate pfx
```openssl pkcs12 -export -out dxnet.pfx -inkey dxnet.key -in dxnet.crt -certfile ca.crt```
Export password : blank