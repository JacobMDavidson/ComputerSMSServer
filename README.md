# ComputerSMSServer

Using this Java application in conjunction with the [ComputerSMS Android application](https://github.com/JacobMDavidson/ComputerSMS) will allow you to send and receive text messages through your Android phone using your computer. The Android phone and Computer must be connected to the same local network.

## Usage

1. Select *"Start"* and wait for the message *"Ready to connect"* to be displayed. This will take several seconds.
2. Select the *"Enable"* button from the Android companion application.
3. The message *"Service started"* should now be displayed on both this application and the Android companion application.
4. To send a message:
  * Enter the 10 digit recipient's number in the *"Phone Number:"* text box.
  * Enter the message in the *"Message:"* text box.
  * Select the *"Send"* button.
5. Incoming text messages will be displayed with the sender's phone number in blue.
6. Incoming phone calls will be displayed in red.

## Known Bugs

* If the Android app is disabled while the ComputerSMSServer is running, both applications will need to be closed and restarted before the Android app can once again connect to the ComputerSMSServer.
* If the ComputerSMSServer is closed while the Android app is still enabled, the Android app will need to be disabled and restarted before it can once again connect to the ComputerSMSServer.
