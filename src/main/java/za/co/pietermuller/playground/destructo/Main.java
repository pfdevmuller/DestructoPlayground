package za.co.pietermuller.playground.destructo;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

public class Main {
    public static void main(String[] args) {
        LCD.clear();
        LCD.drawString("Hurray", 0, 5);
        Sound.beep();
        LCD.clear();
        LCD.refresh();
    }
}
