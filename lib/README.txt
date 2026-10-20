Place the MySQL Connector/J JAR in this folder.

Example file name:
mysql-connector-j-9.x.x.jar

Compile from the project root:
javac -cp "lib/*" -d out src/vrms/*.java

Run on Windows:
java -cp "out;lib/*" vrms.Main
