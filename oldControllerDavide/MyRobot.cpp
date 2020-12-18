#include "MyRobot.h"

MyRobot::MyRobot(int x, int y, int orientation, int gridSizeRows, int gridSizeCols, int nDistancesSensors) : Robot()
{
    myPosition = (struct Position *) malloc(sizeof(struct Position));

    myPosition->x = x;
    myPosition->y = y;
    myPosition->orientation = orientation;

    leftMotor = getMotor("left wheel motor");
    rightMotor = getMotor("right wheel motor");
    leftMotor->setPosition(INFINITY);
    rightMotor->setPosition(INFINITY);

    leftMotorSensor = getPositionSensor("left wheel sensor");
    rightMotorSensor = getPositionSensor("right wheel sensor");

    leftMotorSensor->enable(TIME_STEP);
    rightMotorSensor->enable(TIME_STEP);

    this->gridSizeRows = gridSizeRows;
    this->gridSizeCols = gridSizeCols;

    this->map = (int **) malloc(sizeof(int *) * gridSizeRows);

    for (int i = 0; i < gridSizeRows; ++i)
    {
        map[i] = (int *) malloc(sizeof(int) * gridSizeCols);

        for (int j = 0; j < gridSizeCols; ++j)
            map[i][j] = EMPTY;
    }

    map[x][y] = ROBOT;
    this->status = STOP;

    this->nDistancesSensors = nDistancesSensors;
    sensors = (DistanceSensor **) malloc(sizeof(DistanceSensor *) * nDistancesSensors);
    
    sensors[0] = getDistanceSensor("ps6");
    sensors[0]->enable(TIME_STEP);

    sensors[1] = getDistanceSensor("ps7");
    sensors[1]->enable(TIME_STEP);

    sensors[2] = getDistanceSensor("ps0");
    sensors[2]->enable(TIME_STEP);

    sensors[3] = getDistanceSensor("ps1");
    sensors[3]->enable(TIME_STEP);
    
    pose[0] = pose[1] = pose[2] = 0;

    std::cout << "robot created\n";
}

struct Position * MyRobot::getPosition()
{
    return myPosition;
}

bool MyRobot::goStraightOn()
{
    status = GOING_ON;
    leftMotor->setVelocity(1 * MAX_SPEED);
    rightMotor->setVelocity(1 * MAX_SPEED);

    bool obstacles = false;
    for (int i = 0; i < 60; ++i)
    {
        if ((obstacles = checkObstaclesInFront()) == true)
        {
            stop();
            for (int j = 0; j <= i; ++j)
                goBack();

            break;
        }
        leftMotor->setVelocity(1 * MAX_SPEED);
        rightMotor->setVelocity(1 * MAX_SPEED);    
        step(TIME_STEP);
    }
    
    stop();

    if (!obstacles)
    {
        map[myPosition->x][myPosition->y] = EMPTY;

        switch (myPosition->orientation)
        {
            case NORD:
                myPosition->x -= 1;
            break;
            case SUD:
                myPosition->x += 1;
            break;
            case EST:
                myPosition->y += 1;
            break;
            case OVEST:
                myPosition->y -= 1;
            break;
        }

        map[myPosition->x][myPosition->y] = ROBOT;
    }

    printPosition();

    return !obstacles;
}

void MyRobot::turnLeft()
{
    status = TURNING_LEFT;

    stop();
    step(TIME_STEP);
    sensorValue[0] = leftMotorSensor->getValue();
    sensorValue[1] = rightMotorSensor->getValue();

    double goalTheta = fmodf(pose[2] + PI/2.00, PI2);

    while(pow(pose[2] - goalTheta, 2) > 0.0001)
    {
        leftMotor->setVelocity(-0.5);
        rightMotor->setVelocity(0.5);
        step(TIME_STEP);
        updatePose();
    }

    stop();
    
    myPosition->orientation -= 1;
    if (myPosition->orientation < 0)
        myPosition->orientation = OVEST;

    printPosition();
}

void MyRobot::updatePose()
{
    // compute current encoder positions
    double del_enLeftW = leftMotorSensor->getValue() - sensorValue[0];
    double del_enRightW = rightMotorSensor->getValue() - sensorValue[1];
    
    // compute wheel displacements
    double dispLeftW; double dispRightW;
    getWheelDisplacements(&dispLeftW, &dispRightW, del_enLeftW, del_enRightW);

    // displacement of robot
    double dispRobot = (dispLeftW + dispRightW)/2.0; 

    // Update position vector:
    // Update in position along X direction
    pose[0] +=  dispRobot * cos(pose[2]); 
    // Update in position along Y direction
    pose[1] +=  dispRobot * sin(pose[2]); // robot position w.r.to Y direction
    // Update in orientation
    pose[2] += (dispRightW - dispLeftW)/AXLE_LENGTH; // orientation
    pose[2] = fmodf(pose[2], PI2) ;

    //printf("estimated robot_theta : %f radians\n", pose[2]);
}

