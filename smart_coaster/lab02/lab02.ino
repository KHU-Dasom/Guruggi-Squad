#include "HX711.h" //HX711로드셀 엠프 관련함수 호출
#define calibration_factor 70.0 // 로드셀 스케일 값 선언
#define DOUT  3 //엠프 데이터 아웃 핀 넘버 선언
#define CLK  2  //엠프 클락 핀 넘버 
HX711 scale(DOUT, CLK); //엠프 핀 선언 
float latest, current;

void setup() {
  Serial.begin(9600);  // 시리얼 통신 개방
  Serial.println("HX711 scale TEST");  
  scale.set_scale(calibration_factor);  //스케일 지정  calibration_factor = -7050.00 이 수의 절댓값을 더 작게 하면 더 미세한 측정이 가능하다.
  scale.tare(); //영점잡기. 현재 측정값을 0으로 둔다.
  Serial.println("Readings:");
}

// 양이 줄었으면 -1 => 마셨다
// 양이 그대로면 0
// 양이 늘었으면 1 => 따랐다
int isChange(float current, float latest) {
  if (latest - current > 15.0) {
    //마셨다
    

    return -1;
  }
  else if (latest - current < -15.0) {
    //따랐다


    return 1;
  }
  else {
    //암것도 안했다.


    return 0;
  }
}


void isUnderZZan(float current, float latest){
  if(abs(latest - current)>15.0){
    Serial.print("급격한 변화");
    Serial.println();
  }
}
void loop() {
  current = scale.get_units();
  isUnderZZan(current, latest);
  Serial.print("current: ");
  Serial.print(current);  //무게 출력 
  Serial.println(); 
  
  latest = scale.get_units();
  Serial.print("latest: ");
  Serial.print(latest);  //무게 출력 
  Serial.println(); 

}
