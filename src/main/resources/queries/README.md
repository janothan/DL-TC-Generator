# Directory 'test-cases'
This directory is parsed automatically. Therefore, some boundary conditions must hold:
- Each test case collection is placed in its own folder.
- Each test case collection directory contains one or more directories (test case folders) for each test case.  
  - In each test case folder there must be a file named `positive_query.sparql`.
  - In each test case folder there must be a file named `negative_query.sparql`.
  - In each test case folder there may be a file named `negative_query_hard.sparql`.
  - Each query must have the `SELECT` parameter `?x`. It may have more, but the program ignores the others.
- Each SPARQL query contained in the files must contain a `<number>` placeholder that can be dynamically set by the 
  program.    
    
