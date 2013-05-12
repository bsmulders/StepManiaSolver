/* StepmaniaToSolver - contribution bsmulders */

#include "global.h"
#include "StepmaniaToSolver.h"
#include "RageLog.h"
#include "RageTimer.h"
#include "RageThreads.h"
#include "InputFilter.h"

#include <ctime>
#if defined(_WINDOWS)
#include <windows.h>
#endif
#include <map>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

StepmaniaToSolver*	STS;	// global and accessable from anywhere in our program

static RageThread STSThread;
static bool sts_Shutdown;
static int sts_sockfd;

int STSThread_start( void *p )
{    
	while( !sts_Shutdown )
	{
        char buf[1024];
        bzero(buf, 1024);
        int n = read(sts_sockfd, buf, 1024);
        if (n < 0)
            LOG->Warn("Error reading from socket");
        
        RageTimer rtimer = RageTimer(0,0);
        rtimer.Touch();
        DeviceInput dipUp = DeviceInput(DEVICE_KEYBOARD, KEY_UP, rtimer);
        DeviceInput dipDown = DeviceInput(DEVICE_KEYBOARD, KEY_DOWN, rtimer);
        DeviceInput dipLeft = DeviceInput(DEVICE_KEYBOARD, KEY_LEFT, rtimer);
        DeviceInput dipRight = DeviceInput(DEVICE_KEYBOARD, KEY_RIGHT, rtimer);
        
        switch(buf[0]) {
            case '1':
                LOG->Info( "Left" );
                dipLeft.bDown = true;
                dipLeft.level = 1;
                dipLeft.z = 0;
                INPUTFILTER->ButtonPressed( dipLeft );
                rtimer.Touch();
                dipLeft.bDown = false;
                dipLeft.level = 0;
                INPUTFILTER->ButtonPressed( dipLeft );
                break;
            case '2':
                LOG->Info( "Down" );
                dipDown.bDown = true;
                dipDown.level = 1;
                dipDown.z = 0;
                INPUTFILTER->ButtonPressed( dipDown );
                rtimer.Touch();
                dipDown.bDown = false;
                dipDown.level = 0;
                INPUTFILTER->ButtonPressed( dipDown );
                break;                
            case '3':
                LOG->Info( "Up" );
                dipUp.bDown = true;
                dipUp.level = 1;
                dipUp.z = 0;
                INPUTFILTER->ButtonPressed( dipUp );
                rtimer.Touch();
                dipUp.bDown = false;
                dipUp.level = 0;
                INPUTFILTER->ButtonPressed( dipUp );
                break;                
            case '4':
                LOG->Info( "Right" );
                dipRight.bDown = true;
                dipRight.level = 1;
                dipRight.z = 0;
                INPUTFILTER->ButtonPressed( dipRight );
                rtimer.Touch();
                dipRight.bDown = false;
                dipRight.level = 0;
                INPUTFILTER->ButtonPressed( dipRight );
                break;
        }
	}
    
	return 0;
}

StepmaniaToSolver::StepmaniaToSolver()
{    
    // Setup network
    int portno;
    struct sockaddr_in serveraddr;
    struct hostent *server;
    char *hostname;
    
    hostname = "localhost";
    portno = 6789;
    
    // Create socket
    sts_sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sts_sockfd < 0)
        LOG->Warn("Error opening socket");
    
    // Get the hostname
    server = gethostbyname(hostname);
    if (server == NULL) {
        LOG->Warn("Error, no such host as %s", hostname);
        exit(0);
    }
    
    // Build the internet address
    bzero((char *) &serveraddr, sizeof(serveraddr));
    serveraddr.sin_family = AF_INET;
    bcopy((char *)server->h_addr,
          (char *)&serveraddr.sin_addr.s_addr, server->h_length);
    serveraddr.sin_port = htons(portno);
    
    // Try to connect
    if (connect(sts_sockfd, (struct sockaddr *) &serveraddr, sizeof(serveraddr)) < 0)
        LOG->Warn("Error connecting to server");
    
    // Report for duty
    LOG->Info("StepmaniaToSolver is ready");
    Send( "StepmaniaToSolver loaded\n" );
    
    // Setup thread for receiving network
    sts_Shutdown = false;
	STSThread.SetName("Stepmania to solver");
	STSThread.Create( STSThread_start, this );
}

StepmaniaToSolver::~StepmaniaToSolver()
{
    sts_Shutdown = true;
    close(sts_sockfd);
}

void StepmaniaToSolver::Vsync( )
{
    Send( "VSYNC\n" );
}

void StepmaniaToSolver::Note( float x, float y )
{
    char * message = (char *) malloc(sizeof(char) * 30);
    asprintf(&message, "XY;%f;%f\n", x, y);

    Send(message);
}

void StepmaniaToSolver::Send( const char * message )
{
    int n = write(sts_sockfd, message, strlen(message));
    if (n < 0)
        LOG->Warn ("Error writing to socket");
}