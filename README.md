Make custom content provider. 

In this app we  make use of a csv file 'english-dict.csv' which contains some English words with 
definitions added in assets folder, we will loop over and insert all these words into a 
local database and expose an API for this local database for our other app to interact with that.

In this project we make use of Room for database and 'org.apache.commons:commons-csv' dependency to
load csv.

