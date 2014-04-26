Schulze
=
Aggregates preferences via the schulze method voting system (a condorcet method)

Setup
=
- Fix clojure zip-within-zip bug
- Install Datomic
- Launch Datomic
- Generate schulze server one-jar
- Launch schulze server one-jar

Fix clojure zip-within-zip bug
=
Clojure is needed because we are depending on datomic.  
Clojure has a bug which prevents the clojure runtime from loading classes that are in nested zip files.  
This will matter if you are distributing the code via the maven-onejar-plugin.  
There is some discussion about this issue at [CLJ-971](http://dev.clojure.org/jira/browse/CLJ-971).  
Until this is fixed, you will have to fix the clojure runtime yourself.  
Instructions to do this can be found at [clojure-one-jar](https://github.com/SeanShubin/clojure-one-jar).  

Install datomic
=
Get datomic from [get-datomic](http://www.datomic.com/get-datomic.html) and follow the installation instructions  
In the datomic directory, run bin/maven-install  
Edit the pom.xml file so that the datomic version matches, for example:  

    <dependency>
        <groupId>com.datomic</groupId>
        <artifactId>datomic-free</artifactId>
        <version>0.9.4755</version>
    </dependency>

Launch Datomic
=
In the datomic directory run

    bin/transactor config/samples/free-transactor-template.properties

Generate schulze server one-jar
=
from the schulze project directory, run

    mvn clean package

Launch schulze server one-jar
=
The parameters to schulze-server.jar are as follows

1. The port for the schulze voter application
2. The datomic uri

For example, if you want to use port 4000 and the datomic uri datomic:free://localhost:4334/schulze

    java -jar server/target/schulze-server.jar 4000 datomic:free://localhost:4334/schulze

Once the schulze application is launched, try it out by navigating to

    http://localhost:4000
    http://localhost:4000/schulze/introduction.txt

Familiarize yourself with the API
=
To understand the API, start with com.seanshubin.schulze.server.SchulzeHandler
