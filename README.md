
# Backend Identity Reconciliation

***
**Note: Check the [considerations](#considerationfuture-work) for my thoughts**

We have a contacts table in with following info : 

```TypeScript
{
    id                   Int
    phoneNumber          String?
    email                String?
    linkedId             Int? // the ID of another Contact linked to this one
    linkPrecedence       "secondary"|"primary" // "primary" if it's the first Contact in the link
    createdAt            DateTime
    updatedAt            DateTime
    deletedAt            DateTime?
}
```


One customer can have multiple Contact rows in the database against them. All of the rows are linked together with the oldest one being treated as `primary` and the rest as `secondary`. 

Contact rows are linked if they have either of `email` or `phone` as common.

### For example:

If a customer placed an order with
`email=lorraine@hillvalley.edu` & `phoneNumber=123456`
and later came back to place another order with
`email=mcfly@hillvalley.edu` & `phoneNumber=123456`,
database will have the following rows:

```JavaScript
{
    id                   1                   
    phoneNumber          "123456"
    email                "lorraine@hillvalley.edu"
    linkedId             null
    linkPrecedence       "primary"
    createdAt            2023-04-01 00:00:00.374+00              
    updatedAt            2023-04-01 00:00:00.374+00              
    deletedAt            null
},
{
    id                   23                     
    phoneNumber          "123456"   
    email                "mcfly@hillvalley.edu" 
    linkedId             1  
    linkPrecedence       "secondary"    
    createdAt            2023-04-20 05:30:00.11+00                  
    updatedAt            2023-04-20 05:30:00.11+00                  
    deletedAt            null
}
```

## Requirements

You are required to design a web service with an endpoint /identify that will receive HTTP POST requests with JSON body of the following format:

```TypeScript
{
    "email"?: string,
    "phoneNumber"?: number
}
```

The web service should return an HTTP 200 response with a JSON payload containing the consolidated contact.

Your response should be in this format:

```JSON
{
    "contact": {
        "primaryContactId": 0, // Number
        "emails": [], // String Array : first element being email of primary contact 
        "phoneNumbers": [], // String Array : first element being phoneNumber of primary contact
        "secondaryContactIds": [] // Number Array : Array of all Contact IDs that are "secondary" to the primary contact
    }
}
```

### But what happens if there are no existing **contacts** against an incoming request?

The service will simply create a new **`Contact`** row with `linkPrecedence=”primary"` treating it as a new customer and return it with an empty array for `secondaryContactIds`

### When is a secondary contact created?

If an incoming request has either of `phoneNumber` or `email` common to an existing contact but contains new information, the service will create a “secondary” **`Contact`** row.

**Example:**

**Existing state of database:**

```JavaScript
{
    id                   1                   
    phoneNumber          "123456"
    email                "lorraine@hillvalley.edu"
    linkedId             null
    linkPrecedence       "primary"
    createdAt            2023-04-01 00:00:00.374+00              
    updatedAt            2023-04-01 00:00:00.374+00              
    deletedAt            null
}
```

**Identify Request:**

```JavaScript
{
    "email":"mcfly@hillvalley.edu",
    "phoneNumber":"123456"
}
```

**New state of Database**:
```JavaScript
{
    id                   1                   
    phoneNumber          "123456"
    email                "lorraine@hillvalley.edu"
    linkedId             null
    linkPrecedence       "primary"
    createdAt            2023-04-01 00:00:00.374+00              
    updatedAt            2023-04-01 00:00:00.374+00              
    deletedAt            null
},
{
    id                   23                   
    phoneNumber          "123456"
    email                "mcfly@hillvalley.edu"
    linkedId             1
    linkPrecedence       "secondary"
    createdAt            2023-04-20 05:30:00.11+00              
    updatedAt            2023-04-20 05:30:00.11+00              
    deletedAt            null
},
```

### Can primary contacts turn into secondary?

Yes. Let’s take an example

**Existing state of database:**
```JavaScript
{
    id                   11                   
    phoneNumber          "919191"
    email                "george@hillvalley.edu"
    linkedId             null
    linkPrecedence       "primary"
    createdAt            2023-04-11 00:00:00.374+00              
    updatedAt            2023-04-11 00:00:00.374+00              
    deletedAt            null
},
{
    id                   27                   
    phoneNumber          "717171"
    email                "biffsucks@hillvalley.edu"
    linkedId             null
    linkPrecedence       "primary"
    createdAt            2023-04-21 05:30:00.11+00              
    updatedAt            2023-04-21 05:30:00.11+00              
    deletedAt            null
}
```

**Request:**
```JavaScript
{
    "email":"george@hillvalley.edu",
    "phoneNumber": "717171"
}
```

**New state of database:**
```JavaScript
{
	id                   11                   
	phoneNumber          "919191"
	email                "george@hillvalley.edu"
	linkedId             null
	linkPrecedence       "primary"
	createdAt            2023-04-11 00:00:00.374+00              
	updatedAt            2023-04-11 00:00:00.374+00              
	deletedAt            null
},
{
	id                   27                   
	phoneNumber          "717171"
	email                "biffsucks@hillvalley.edu"
	linkedId             11
	linkPrecedence       "secondary"
	createdAt            2023-04-21 05:30:00.11+00              
	updatedAt            2023-04-28 06:40:00.23+00              
	deletedAt            null
}
```

**Response**:
```JSON
{
  "contact":{
    "primaryContactId": 11, 
    "emails": ["george@hillvalley.edu","biffsucks@hillvalley.edu"]
    "phoneNumbers": ["919191","717171"],
    "secondaryContactIds": [27]
  }
}
```


## Consideration/Future Work

I did not knew the scale of this application, So I have not added the optimization but we can look for them. 

1. Removal of `linkPrecedence` column from schema. I did not find any usecase for the linkPrecedence column so I think we should remove and even if we want it we can change it to a calculated column with predicate that `linkedId == NULL ? 'primary' : 'secondary'`.
2. Index Creation : We can create index [(Phone, Email), (Email), (LinkedId)] but I did not knew the read/write ratio so I just added a Unique key on (Phone, Email) for correctness.
3. I also think, If reads were less we could even remove this updation all-together or use precomputation but that is a long shot and I don't think that is needed for now. 