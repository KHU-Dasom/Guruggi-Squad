#include <SoftwareSerial.h>
/* 블루투스를 사용하기 위한 준비를 합니다. 2번은 TXD, 3번은 RXD 입니다. 데이터를 주고 받을 핀 번호 입니다. */
#include "HX711.h" //HX711로드셀 엠프 관련함수 호출

#define calibration_factor 70.0 // 로드셀 스케일 값 선언
#define BOTTOM_FACTOR 15.0
#define FEW_CHANGE 15.0


float latest, current, saved;

int DOUT = 5;
int CLK = 4;

HX711 scale(DOUT, CLK); //엠프 핀 선언 

int TxPin = 2;
int RxPin = 3;
int bluePin = 9;
int greenPin = 10;
int redPin = 11;
SoftwareSerial BLU(TxPin, RxPin); 
void setup()
{
  //Serial setup
  Serial.begin(9600);
  BLU.begin(9600);
  scale.set_scale(calibration_factor);  //스케일 지정  calibration_factor = -7050.00 이 수의 절댓값을 더 작게 하면 더 미세한 측정이 가능하다.
  scale.tare(); //영점잡기. 현재 측정값을 0으로 둔다.
  //pinMode(4, OUTPUT);
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(bluePin, OUTPUT);
  //digitalWrite(4,HIGH);
}

void loop()
{
      Serial.print("hi");
//    current = scale.get_units();
//    // 잔을 들었다.
//    Serial.print(current);
//    if (latest - current > FEW_CHANGE) {
//      saved = latest;
//      Serial.print("들었다잉");
//    }
  while (BLU.available() > 0)
  {
    latest = scale.get_units();
    int redInt = BLU.parseInt();
    int greenInt = BLU.parseInt();
    int blueInt = BLU.parseInt();
    redInt = constrain(redInt, 0, 255);
    greenInt = constrain(greenInt, 0, 255);
    blueInt = constrain(blueInt, 0, 255);
    if (BLU.available() > 0)
    {
      if (isExist(current))
        setColor(redInt, greenInt, blueInt);
      else
        setColor(0,0,0);
      
      Serial.print("Red: ");
      Serial.print(redInt);
      Serial.print(" Green: ");
      Serial.print(greenInt);
      Serial.print(" Blue: ");
      Serial.print(blueInt);
      Serial.println();
      BLU.flush();
      
    }
  }
}
void setColor(int red, int green, int blue)
{
  analogWrite(redPin, red);
  analogWrite(greenPin, green);
  analogWrite(bluePin, blue);
}

bool isExist(float current) {
  if (current > BOTTOM_FACTOR)
    return true;
  else 
    return false;
}


// 양이 줄었으면 -1 => 마셨다
// 양이 그대로면 0
// 양이 늘었으면 1 => 따랐다
int sendDrinkScore(float current, float latest) {
  float change = latest - current;
  if (change > FEW_CHANGE) {
    //마셨다
    BLU.write(change);
    return -1;
  }
  else if (change < -FEW_CHANGE) {
    //따랐다
    return 1;
  }
  else {
    //암것도 안했다.
    return 0;
  }
}
