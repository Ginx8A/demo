## Getting Started

```bash
# requires
java 11
SpringBoot 2.5.14
Gradle 7.0
```

## Development

### Install dependencies

```batch
gradle clean build
```

### Configure environment vars

```batch
# Secret in HmacSHA512
JWT_SECRET=G1.8A
JWT_EXPIRATION_TIME=300000
```

```batch
# one line
JWT_SECRET=G1.8A;JWT_EXPIRATION_TIME=300000
```


## API Reference

[POST SignUp](#post-signup)

- [Method Reference](#http-method-and-url)
- [Diagrams](#diagrams)

[POST Login](#post-login)

- [Method Reference](#http-method-and-url-1)
- [Diagrams](#diagrams-1)

[Postman Collection](#postman-collection)

---

### POST SignUp

Create and save a new User in ddbb.

#### HTTP Method and URL

`POST {{endpoint}}/sign-up`

#### Body Parameters

> Json

```json
{
  "name": "",
  "email": "",
  "password": "",
  "phones": [
    {
      "number": 11,
      "citycode": 11,
      "contrycode": ""
    }
  ]
}
```

 Element        | Type   | Description   | Required? 
----------------|--------|---------------|:---------:
 name           | String | User Name     |
 email          | String | User Email    |   true    
 password       | String | User Password |   true    
 phones         | Array  |               |
 \> number      | Long   |               |
 \> citycode    | int    |               |
 \> countrycode | String |               |

#### Example Request

> Object

```json
{
  "name": "Claudia",
  "email": "claudia@ginx8a.com",
  "password": "a38Azzzzz",
  "phones": [
    {
      "number": 11111111,
      "citycode": 111,
      "contrycode": "AR"
    }
  ]
}
```

#### Example Response

> Json

```json

{
  "id": "e5c6cf84-8860-4c00-91cd-22d3be28904e",
  "created": "Nov 16, 2021 12:51:43 PM",
  "lastLogin": "Nov 16, 2021 12:51:43 PM",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWxpb0B0ZXN0...",
  "isActive": true
}
```

 Element   | Type    | Description                                           
-----------|---------|-------------------------------------------------------
 id        | String  | User UUID                                             
 created   | String  | LocalDateTime - "MMM d, yyyy h:mm:ss a" format String 
 lastLogin | String  | LocalDateTime - "MMM d, yyyy h:mm:ss a" format String 
 token     | String  | JSON Web Token                                        
 isActive  | boolean | is an user active                                     

#### Error and Status Codes

 Code | Message                 | Meaning               
------|-------------------------|-----------------------
 201  | "Created"               | Created               
 400  | "Bad Request"           | Error on validations  
 500  | "Internal Server Error" | Internal Server Error 

---

### Diagrams

#### Component Diagram

![](/componentDiagram.jpg)


#### Sequence Diagram

![](/sequenceDiagram.jpg)

---

### POST Login

Login user and reload token

#### HTTP Method and URL

`POST {{endpoint}}/login`

#### Body Parameters

> N/A

#### Example Request

> N/A

#### Example Response

> Json

```json
{
	"id": "e5c6cf84-8860-4c00-91cd-22d3be28904e",
	"created": "Nov 16, 2021 12:51:43 PM",
	"lastLogin": "Nov 16, 2021 12:51:43 PM",
	"token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWxpb0B0ZXN0...",
	"isActive": true,
	"name": "Julio Gonzalez",
	"email": "julio@testssw.cl",
	"password": "a2asfGfdfdf4",
	"phones": [
	 {
		"number": 87650009,
		"citycode": 7,
		"contrycode": "25"
	 }
	]
}
```

 Element        | Type    | Description                                           
----------------|---------|-------------------------------------------------------
 id             | String  | User UUID                                             
 created        | String  | LocalDateTime - "MMM d, yyyy h:mm:ss a" format String 
 lastLogin      | String  | LocalDateTime - "MMM d, yyyy h:mm:ss a" format String 
 token          | String  | JSON Web Token                                        
 name           | String  | User Name                                             |
 email          | String  | User Email                                            |
 password       | String  | User Password                                         |
 phones         | Array   |                                                       |
 \> number      | Long    |                                                       |
 \> citycode    | int     |                                                       |
 \> countrycode | String  |                                                       |
 isActive       | boolean | is an user active                                     

#### Error and Status Codes

 Code | Message                 | Meaning               
------|-------------------------|-----------------------
 200  | "Seccess"               | Successful login      
 400  | "Bad Request"           | User unavailable      
 500  | "Internal Server Error" | Internal Server Error 

---

### Diagrams

#### Component Diagram

![](/componentDiagram.jpg)

#### Sequence Diagram

![](/sequenceDiagram.jpg)

---

### Postman Collection

./docs/Demo.postman_collection.json


---

⭐️ From [Ginx8A](https://github.com/Ginx8A)

---