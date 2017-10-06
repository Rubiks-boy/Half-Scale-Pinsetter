#include <Servo.h>
Servo a, b;
void setup() {
  // put your setup code here, to run once:
pinMode(52, OUTPUT);
pinMode(38, OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  a.attach(52);
  b.attach(38);
a.write(100);
b.write(80);
delay(1000);
delay(7000);
a.attach(52);
b.attach(38);
a.write(0);
b.write(180);
delay(1000);
a.detach();
b.detach();
delay(3000);
}
