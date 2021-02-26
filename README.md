# FAVOSolver

### Introduction

The project is for providing better solution for the mobile and Steam game "Favo!".

### Environment

- The jar file was compiled by jdk11, please make sure the jre is later than version 11
- The project was made of maven project, you could compile yourself with the instruction below
  - modify line 73 and 74 of `pom.xml`, change 11 with the jdk version you use
  - run the command` $ mvn clean compile assembly:single `
- Run the jar file `$ java -jar target/FAVOSolver-1.0-jar-with-dependencies.jar`

### Usage

1. Input the brick format
   - the format should be constructed with the R, G, B and 0
   - the middle block first, and type the letter w.r.t the block on the edge, if none, type 0
   - if the brick is merge brick, type the color with six 0, and append 'm' at the end
2. After three blocks were input, the program would tell the chosen position with letter 'E' specified on the board
3. Choose if you want to take the chosen step, if not, you could input the coordinate yourself