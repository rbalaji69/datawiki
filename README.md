# datawiki
Tools to extract data from Wikipedia into Wikidata
This repository contains code to extract the introduction section of given Wikipedia articles and looks for the reign parameter in the Infobox to extract
the reign start and end dates of the Monarch represented by the Wikipedia article. 

The extracted reign start and end years are stored in an RDBMS which will be later used by a Bot to update corresponding Wikidata entities with those 
reign start and end years. 
