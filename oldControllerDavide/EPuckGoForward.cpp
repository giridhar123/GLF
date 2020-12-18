#include "MyRobot.cpp"
#include <list>
#include <webots/Supervisor.hpp>

// All the webots classes are defined in the "webots" namespace
using namespace webots;

enum PathMovement
{
    UP = 0,
    DOWN,
    LEFT,
    RIGHT
};

struct Position findExit(int ** map);
void goTo(MyRobot * robot, struct Position destinationPosition);
int * getPath(struct Position from, struct Position to, int ** map);
void printMap(int ** map);
struct Position findAdiacentEmptyCell(int ** map, struct Position currentPosition, std::list<Position> viewedCells);
bool contains(std::list<Position> myList, struct Position position);

int main(int argc, char **argv)
{
    Supervisor * supervisor = new Supervisor();
    // do this once only
    Node *robot_node = supervisor->getFromDef("prova");
    if (robot_node == NULL) {
        std::cout << "No DEF MY_ROBOT node found in the current world file" << std::endl;
        exit(1);
    }
    Field *trans_field = robot_node->getField("translation");

/*
    while (supervisor->step(TIME_STEP) != -1) {
        // this is done repeatedly
        const double *values = trans_field->getSFVec3f();
        std::cout << "MY_ROBOT is at position: " << values[0] << ' '
                << values[1] << ' ' << values[2] << std::endl;
    }
    */

    const double INITIAL[3] = {0, 0.5, 0};
    trans_field->setSFVec3f(INITIAL);

    delete supervisor;
    return 0;

    MyRobot *robot = new MyRobot(ROBOT_POSITION_X,
                                ROBOT_POSITION_Y,
                                ROBOT_ORIENTATION,
                                GRID_SIZE_ROWS,
                                GRID_SIZE_COLS,
                                N_DISTANCE_SENSORS);


    while (robot->step(TIME_STEP) != -1)
    {
        struct Position exitPosition = findExit(robot->getMap());
        goTo(robot, exitPosition);

        printMap(robot->getMap());
        if (robot->getPosition()->x == 0 ||
            robot->getPosition()->x == (GRID_SIZE_ROWS -1) ||
            robot->getPosition()->y == 0 ||
            robot->getPosition()->y == (GRID_SIZE_COLS - 1))
            {
                printf("E-PUCK è UN ROBOT LIBERO!!!\n");
                break;
            }
    }

    delete robot;

    return 0;
}

struct Position findExit(int ** map)
{
    struct Position exitPostion;

    for (int j = 0; j < GRID_SIZE_COLS; ++j)
    {
        if (map[0][j] == EMPTY)
        {
            exitPostion.x = 0;
            exitPostion.y = j;
            return exitPostion;
        }
    }

    for (int j = 0; j < GRID_SIZE_COLS; ++j)
    {
        if (map[GRID_SIZE_ROWS - 1][j] == EMPTY)
        {
            exitPostion.x = GRID_SIZE_ROWS - 1;
            exitPostion.y = j;
            return exitPostion;
        }
    }

    for (int i = 0; i < GRID_SIZE_ROWS; ++i)
    {
        if (map[i][0] == EMPTY)
        {
            exitPostion.x = i;
            exitPostion.y = 0;
            return exitPostion;
        }
    }

    for (int i = 0; i < GRID_SIZE_ROWS; ++i)
    {
        if (map[i][GRID_SIZE_COLS - 1] == EMPTY)
        {
            exitPostion.x = i;
            exitPostion.y = GRID_SIZE_COLS - 1;
            return exitPostion;
        }
    }

    return exitPostion;
}

