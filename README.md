# Banker

so defualt Campaigns will upload in setup via getCampaigns (bean) function .
getCampaigns return hashmap of key,value  
key is type long that represent id of Campaign
value is type long that represent money of Campaign

the data can change only via DataCampaignsConfig class

redis db use in port 6379 and host localhost

can change via application property

spring.redis.host=localhost
spring.redis.port=6379

like we say there is two api :

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



