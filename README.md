# Banker

General information

The defualt campaigns will upload in setup via getCampaigns (bean) function .
getCampaigns function return hashmap of key,value. 
Key is type long that represent id of Campaign
value is type long that represent money of Campaign
The data can change only via DataCampaignsConfig class.
Redis db use in port 6379 and host localhost.
You can change those properties via application properties.

spring.redis.host=localhost
spring.redis.port=6379

Api information :

@PostMapping("/bid")
public Boolean newBid(@RequestBody Bid newBid)

post request get Bid as json object 

like 
{
	"bidId":1,
	"campaignId":3,
	"cost":2
}

this api check if there is enough  money in server memory 
if there is money 
insert bid object to redis and remove money from the server

the second api is to get the answer of the result of bid

@PostMapping("/bid/{id}/{status}")

post request get  bit id and status (WIN,LOSE)
and update accordingly the server budget
and bid status in redis