void goTo(MyRobot * robot, struct Position destinationPosition)
{
    std::cout << "Going to\nX: " << destinationPosition.x << "\nY: " << destinationPosition.y << std::endl << std::flush;
    
    int * path = getPath(*(robot->getPosition()), destinationPosition, robot->getMap());

    std::cout << "Path is: " << std::endl;

    for (int i = 0; path[i] != -1; ++i)
        std::cout << path[i];

    std::cout << std::endl;

    bool notObstacles = true;
    
    int index = 0;

    while (notObstacles && path[index] != -1)
    {
        while (robot->getPosition()->orientation != path[index])
            robot->turnLeft();

        notObstacles = robot->goStraightOn();
        index++;
    }

    delete path;
}

int * getPath(struct Position from, struct Position to, int ** map)
{
    std::list<Position> viewedCells;
    std::list<Position> pathList;
    
    viewedCells.push_back(from);
    pathList.push_back(from);

    struct Position nextPosition;
    struct Position currentPosition = from;

    int count = 0;
    while (pathList.back().x != to.x || pathList.back().y != to.y)
    {
        nextPosition = findAdiacentEmptyCell(map, currentPosition, viewedCells);
        viewedCells.push_back(nextPosition);
        pathList.push_back(nextPosition);
        
        if (nextPosition.x == -1 && nextPosition.y == -1)
        {
            viewedCells.pop_back();
            pathList.pop_back();
            currentPosition = pathList.back();

            if (++count == 4)
            {
                pathList.pop_back();
                currentPosition = pathList.back();
                count = 0;    
            }
        }
        else
        {  
            currentPosition = nextPosition;
            count = 0;
        }

        if (++count == 30)
            break;
    }

    int * path = (int *) malloc(sizeof(int) * (pathList.size() + 1));
    int index = 0;

    for (std::list<Position>::iterator it = pathList.begin(); it != --(pathList.end());)
    {
        Position previousPosition = *it;
        Position nextPosition = *(++it);

        if (nextPosition.x == (previousPosition.x + 1))
            path[index++] = SUD;
        else if (nextPosition.x == (previousPosition.x - 1))
            path[index++] = NORD;
        else if (nextPosition.y == (previousPosition.y - 1))
            path[index++] = OVEST;
        else if (nextPosition.y == (previousPosition.y + 1))
            path[index++] = EST;
    }

    path[index] = -1;

    return path;
}

struct Position findAdiacentEmptyCell(int ** map, struct Position currentPosition, std::list<Position> viewedCells)
{
    struct Position nextPosition;
    nextPosition = currentPosition;

    nextPosition.x += 1;

    if (nextPosition.x < GRID_SIZE_ROWS &&
        map[nextPosition.x][nextPosition.y] == EMPTY &&
        !contains(viewedCells, nextPosition))
        return nextPosition;
    
    nextPosition.x -= 2;
    if (nextPosition.x >= 0 &&
        map[nextPosition.x][nextPosition.y] == EMPTY &&
        !contains(viewedCells, nextPosition))
        return nextPosition;

    nextPosition.x += 1;
    nextPosition.y += 1;
    if (nextPosition.y < GRID_SIZE_COLS &&
        map[nextPosition.x][nextPosition.y] == EMPTY &&
        !contains(viewedCells, nextPosition))
        return nextPosition;

    nextPosition.y -= 2;
    if (nextPosition.y >= 0 &&
        map[nextPosition.x][nextPosition.y] == EMPTY &&
        !contains(viewedCells, nextPosition))
        return nextPosition;

    nextPosition.x = -1;
    nextPosition.y = -1;

    return nextPosition;
}

bool contains(std::list<Position> myList, struct Position position)
{
    for (std::list<Position>::iterator it=myList.begin(); it != myList.end(); ++it)
    {
        if (it->x == position.x && it->y == position.y)
            return true;
    }

    return false;
}

void printMap(int ** map)
{
    std::cout << "Map is:\n";

    for (int i = 0; i < GRID_SIZE_ROWS; ++i)
    {
        for (int j = 0; j < GRID_SIZE_COLS; ++j)
            std::cout << map[i][j] << " ";

        std::cout << "\n";
    }

    std::cout << "\n\n";
}