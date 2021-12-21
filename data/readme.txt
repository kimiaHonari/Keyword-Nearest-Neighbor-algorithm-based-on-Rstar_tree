
for creating a database in mongodb you can use following command:

biz_subset.json  ---> the data that create from [yelp dataset](https://www.yelp.com/dataset)


mongoimport --jsonArray --db test --collection docs --file biz_subset.json
mongoimport --db projectdb --collection business --type json --file biz_subset.json --jsonArray

mongod --dbpath /usr/local/mongodb-data

db.business.find({"categories":"Restaurants"}).pretty()
Object.bsonsize(db.business.find({"categories":"Restaurants"}))
mongoimport --db projectdb --collection business --type json --file yelp_dataset_challenge_round9.json
db.yel_business.aggregate([{"$group" : {_id:"$categories[0]", count:{$sum:1}}}])

db.yel_business.find({"state":"NC"}).aggregate([{"$group" : {_id:"$categories", count:{$sum:1}}}])
db.yel_business.aggregate([{$group : {_id : "$by_user", first_url : {$first : "$url"}}}])
> db.yel_business.aggregate([{"$group" :{_id:"$city", count:{$sum:1}}},{$sort:{"count":-1}}])
db.yel_business.aggregate([{ "$unwind": "$categories" },{ "$group": {"_id": "$_id","categories": { "$first": "$categories" }}},{ "$group": {"_id": "$categories","count": { "$sum": 1 }}}])
db.yel_business.aggregate([{"$group" :{_id:{state:"$state",city:"$city"}, count:{$sum:1}}},{$sort:{"count":1}}])

db.yel_business.find({$and: [{"city":"Toronto"},{"categories":"Fast Food"}]}).size()
db.yel_business.find().sort({"latitude":1}).limit(1)
db.yel_business.find({city:"Toronto"}).limit(5)
