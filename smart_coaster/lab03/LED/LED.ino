#include <SoftwareSerial.h>
/* 블루투스를 사용하기 위한 준비를 합니다. 2번은 TXD, 3번은 RXD 입니다. 데이터를 주고 받을 핀 번호 입니다. */
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
  Serial.println("-= HC-06 Bluetooth RGB LED =-");
  BLU.begin(9600);
  BLU.println("-= HC-06 Bluetooth RGB LED =-");
 
  //pinMode(4, OUTPUT);
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(bluePin, OUTPUT);
  //digitalWrite(4,HIGH);

}
void loop()
{
  while (BLU.available() > 0)
  {
    int redInt = BLU.parseInt();
    int greenInt = BLU.parseInt();
    int blueInt = BLU.parseInt();
    redInt = constrain(redInt, 0, 255);
    greenInt = constrain(greenInt, 0, 255);
    blueInt = constrain(blueInt, 0, 255);
    if (BLU.available() > 0)
    {
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
