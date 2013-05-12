# StepManiaSolver
## License
Copyright (c) 2013, Bobbie Smulders

Contact: <mail@bsmulders.com>

License: GPLv3

## Project goal
The goal of this project is to control StepMania with an external application that uses a neural network to try to play the game.

## Project components
### stepmania_diff
This directory contains the source-code needed to adapt the StepMania client so that the game can be controlled over TCP. 

### application
This directory contains the application that controls StepMania. It has various modes to play the game (at random, using a smart algorithm or using a neural network) and can import/export previously recorded data to CSV files. It hosts a server socket that the modified StepMania game will connect to.

## Usage
### stepmania_diff
Apply the "changes.patch" using your favorite diff tool or by hand (the file is human readable). Afterwards, add "StepmaniaToSolver.h" and "StepmaniaToSolver.cpp" to the "src" directory of StepMania and don't forget to add them to your IDE / compiler. 

### application
Compile the java source files using the terminal (don't forget to include the "encog-core-3.1.0.jar" library), or add the java source files to a new Eclipse project (and add "encog-core-3.1.0.jar" as a library).