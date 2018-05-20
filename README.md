curl -H "Content-Type: application/json"\
     -X POST\
     -d '{"username": "normen.mueller@gmail.com"}'\
     http://localhost:9000/signup
Result:

{
    "type":"Info",
    "subject":"User 'normen.mueller@gmail.com' successfully staged",
    "details":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1MjYzNjg3NDAsImlhdCI6MTUyNjI4MjM0MDE2NCwidXNlcm5hbWUiOiJub3JtZW4ubXVlbGxlckBnbWFpbC5jb20ifQ.utxNj-slLtv7Rvzqq_zXsa2D1lZu2yRbJuWfWSzkav_lsr4JHrCI6ClVm-kkGZu6ldNffd3iUDi_70TNkQ4DAw"
}


curl -H "Content-Type: application/json"\
     -X POST\
     -d '{"forename": "Normen", "surname" : "Müller", "password" : "asdf" }'\
     http://localhost:9000/api/user/eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1MjYzNjg3NDAsImlhdCI6MTUyNjI4MjM0MDE2NCwidXNlcm5hbWUiOiJub3JtZW4ubXVlbGxlckBnbWFpbC5jb20ifQ.utxNj-slLtv7Rvzqq_zXsa2D1lZu2yRbJuWfWSzkav_lsr4JHrCI6ClVm-kkGZu6ldNffd3iUDi_70TNkQ4DAw     
     
{
    "type":"Info",
    "subject":"User 'normen.mueller@gmail.com' successfully registered",
    "details":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1MjYzNjg4NzgsImlhdCI6MTUyNjI4MjQ3ODExNSwidXNlcm5hbWUiOiJub3JtZW4ubXVlbGxlckBnbWFpbC5jb20iLCJmb3JlbmFtZSI6Ik5vcm1lbiIsInN1cm5hbWUiOiJNw7xsbGVyIn0.Fhk06s52MkjI7zvg3vhhP5B0c5greFCCO4jiCN7j05duAfmOC5NoXS5iR7Hepf2zZznhB_TsXOWzFkuwwSL0vg"
  
}     

curl -H "Content-Type: application/json"\
	 -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1MjYzNjg4NzgsImlhdCI6MTUyNjI4MjQ3ODExNSwidXNlcm5hbWUiOiJub3JtZW4ubXVlbGxlckBnbWFpbC5jb20iLCJmb3JlbmFtZSI6Ik5vcm1lbiIsInN1cm5hbWUiOiJNw7xsbGVyIn0.Fhk06s52MkjI7zvg3vhhP5B0c5greFCCO4jiCN7j05duAfmOC5NoXS5iR7Hepf2zZznhB_TsXOWzFkuwwSL0vg"\
     -X GET\
    http://localhost:9000/api/user
    
{
    "type":"Info",
    "subject":"User not found",
    "details":"UserInfo(normen.mueller@gmail.com,Some(Normen),Some(Müller))"}    