# PropertiesImporter

Tool to imports the available Textwerk XML file properties into the specified projects properties

## Getting Started
xml files
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

``git clone https://licdci01.bmwgroup.net:7993/scm/inflib/textwerkpropertiesimporter.git``

### Installing

A step by step series of examples that tell you have to get a development env running

Introduce the dependency in pom.xml

```
<dependency>
	<groupId>com.bmw.cdp.propertiesimporter</groupId>
	<artifactId>propertiesimporter</artifactId>
	<version>1.0.0</version>
    <type>jar</type>
</dependency>

```

## Running the tests

The test will run automatically when a clean install build will be executed.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Usage

To use the propertiesmporter.jar standalone tool you have to download it from nexus repository
with the following maven command in the textwerk directory of the project nbt | portal_bmwlive root:

mvn org.apache.maven.plugins:maven-dependency-plugin:3.0.2:copy -Dartifact=com.bmw.cdp.propertiesimporter:propertiesimporter:1.0.0:jar -DoutputDirectory=.

Groovy:
mvn "org.apache.maven.plugins:maven-dependency-plugin:3.0.2:copy -Dartifact=com.bmw.cdp.propertiesimporter:propertiesimporter:LATEST:jar -DoutputDirectory=."



Examples for use of Propertiesimporter.jar from nbt/textwerk | portal_bmwlive/textwerk root:
```
java -jar propertiesimporter.jar importxml .. report_gen3_bmw.txt CD_BMWOnline_NBT_ALL-BMW.xml NBT BMW
java -jar propertiesimporter.jar importxml .. report_gen2_bmw.txt CD_BMWOnline_COOL_ALL-BMW.xml COOL BMW

java -jar propertiesimporter.jar -function=importxml -rootdir=.. -outputfile=report_gen3_bmw.txt -inputfile=CD_BMWOnline_NBT_ALL-BMW.xml -portal=NBT -brand=BMW
java -jar propertiesimporter.jar -function=importxml -rootdir=.. -outputfile=report_gen2_bmw.txt -inputfile=CD_BMWOnline_COOL_ALL-BMW.xml -portal=COOL -brand=BMW

java -jar propertiesimporter.jar -function=importxml -rootdir=.. -outputfile=report_gen3_bmw.txt -inputfile=CD_BMWOnline_NBT_ALL-BMW.xml -inputlocales=de_DE,en_GB" -brand=BMW
java -jar propertiesimporter.jar -function=importxml -rootdir=.. -outputfile=report_gen2_bmw.txt -inputfile=CD_BMWOnline_COOL_ALL-BMW.xml -inputlocales=de_DE,en_GB" -brand=BMW

java -jar propertiesimporter.jar -function=importxml -rootdir=.. -outputfile=report_gen3_zinoro.txt -inputfile=CD_BMWOnline_NBT_ALL-ZINORO.xml -portal=NBT -brand=ZINORO

java -jar propertiesimporter.jar importexcel .. report_gen3_bmw_xlsx.txt CD_BMWOnline_NBT_ALL-BMW.xlsx NBT BMW
java -jar propertiesimporter.jar importexcel .. report_gen2_bmw_xlsx.txt CD_BMWOnline_COOL_ALL-BMW.xlsx COOL BMW
```
