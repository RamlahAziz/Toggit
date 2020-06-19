# Toggit
Chat application developed for Appcon 2019 by easypaisa

# UI
DemoLink: https://drive.google.com/file/d/11MT02O74gxTiGK7JMNLUx5H_lVqNuBDK/view?usp=sharing

# Architecture
![alt text](https://github.com/RamlahAziz/Toggit/blob/master/blob/architecture.png?raw=true)

# How to Run
Required IDEs
1. Android Studio
2. Visual Studio Code(Only if you want to make changes to index.js which contains the code for cloud functions)

Setup Backend:
Firebase is used as the backend for the project.
In order for the firebase project in the firebase console to recognize the version build by a android studio as an authorized version 
that can make database calls, you need to add your android studio SHA1 certificate fingerprints to the firebase project settings
=> To retrieve fingerprint:
  1. wait for android studio to finish build
  2. go to Gradle > Toggit > root > tasks > android > signingReport
  3. select signingReport and copy the SHA1 fingerprint
=> To add the fingerprint
  1. Firebase console > Toggit > Project Settings
  2. Under "your apps", add the fingerprint
Note: if you get "an unknown error occured" during authentication when you build and run the app on your own through android studio,
its because the SHA1 key was not added properly.

Run frontend:
Step 1: Open project in android studio
Step 2: Connect your mobile to the laptop using USB (target device + ensure developer mode is enabled on your phone)
        Alternatively: Use Emulator
Step 3: click run

Please ensure the app has access to your contacts. You must do this manually going to Settings ->Applications -> Toggit -> Permissions -> Contacts (enabled)
