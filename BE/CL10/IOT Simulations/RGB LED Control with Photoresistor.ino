// C++ code
//
int lightVal = 0;

void setup()
{
  pinMode(13, OUTPUT);
  pinMode(A0, INPUT);
  Serial.begin(9600);
}
void loop()
{
  lightVal = analogRead(A0);
  
  Serial.println(lightVal);
  
  if(lightVal > 550){		//red
    digitalWrite(9, LOW);
    digitalWrite(11, LOW);
    digitalWrite(13, HIGH);
  }
  else if(lightVal > 250 && lightVal < 550){	//blue
    digitalWrite(9, LOW);
    digitalWrite(13, LOW);
    digitalWrite(11, HIGH);
  }
  else{						//green
    digitalWrite(11, LOW);
    digitalWrite(13, LOW);
    digitalWrite(9, HIGH);
  }
  //delay(1000);

}