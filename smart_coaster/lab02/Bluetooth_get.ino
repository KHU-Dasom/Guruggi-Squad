#include <SoftwareSerial.h>
  
  SoftwareSerial BTSerial(2, 3); // SoftwareSerial(RX, TX), 통신을 하기 위한 RX,TX 연결 핀번호
  byte buffer[1024];    // 데이터를 수신 받을 자료를 저장할 버퍼
  int bufferPosition;   // 버퍼에 데이터를 저장할 때 기록할 위치
 
void setup(){
  BTSerial.begin(9600); // 블루투스 모듈 초기화, 블르투스 연결
  Serial.begin(9600);   // 시리얼 모니터 초기화, pc와 연결
  bufferPosition = 0;   // 버퍼 위치 초기화
}
 
 
// loop문 안에서 데이터를 받아올 때는 한번에 한글자씩 받아오게 됨.
// 글자를 하나씩 받아와서 출력하고, 현재 bufferPositon에 맞게 데이터를 버퍼에 저장하고 bufferPositon을 1개 늘려줌.
// 이렇게 계속 반복하여 문자열의 끝('\n') 이 나오게 되면 버퍼의 마지막에 ('\0')을 넣고 버퍼에 저장된 문자열을 
// 다시 스마튼폰으로 전송하고 버퍼를 초기화 해준다. 
 
void loop(){
  if (Serial.available()){     // 블루투스로 데이터 수신, 블루투스에서 신호가 있으면
    char data = Serial.read(); // 수신 받은 데이터 저장
    Serial.write(data);         // 수신된 데이터 시리얼 모니터로 출력
    buffer[bufferPosition++] = data;  // 수신 받은 데이터를 버퍼에 저장
  
    if(data == '\n'){     // 문자열 종료 표시
      buffer[bufferPosition] = '\0';
 
      // 스마트폰으로 문자열 전송
      BTSerial.write(buffer, bufferPosition);
      bufferPosition = 0;
    }  
  }
}


//BTSerial.available() 핸드폰에서 send 신호가 왔을 때 1이다.
//Serial.available() 컴퓨터에서 send 신호를 보낼 때 1이다.
