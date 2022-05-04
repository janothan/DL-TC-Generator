# DL-TC-Generator
[![Java Build](https://github.com/janothan/DL-TC-Generator/actions/workflows/java_build.yml/badge.svg)](https://github.com/janothan/DL-TC-Generator/actions/workflows/java_build.yml) [![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.6509715.svg)](https://doi.org/10.5281/zenodo.6509715) [![License](https://img.shields.io/github/license/janothan/DL-TC-Generator)](https://github.com/janothan/DL-TC-Generator/blob/master/LICENSE)

This maven project can generate a description logics gold standard to evaluate
knowledge graph embeddings in terms of their ability to learn specific
logical expressions.
Multiple options exist to generate a gold standard.
For the evaluation, [this framework](https://github.com/janothan/dl-evaluation-framework) can be used.

## Gold Standards

### Overview

| Version | Name               | Location                                              | Based On        | Size Classes  | Other Parameters                                                                                                                                         |
|---------|--------------------|-------------------------------------------------------|-----------------|---------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| v1      | dbpedia            | [/results/v1/dbpedia](/results/v1/dbpedia)            | DBpedia 2021-09 | 50, 500, 5000 | -                                                                                                                                                        |
| v1      | synthetic_ontology | [/results/v1/synthetic_ontology](/results/v1/synthetic_ontology) | -               <br/>| 1000          | <ul><li># classes: 760</li> <li># properties: 1355</li> <li>Max triples per node: 11</li> <li>AVG branching factor: 5</li> <li>node factor: 10</li></ul> |



### DBpedia SPARQL Gold Standard
Given multiple SPARQL queries defined in [the query directory](/src/main/resources/queries), this project
generates a gold standard that can be used for machine learning extensions.

The gold standard is intended to be used with a DBpedia embedding.

You can find the current version of the gold standard 
in [the results directory](/results/dbpedia). 

### Synthetic Gold Standard
The DBpedia SPARQL GS (see above) is a real gold standard but it is not perfect. The synthetic gold
standard option allows for generating synthetic "lab-grown" graphs for precisely
evaluating knowledge graph embeddings.

There are three flavors: Random graph generation (very expensive), constructed
graph generation (in easy and hard), and ontology-based graph generation (recommended).

You can find the current version of the constructed gold standards 
[the results directory](/results/).


## Structure of the Results Folder and Terminology

**Test Case Collection**<br/>
The first directory order in the results directory is called *test case collection*
in the implementation. Examples for a test case collection are `tc01`, `tc02`.
A test case collection is a collection of multiple test case groups (see below).
All test cases in a test case collection test for the same DL construct.

**Test Case Group**<br/>
A test case group is a domain-bound group of test cases. Examples for a test case
group are `people` or `movies`. 

Note that the synthetic results only have one test case group (e.g. `synthetic_ontology`).
In the synthetic case, there is additionally a file `graph.nt` (the generated graph) and a directory `dgl-ke-graph`
(the same graph in the [DGL-KE](https://github.com/awslabs/dgl-ke) format).

**Size Group**<br/>
A size is merely the size of the test case; more specifically, the number of positives
and negatives, respectively.

*Example: Structure of the DBpedia Gold Standard Directory*<br/>
```
v1/
|
-- dbpedia/
   | 
   -- tc01/
      |
      -- books/
      |   |
      |   -- 50/ 
      |   |   |
      |   |   -- negatives.txt
      |   |   |
      |   |   -- positives.txt
      |   |   |
      |   |   -- train_test/
      |   |      |
      |   |      -- test.txt
      |   |      |
      |   |     -- train.txt
      |   |
      |    -- 500/
      |       |
      |       -- ...
      |
      -- cities/
         |
         -- ...         
```

*Example: Structure of the Synthetic Gold Standard Directory*<br/>
```
v1/
|
-- synthetic_ontology/
   | 
   -- tc01/
   |   |
   |   -- synthetic_ontology/
   |      |
   |      -- 1000/ 
   |      |   |
   |      |   -- negatives.txt
   |      |   |
   |      |   -- positives.txt
   |      |   |
   |      |   -- train_test/
   |      |      |
   |      |      -- test.txt
   |      |      |
   |      |     -- train.txt
   |      |
   |      |
   |      -- graph.nt
   |      |
   |      -- ontology.nt
   |      |
   |      -- dgl-ke-graph/
   |
   -- tc02/
      |
      -- ...  
```

## Generator Command Line Interface (CLI)
The CLI can be used on to generate a gold standard without editing code in an IDE.
You can call the help menu via `-h`/`--help`.

```
 -a,--analyze
 -b,--branchingFactor <arg>   Only valid for synthetic generators. The
                              parameter specifies the branching factor for
                              the ontology class tree.
 -c,--classes <arg>           Only valid for synthetic generators. The
                              parameter specifies the number of classes to
                              be used.
 -d,--directory <arg>         The test directory that shall be written or
                              analyzed (-a). If the directory shall be
                              written, it must not exist yet.
 -e,--edges <arg>             Only valid for synthetic generators. The
                              parameter specifies the number of edges to
                              be used.
 -h,--help                    Print this help message.
 -m,--maxTriples <arg>        Only valid for synthetic generators. The
                              parameter specifies the maximum number of
                              triples per node.
 -n,--nodeFactor <arg>        Only valid for synthetic generators. The
                              total nodes factor determines the maximum
                              number of nodes in a graph (totalNodesFactor
                              * nodesOfInterest).
 -q,--queries <arg>           The directory where the queries reside. If
                              parameter -q is not specified, the synthetic
                              query module is used.
 -s,--sizes <arg>             The sizes for the test cases, space
                              separated.
 -t,--timeout <arg>           Time out in seconds for queries.
 -tc,--tc_group <arg>         The test case group such as 'cities'; space
                              separated.
 -tcc,--tc_collection <arg>   The test case collection such as 'tc01';
                              space separated.

```

## Development Remarks
- java 17
- maven project
