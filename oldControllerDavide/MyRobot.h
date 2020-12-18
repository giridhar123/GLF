#include <webots/Robot.hpp>
#include <webots/Motor.hpp>
#include <webots/DistanceSensor.hpp>
#include <webots/Gyro.hpp>
#include <webots/Accelerometer.hpp>
#include <webots/PositionSensor.hpp>
#include "something.h"

using namespace webots;

#define MAX_SPEED 6.28

// Robot constants for odometry
#define WHEEL_RADIUS 0.0205 // in m
#define AXLE_LENGTH 0.052 // in m
#define STEPS_ROT 1000 // 1000 steps per rotatation
#define PI 3.141592654
#define PI2 6.283185307

class MyRobot : public Robot
{
public:
    MyRobot(int x, int y, int orientation, int gridSizeRows, int gridSizeCols, int nDistancesSensors);
    struct Position * getPosition();

    void stop();
    void goBack();
    void turnRight();
    void turnLeft();
    bool goStraightOn();
    int ** getMap();

private:
    struct Position * myPosition;
    Motor * leftMotor;
    Motor * rightMotor;
    int status, gridSizeRows, gridSizeCols, nDistancesSensors;
    int ** map;
    DistanceSensor * *sensors;
    
    bool checkObstaclesInFront();
    void printPosition();

    PositionSensor * leftMotorSensor;
    PositionSensor * rightMotorSensor;
    double pose[3];
    double sensorValue[2];
    void updatePose();
    void getWheelDisplacements(double *dispLeftW, double *dispRightW, double del_enLeftW, double del_enRightW);
};