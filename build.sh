#!/bin/bash

javac -d bin src/Board/*.java src/Game/*.java src/UI/*.java src/Effect/*.java src/Main.java 

java -cp bin Main &