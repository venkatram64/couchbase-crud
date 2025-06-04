step 1: install couchbase server from

https://www.couchbase.com/downloads?family=couchbase-server



step 2: start couchbase server

step 3: access the couchbase server

http://localhost:8090


step 4: create a bucket with name mycompany

step 5: run the application

post :

http://localhost:8090/employee/v1

{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "address": {
        "street": "123 Main St",
        "city": "Anytown",
        "state": "USA",
        "zipCode": "12345"
    },
    "phones": [
        {
        "type": "home",
        "number": "123-456-7890"
        },
        {
        "type": "work",
        "number": "987-654-3210"
        }
    ]
}


Get:
http://localhost:8090/employee/v1


[
    {
    "id": "3ec8d9e9-9a53-4f36-bb78-57c95676e275",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "address": {
    "street": "123 Main St",
    "city": "Anytown",
    "state": "USA",
    "zipCode": "12345"
    },
    "phones": [
        {
        "type": "home",
        "number": "123-456-7890"
        },
        {
        "type": "work",
        "number": "987-654-3210"
        }
    ]
    }
]



Put:

http://localhost:8090/employee/v1/3ec8d9e9-9a53-4f36-bb78-57c95676e275

{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "address": {
        "street": "123 Main St",
        "city": "Anytown",
        "state": "USA",
        "zipCode": "12345"
    },
    "phones": [
        {
        "type": "home",
        "number": "123-456-7890"
        },
        {
        "type": "work",
        "number": "987-654-3210"
        }
    ]
}


