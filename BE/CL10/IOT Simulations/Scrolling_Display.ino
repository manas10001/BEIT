#include <LiquidCrystal.h>

const int rs = 12, en = 11, d4 = 5, d5 = 4, d6 = 3, d7 = 2;

LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

void setup() {

  // set up the LCD's number of columns and rows:

  lcd.begin(16, 2);
}

void loop() {

  // set the cursor to (0,0):

  lcd.setCursor(0, 0);
  
  lcd.print("Hello World");

    delay(500);

    lcd.setCursor(16, 0);
  

  lcd.autoscroll();

 
  
  lcd.print("H");
  
    delay(500);
  
   lcd.print("e");
  
    delay(500);
  
   lcd.print("l");
  
    delay(500);
  
   lcd.print("l");
  
    delay(500);
  
   lcd.print("o");
  
    delay(500);
  
   lcd.print(" ");
  
    delay(500);
  
   lcd.print("W");
  
    delay(500);
  
   
   lcd.print("o");
  
    delay(500);
  
   lcd.print("r");
  
    delay(500);
  
   lcd.print("l");
  
    delay(500);
  
   lcd.print("d");
  
    delay(500);
  
  lcd.print(" ");
  
    delay(500);
  
  
    lcd.print(" ");
  
    delay(500);
  
    lcd.print(" ");
  
    delay(500);
  

    lcd.print(" ");
  
    delay(500);
 

 lcd.noAutoscroll();

  lcd.clear();
}