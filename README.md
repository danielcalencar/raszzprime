## RA-SZZ ##
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

RA-SZZ is composed of several modules, which are listed below:

- connector-core 
- DiffJ
- Refactoring\_Miner\_Adapted
- refdiff-core

## Building RA-SZZ

Currently, RA-SZZ uses Apache Ant as the building tool for its core components.
In order to run RA-SZZ the components should be build in the following order (assuming you are
at the raszzprime/ directory).

```
$ cd connector-core/
$ ant compile

$ cd ../DiffJ/
$ ant compile

$ cd ../Refactoring_Miner_Adapted
$ ant compile

$ cd ../refdiff-core/
$ ant compile

$ cd ../RA-SZZ/
$ ant build
```

## Running RA-SZZ

Use the following command to run the RA-SZZ algorithm:

```
java -cp raszzprime/core-connector/build/classes:raszzprime/core-connector/libs/*:raszzprime/DiffJ/build/classes:raszzprime/DiffJ/libs/*:raszzprime/RefactoringMiner_Adapted/build/classes:raszzprime/RefactoringMiner_Adapted/libs/*:raszzprime/refdiff-core/build/classes:raszzprime/refdiff-core/libs/*:raszzprime/RA-SZZ/libs/*:raszzprime/RA-SZZ/build/classes br.ufrn.raszz.miner.szz.RaSZZ
```

Please, note that you need to change the ':' to ';' on Windows systems.




