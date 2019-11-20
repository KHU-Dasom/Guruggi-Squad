 #include <SoftwareSerial.h>
/* 블루투스를 사용하기 위한 준비를 합니다. 2번은 TXD, 3번은 RXD 입니다. 데이터를 주고 받을 핀 번호 입니다. */
#include "HX711.h" //HX711로드셀 엠프 관련함수 호출

#define calibration_factor 70.0 // 로드셀 스케일 값 선언
#define BOTTOM_FACTOR 15.0
#define FEW_CHANGE 30.0


float latest, current, saved;

int DOUT = 5;
int CLK = 4;
HX711 scale(DOUT, CLK); //엠프 핀 선언 

bool isExist = false;
int TxPin = 2;
int RxPin = 3;
int bluePin = 9;


int greenPin = 10;
int redPin = 11;
SoftwareSerial BLU(TxPin, RxPin);


int redInt;
int greenInt;
int blueInt;

int saveRed;
int saveGreen;
int saveBlue;

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
  current = scale.get_units();
  Serial.print(current);
  Serial.print("    ");
  
  if (latest - current > FEW_CHANGE) {
      Serial.print("놓았다잉");
      Serial.println(saved);
      isExist = true;
      if (saved < current){
        char buffer [100];
        itoa(current - saved, buffer, 10);
        BLU.write(buffer);
        BLU.write('\n');
      }
      // 야매 기능 어쩔 수 없음...
      if (current - saved > BOTTOM_FACTOR)
        setColor(saveRed,saveGreen,saveBlue);
  }
  
  else if (current - latest > FEW_CHANGE) {
      saved = latest;
      Serial.print("들었다잉");
      isExist = false;
      setColor(0,0,0);
  }


  delay(1000);

  Serial.println();
  latest = current;

  if (BLU.available() > 0)
  {
    redInt = BLU.parseInt();
    greenInt = BLU.parseInt();
    blueInt = BLU.parseInt();
    redInt = constrain(redInt, 0, 255);
    greenInt = constrain(greenInt, 0, 255);
    blueInt = constrain(blueInt, 0, 255);
    
    if (BLU.available() > 0){
      saveRed = redInt;
      saveGreen = greenInt;
      saveBlue = blueInt;
      setColor(redInt, greenInt, blueInt);
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
