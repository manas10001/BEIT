#include <Servo.h>
Servo myServo;

int pos = 20;

int delayval = 0;
int dir = 1;

void setup()
{
  pinMode(A0, INPUT);
  pinMode(3, OUTPUT);
  Serial.begin(9600);
  myServo.attach(3);
  myServo.write(20);
  delay(100);
}

void loop()
{
  int val = analogRead(A0);
  val = map(val,0,1023,2000/160,8000/160);
  pos = pos+dir;
  
  myServo.write(pos);
  delay(val);
  if(dir == 1 && pos > 180)
  {
    dir = -1;
    return;
  }
  if(dir == -1 && pos < 20)
  {
    dir = 1;
    return;
  }
  
  
  
}