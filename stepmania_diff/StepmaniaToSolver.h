/* StepmaniaToSolver - contribution bsmulders */

#ifndef STEPMANIA_TO_SOLVER_H
#define STEPMANIA_TO_SOLVER_H

int STSThread_start( void *p );

class StepmaniaToSolver
{
public:
	StepmaniaToSolver();
	~StepmaniaToSolver();
    
	void Vsync( ) ;
	void Note( float x, float y );
    
private:
	void Send( const char * message );
};

extern StepmaniaToSolver*	STS;	// global and accessable from anywhere in our program
#endif