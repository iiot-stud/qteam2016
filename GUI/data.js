var DATA = {"a" : "b", "c" : "d"};

var RESPONSE = {"vendor":"OpenPolicy","name":"alex","locations":[{"continent":"Europe","country":"Germany","price":5,"latitude":"48.105964","name":"Slot_ger_4","longitude":"11.612549"},{"continent":"Europe","country":"Germany","price":6,"latitude":"52.473412","name":"Slot_ger_1","longitude":"13.390961"},{"continent":"Europe","country":"Germany","price":5,"latitude":"48.105964","name":"Slot_ger_2","longitude":"11.612549"},{"continent":"Europe","country":"Germany","price":4,"latitude":"52.473412","name":"Slot_ger_3","longitude":"13.390961"}],"version":"0.1"}



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