//Function to return displacements of left and right wheel given previous encoder positions
void MyRobot::getWheelDisplacements(double *dispLeftW, double *dispRightW, double del_enLeftW, double del_enRightW)
{
  // compute displacement of left wheel in meters
  *dispLeftW = del_enLeftW / STEPS_ROT * 2 * PI * WHEEL_RADIUS; 
  // compute displacement of right wheel in meters
  *dispRightW = del_enRightW / STEPS_ROT * 2 * PI * WHEEL_RADIUS; 
}

void MyRobot::turnRight()
{
    status = TURNING_RIGHT;
    leftMotor->setVelocity(1 * MAX_SPEED);
    rightMotor->setVelocity(-1 * MAX_SPEED);

    step(TIME_STEP);
    stop();

    myPosition->orientation = (myPosition->orientation + 1) % 4;

    printPosition();
}

void MyRobot::goBack()
{
    status = GOING_BACK;
    leftMotor->setVelocity(-1 * MAX_SPEED);
    rightMotor->setVelocity(-1 * MAX_SPEED);

    step(TIME_STEP);
    stop();

    /*
    if (!checkObstacles())
    {
        map[myPosition->x][myPosition->y] = EMPTY;

        switch (myPosition->orientation)
        {
            case NORD:
                myPosition->x += 1;
            break;
            case SUD:
                myPosition->x -= 1;
            break;
            case EST:
                myPosition->y -= 1;
            break;
            case OVEST:
                myPosition->y += 1;
            break;
        }

        map[myPosition->x][myPosition->y] = ROBOT;
    }
    else
    {
        switch (myPosition->orientation)
        {
            case NORD:
                map[myPosition->x + 1][myPosition->y] = FULL;
            break;
            case SUD:
                map[myPosition->x - 1][myPosition->y] = FULL;
            break;
            case EST:
                map[myPosition->x][myPosition->y - 1] = FULL;
            break;
            case OVEST:
                map[myPosition->x][myPosition->y + 1] = FULL;
            break;
        }
    }
    */
}

void MyRobot::stop()
{
    status = STOP;
    leftMotor->setVelocity(0 * MAX_SPEED);
    rightMotor->setVelocity(0 * MAX_SPEED);
}

int ** MyRobot::getMap()
{
    return map;
}

void MyRobot::printPosition()
{
    return;

    std::cout << "Robot Position - X: " << myPosition->x << " - Y: " << myPosition->y << " - Orientation: " << myPosition->orientation << std::endl;
}

/*
 * return true if obstacles in front
 */
bool MyRobot::checkObstaclesInFront()
{
    if (sensors[1]->getValue() > OBSTACLE_TRESHOLD || sensors[2]->getValue() > OBSTACLE_TRESHOLD)
    {
        switch (myPosition->orientation)
        {
            case NORD:
                map[myPosition->x - 1][myPosition->y] = FULL;
            break;
            case SUD:
                map[myPosition->x + 1][myPosition->y] = FULL;
            break;
            case EST:
                map[myPosition->x][myPosition->y + 1] = FULL;
            break;
            case OVEST:
                map[myPosition->x][myPosition->y - 1] = FULL;
            break;
        }

        return true;
    }

    bool left = false;
    while (sensors[0]->getValue() > MY_TRESHOLD)
    {
        left = true;
        leftMotor->setVelocity(0.5 * MAX_SPEED);
        rightMotor->setVelocity(-0.5 * MAX_SPEED);

        step(TIME_STEP);
    }
    if (left)
    {
        leftMotor->setVelocity(-0.5 * MAX_SPEED);
        rightMotor->setVelocity(0.5 * MAX_SPEED);

        step(TIME_STEP);
        stop();
    }

    bool right = false;
    while (sensors[3]->getValue() > MY_TRESHOLD)
    {
        right = true;
        leftMotor->setVelocity(-0.5 * MAX_SPEED);
        rightMotor->setVelocity(0.5 * MAX_SPEED);

        step(TIME_STEP);
    }
    if (right)
    {
        leftMotor->setVelocity(0.5 * MAX_SPEED);
        rightMotor->setVelocity(-0.5 * MAX_SPEED);

        step(TIME_STEP);
        stop();
    }

    return false;
}