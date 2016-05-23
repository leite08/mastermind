# Mastermind Game
## API implementation on REST services

This version is only available through REST requisitions using Json as data format.

It is implemented in Java with Spring Boot. The application server is integrated within the application.


## Endpoints

* /new_game [POST]
* /guess [POST]


## Requirements

* Java 8
* MySQL (may use another JPA-compatible database, need to update pom.xml - drivers - and application.properties).


## Installation

*Items from 1 to 3 can be skipped if you want to use an existing database, just make sure you update "application.properties" accordingly.*

1. Install MySQL
2. Create a database in MySQL with name "mastermind"
3. Create a user "mastermind" with the password "mastermind"
4. Run the script "src/script/populate.sql" in the database
5. Build and package the application:
  * $mastermind_home/> mvn install  
6. Run the application and server:
  * $mastermind_home/> java -jar target/mastermind-*.jar
7. Access the endpoint "/new_game" to start playing:
  * http://localhost:8080/new_game

The implementation is based in this documentation: http://careers.axiomzen.co/challenge


## Testing

* Itegration tests:
  * Run the class com.rafaelleite.mastermind.MasterMindApplicationTests
* Unit tests:
  * Currently only one class: com.rafaelleite.mastermind.serviceGameServiceTest 
 

## Future

* Functional
  * Control the number of turns
* Non-functional
  * Improve integration tests
  * Improve unit testing 
  * Use Https and Authentication?
  * Add Internationalization (I18N)
