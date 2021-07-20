# KEYCAM

Projet Android for Embedded Software Development based on Android at BJTU (Beijing Jiaotong University)

#### Motivation and ambition

The project was born during a Android Embedded Software Development course at Beijing Jiaotong University in China between two students who wanted to create an Android app entirely based on functionality. I (Simon) think to make an Android application since the first time I started to learn JAVA languages. In love with programming, I wanted to share it through an easy access application so everybody can join and try it. "KEYCAM" ambition is to place programming in the middle of a funny, entertaining so everybody even beginners can enjoy programming and hopefully start learning programming languages afterward.

## Description

KEYCAM is an application to control your device from a web site everywhere in the world. 
Nowadays, a lot of peoples change phone every year so them probably have an old phone in their house wasted at doing NOTHING. So we wanted to make a smart reuse of those smartphone so we created KeyCam.
There is a lot of features, you can take pictures, see the video, vibrate the phone, write a message which will be spoken at loud, etc... 

Example:

You want to see what’s going on in your house when you are out.
You can simply place your smartphone somewhere in your house and start our “KEYCAM” application.
Then you just take control of your phone from your computer or your current smart phone to see if there is anybody by taking a picture or just looking at the streamed video.


## Structure

    .
    ├── AndroidManifest.xml		# Manifest
    ├── lib                    	# HTTP Core & Android support
    ├── res				# File source for application
    │   ├── drawable		# Asset Picture
    │   ├── layout			# Template of all Activity
    │   ├── raw			# Asset music
    │   └── values			# Styles and String
    └── src				# MainActivity & Registration

## Examples Activity

#### Function Play Sound

    /.../
    
    private void playSound(int resId) {
	    if(mPlayer != null) {
	        mPlayer.stop();
	        mPlayer.release();
	    }
	    mPlayer = MediaPlayer.create(this, resId);
	    mPlayer.start();
	  }
    
    /.../

Full code [here](https://github.com/keysim/keycam/blob/master/src/com/keysim/keycam/MainActivity.java)

#### Function Take Picture

    /.../

    public void takePicture(){

		if(mCamera!=null){
			try{
				mCamera.setPreviewDisplay(dummy.getHolder());    
				mCamera.startPreview();
				mCamera.takePicture(null, null, mCall);
			  } catch (IOException e) {
			  }
		  }
	  }
    /.../
	
Full code [here](https://github.com/keysim/keycam/blob/master/src/com/keysim/keycam/MainActivity.java)

## Team & Credits

[![Keysim](https://raw.githubusercontent.com/keysim/gearobot/master/doc/img/keysim.png)](http://keysim.fr)
---
:chicken: [Simon Menard](keysim.fr)

## License

[The MIT License](http://opensource.org/licenses/MIT)

Copyright (c) 2017 Simon & Vireth
