SALARY Web applicatin for multiple users like agency or firms.

Create empty database named 'binplate' in MySQL server
MySQL server username : root
			 password: bin123
			 
If you have MySQL installed with different credentials can update MySQL credentials			 
in file src/main/resources/application.properties

populate database as following in CMD:
>mysql -uroot -pbin123 binplate<database.sql

Create project in CMD with Maven:

You must positioning in root folder of project
>mvn clean package

Run project:
>java -jar target/plate-0.0.1-SNAPSHOT.jar

You can rename .jar file as you wish.
Run any browser and go to at http://localhost:8080/
In login page type      user: admin
					password: admin
					
Go to in administration section http://localhost:8080/admin
In this section can make or delete users

This Spring application have basic spring security.
Every user can make their own firms and can working with it separately.

This application can Import in Eclipse STS IDE with Import like existing maven project.
