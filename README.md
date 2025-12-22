Assignment 2: Linear Algebra Engine (LAE)
=========================================

Submission Date: 01/01/2026

Submitters:
-----------
1. Name: Sir Nitzan Isaac Newton 
2. Name: Itamar "Euler" Cohen
3. Name: Tom Georg Ferdinand Ludwig Philipp Cantor
 
Description:
------------
This project implements a multi-threaded Linear Algebra Engine using a custom thread pool (TiredExecutor).
It supports matrix addition, multiplication, transposition, and negation.

Compilation & Execution:
------------------------
1. Compile the project:
   mvn compile

2. Run unit tests:
   mvn test

3. Build the JAR file:
   mvn package

4. Run the program:
   java -jar target/lga-1.0.jar <number_of_threads> <input_file_path> <output_file_path>

   Example:
   java -jar target/lga-1.0.jar 10 ./input_files/example1.json ./output_files/result.json
