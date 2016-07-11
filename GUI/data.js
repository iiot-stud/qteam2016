var DATA = {"a" : "b", "c" : "d"};

var RESPONSE = {
  "name": "alex",
  "vendor": "OpenPolicy",
  "version": "0.1",
  "success": "true/false",
  "locations": [
    {
      "name": "Strato",
      "country": "Germany",
      "price": "2€"
    },
    {
      "name": "dsakdkvd",
      "country": "Germany",
      "price": "1€"
    },
    {
      "name": "Blub",
      "country": "Frankreich",
      "price": "1€"
    },
    {
      "name": "2kjd",
      "country": "Russland",
      "price": "2€"
    },

    {
      "name": "2kjd",
      "country": "Indien",
      "price": "2€"
    },

    {
      "name": "2kjd",
      "country": "Island",
      "price": "2€"
    },

    {
      "name": "2kjd",
      "country": "Canada",
      "price": "2€"
    }
]
}


var REQUEST = {
	"name":"alex",
	"vendor":"OpenPolicy",
	"version":"0.1",
	"preferences":["germany"],

"attributes":[
	{
		"#http://www.q-team.org/Ontology#Einwilligung_Zur_Datensammlung":true
	},
	{
		"#http://www.q-team.org/Ontology#minAge":12
	},
	{
		"#http://www.q-team.org/Ontology#Recht_auf_Vergessen":false
	},
	{
		"#http://www.q-team.org/Ontology#Meldepflicht_bei_Verletzung":true
	}
]
}