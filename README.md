# Identity and Access Management

##User Registration 

### 1. Staging
`curl -H "Content-Type: application/json"\
     -X POST\
     -d '{"username": "bunyodreal@gmail.com"}'\
     http://localhost:9000/signup`

#### Result:

`{
    "type":"Info",
    "subject":"User 'bunyodreal@gmail.com' successfully staged",
    "details":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1MjYzNjg3NDAsImlhdCI6MTUyNjI4MjM0MDE2NCwidXNlcm5hbWUiOiJub3JtZW4ubXVlbGxlckBnbWFpbC5jb20ifQ.utxNj-slLtv7Rvzqq_zXsa2D1lZu2yRbJuWfWSzkav_lsr4JHrCI6ClVm-kkGZu6ldNffd3iUDi_70TNkQ4DAw"
}`

### 2. Register
`curl -H "Content-Type: application/json"\
     -X POST\
     -d '{"forename": "Bunyod", "surname" : "Bobojonov", "password" : "asdf" }'\
     http://localhost:9000/api/user/eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1MjYzNjg3NDAsImlhdCI6MTUyNjI4MjM0MDE2NCwidXNlcm5hbWUiOiJub3JtZW4ubXVlbGxlckBnbWFpbC5jb20ifQ.utxNj-slLtv7Rvzqq_zXsa2D1lZu2yRbJuWfWSzkav_lsr4JHrCI6ClVm-kkGZu6ldNffd3iUDi_70TNkQ4DAw`
   
#### Result:
    `{
        "type":"Info",
        "subject":"User 'bunyodreal@gmail.com' successfully registered",
        "details":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1MjYzNjg4NzgsImlhdCI6MTUyNjI4MjQ3ODExNSwidXNlcm5hbWUiOiJub3JtZW4ubXVlbGxlckBnbWFpbC5jb20iLCJmb3JlbmFtZSI6Ik5vcm1lbiIsInN1cm5hbWUiOiJNw7xsbGVyIn0.Fhk06s52MkjI7zvg3vhhP5B0c5greFCCO4jiCN7j05duAfmOC5NoXS5iR7Hepf2zZznhB_TsXOWzFkuwwSL0vg"
      
    }`

### 3. Info
`curl -H "Content-Type: application/json"\
	 -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1MjYzNjg4NzgsImlhdCI6MTUyNjI4MjQ3ODExNSwidXNlcm5hbWUiOiJub3JtZW4ubXVlbGxlckBnbWFpbC5jb20iLCJmb3JlbmFtZSI6Ik5vcm1lbiIsInN1cm5hbWUiOiJNw7xsbGVyIn0.Fhk06s52MkjI7zvg3vhhP5B0c5greFCCO4jiCN7j05duAfmOC5NoXS5iR7Hepf2zZznhB_TsXOWzFkuwwSL0vg"\
     -X GET\
    http://localhost:9000/api/user
`

#### Result:    
`{
    "type":"Info",
    "subject":"User not found",
    "details":"UserInfo(bunyodreal@gmail.com,Some(Bunyod),Some(Bobojonov))"
}`    