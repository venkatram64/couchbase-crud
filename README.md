couchbase spring boot crud example


post request

http://localhost:8090/employee/v1

{
"firstName": "John",
"lastName": "Doe",
"email": "doe.john@doe.com",
"address": [
"123 Main St, Anytown, USA",
"456 Secondary Ave, Othertown, USA"
]
}



get request

http://localhost:8090/employee/v1

put request

http://localhost:8090/employee/v1/3ec8d9e9-9a53-4f36-bb78-57c95676e275


{
"firstName": "John",
"lastName": "Doe",
"email": "john.doe@john.com",
"address": [
"123 Main St, Anytown, USA",
"456 Secondary Ave, Othertown, USA"
]
}



