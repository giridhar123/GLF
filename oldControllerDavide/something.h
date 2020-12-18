#define TIME_STEP 16

//Labirinto
#define GRID_SIZE_ROWS 7
#define GRID_SIZE_COLS 9

//Robot
#define OBSTACLE_TRESHOLD 90
#define MY_TRESHOLD 130

#define ROBOT_POSITION_X 1
#define ROBOT_POSITION_Y 7
#define ROBOT_ORIENTATION OVEST
#define N_DISTANCE_SENSORS 4

struct Position
{
    int x;
    int y;
    int orientation;
} typedef Position;

enum ORIENTATION
{
    NORD = 0,
    EST,
    SUD,
    OVEST
};

enum STATUS
{
    GOING_ON = 0,
    TURNING_LEFT,
    TURNING_RIGHT,
    GOING_BACK,
    STOP
};

enum mapStatus
{
    EMPTY = 0,
    FULL,
    ROBOT,
};