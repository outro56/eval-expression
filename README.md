## Simple Expression Evaluation

Evaluates a simple math expression with numbers and operators, then return either a number that represents the value of the expression or an exception with a proper error message indicate why expression is not valid.
 
## Build & Run

Run the following to build and package the jars for the application (NOTE: this would also run the unit tests)

        mvn clean package

Then you can start up the application using

         java -jar target/eval-expression-1.0-SNAPSHOT.jar  

### Examples 
 
```
> 5 + 3*2
11
>7.2s + 3
 ***ERROR: 7.2s is not a valid number ***
```