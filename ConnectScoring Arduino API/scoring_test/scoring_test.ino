  #include <ConnectScoring.h>

  ConnectScoring a;
  
  void setup() {
    // put your setup code here, to run once:
    Serial.begin(9600);
    pinMode(13, OUTPUT);
    digitalWrite(13, LOW);
  }
  
  void loop() {
    digitalWrite(13, LOW);
    if(a.isConnected())
    {
      digitalWrite(13, HIGH);
      boolean pins[10] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      a.sendScore(pins);
      //a.genTestScoring();
    }
    delay(1000);
  }
