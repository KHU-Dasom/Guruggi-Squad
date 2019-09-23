#include "HX711.h" //HX711로드셀 엠프 관련함수 호출
#define calibration_factor -7050.0 // 로드셀 스케일 값 선언
#define DOUT  3 //엠프 데이터 아웃 핀 넘버 선언
#define CLK  2  //엠프 클락 핀 넘버 
HX711 scale(DOUT, CLK); //엠프 핀 선언 

void setup() {
  Serial.begin(9600);  // 시리얼 통신 개방
  Serial.println("HX711 scale TEST");  
  scale.set_scale(calibration_factor);  //스케일 지정  calibration_factor = -7050.00 이 수의 절댓값을 더 작게 하면 더 미세한 측정이 가능하다.
  scale.tare();  //스케일 설정
  Serial.println("Readings:");
}

void loop() {
  Serial.print(calibration_factor);
  Serial.print("Reading: ");
  Serial.print(scale.get_units(), 1);  //무제 출력 
  Serial.print(" lbs"); //단위
  Serial.println(); 
}
