# RA-SZZ 
---
**RA-SZZ** (*Refactoring Aware - SZZ*) is an improved and open source
implementation of SZZ algorithm initially proposed by Sliwerski, Zimmermann,
and Zeller (2005). RA-SZZ is designed to find bug introducing changes starting
from bug-fix commits.  RA-SZZ can identify refactoring operations on bug-fix
changes to avoid false positives on the generated results.

This project uses other projects as dependencies:
- [RefDiff](https://github.com/aserg-ufmg/RefDiff)
- [DiffJ](https://github.com/jpace/diffj)
- [RefactoringMiner](https://github.com/tsantalis/RefactoringMiner)

---
# Overview

RA-SZZ is composed of five modules (or subprojects), which are listed below:

- connector-core 
- DiffJ
- Refactoring\_Miner\_Adapted
- refdiff-core
- RA-SZZ

## Building RA-SZZ

Currently, RA-SZZ uses Apache Ant as the building tool for its core components.
In order to run RA-SZZ, the components should be build in the following order (assuming you are
at the raszzprime/ directory).

```
$ cd connector-core
$ ant clean
$ ant compile

$ cd ../DiffJ
$ ant clean
$ ant compile

$ cd ../Refactoring_Miner_Adapted
$ ant clean
$ ant compile

$ cd ../refdiff-core
$ ant clean
$ ant compile

$ cd ../RA-SZZ
$ ant clean
$ ant build
```

## Running RA-SZZ

Use the following command to run the RA-SZZ algorithm:

```
java -cp raszzprime/core-connector/build/classes:raszzprime/core-connector/libs/*:raszzprime/DiffJ/build/classes:raszzprime/DiffJ/libs/*:raszzprime/RefactoringMiner_Adapted/build/classes:raszzprime/RefactoringMiner_Adapted/libs/*:raszzprime/refdiff-core/build/classes:raszzprime/refdiff-core/libs/*:raszzprime/RA-SZZ/libs/*:raszzprime/RA-SZZ/build/classes br.ufrn.raszz.miner.szz.RaSZZ
```

Please, note that you need to replace the ':' by ';' on Windows systems.

SZZ will then prompt for the project that it should work on. You must inform one of the 'projectnames' that are available on the 'linkedissuessvn' table.
To check the available projectname on the linkedissuessvn table, you can run the following sql query

```
select distinct(projectname) from linkedissuessvn order by projectname;
```

## Configuring the Database

This repository comes with the 'bkp_raszz_structure.sql', which is a Postgres
backup file. Once you have installed the Postgres database in your computer,
you can load the database backup via the pGAdmin tool that comes along with the
installation.

RA-SZZ will read the bug-fixing hashes per analyzed project from the
'linkedissuessvn' table. For each commit hash that RA-SZZ processes, it will
store the bug-introducing changes into the 'bicraszzgit' table. 

Every time RA-SZZ processes a commit hash, it stores that hash into the
szz_project_lastrevisionprocessed, so that RA-SZZ does not need to process the
commit hash again in case the execution has been halted. Therefore, if it is
intended to reset the results obtained by RA-SZZ to a given project, the data
within the szz_project_lastrevisionprocessed must be deleted.


### hibernate.cfg.xml

RA-SZZ uses the Hibernate framework to interact with its database. Therefore you will
need to configure the hibernate.cfg.xml file that is located within the RA-SZZ/src/ directory. More specifically,
the properties below should be specified.

```
<!-- Database connection settings -->
<property name="connection.driver_class">org.postgresql.Driver</property>
<property name="connection.url">jdbc:postgresql://localhost:5432/mydatabase</property>
<property name="connection.username">postgres</property>
<property name="connection.password">mypassword</property> 
```










