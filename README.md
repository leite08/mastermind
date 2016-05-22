# Master Mind Game - API implementation on REST services

This version is only available through REST requisitions using Json as data format.


## Endpoints

* /new_game [POST]
* /guess [POST]


## Requirements

* Java 8
* MySQL (may use another JPA-compatible database, need to update pom.xml - drivers - and application.properties).


## Installation

**Items from 1 to 3 can be skipped if you want to use an existing database, just make sure you update "application.properties" accordingly.**

1. Install MySQL
2. Create a database in MySQL with name "mastermind"
3. Create a user "mastermind" with the password "mastermind"
4. Run the script "src\script\populate.sql" in the database


## Testing

* Itegration tests:
  * Run the class com.rafaelleite.mastermind.MasterMindApplicationTests
* Unit tests:
  * Currently only one class: com.rafaelleite.mastermind.serviceGameServiceTest 
 

## Future

* Improve integration tests
* Improve unit testing 
* Use Https and Authentication?
