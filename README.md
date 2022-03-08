# DL-TC-Generator

This maven project can generate a description logics gold standard to evaluate
knowledge graph embeddings in terms of their ability to learn specific
logical expressions.
Multiple options exist to generate a gold standard.
For the evaluation, [this framework](https://github.com/janothan/dl-evaluation-framework) can be used.

## Gold Standards

### DBpedia SPARQL Gold Standard
Given multiple SPARQL queries defined in [the query directory](/src/main/resources/queries), this project
generates a gold standard that can be used for machine learning extensions.

The gold standard is intended to be used with a DBpedia embedding.

You can find the current version of the gold standard 
in [the results directory](/results/dbpedia). 

### Synthetic Gold Standard
DBpedia SPARQL GS (see above) is a real gold standard but it is not perfect. The synthetic gold
standard option allows for generating synthetic "lab-grown" graphs for precisely
evaluating knowledge graph embeddings.

There are two flavors: Random graph generation (very expensive) and constructed
graph generation.

You can find the current version of the constructed gold standard 
[the results directory](/results/synthetic_constructed).


## Structure of the Results Folder and Terminology

**Test Case Collection**<br/>
The first directory order in the results directory is called *test case collection*
in the implementation. Examples for a test case collection are `tc01`, `tc02`.
A test case collection is a collection of multiple test case groups (see below).
All test cases in a test case collection test for the same DL construct.

**Test Case Group**<br/>
A test case group is a domain-bound group of test cases. Examples for a test case
group are `people` or `movies`. Note that the synthetic results only have
one test case group (e.g. `synthetic_constructed`).

**Size Group**<br/>
A size is merely the size of the test case; more specifically, the number of positives
and negatives, respectively.


## Development Remarks
- java 17
- maven project
