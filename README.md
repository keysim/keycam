# KEYCAM

Projet Android for Embedded Software Development based on Android at BJTU (Beijing Jiaotong University)

#### Motivation and ambition

The project was born during a Android Embedded Software Development course at Beijing Jiaotong University in China between two students who wanted to create an Android app entirely based on functionality. I (Vireth) think to make an Android application since the first time I started to learn JAVA languages. In love with programming, I wanted to share it through an easy access application so everybody can join and try it. "Does my phone work ?" ambition is to place programming in the middle of a funny, entertaining so everybody even beginners can enjoy programming and hopefully start learning programming languages afterward.

## Description



## Structure

    .
    ├── AndroidManifest.xml		# Manifest
    ├── lib                    	# HTTP Core & Android support
    ├── res				# File source for application
    │   ├── drawable		# Asset Picture
    │   ├── layout			# Template of all Activity
    │   ├── raw			# Asset music
    │   └── values			# Styles and String
    └── src			# MainActivity & Registration

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

[![Keysim](https://raw.githubusercontent.com/keysim/gearobot/master/doc/img/keysim.png)](http://keysim.fr) | [![Vireth](https://raw.githubusercontent.com/keysim/gearobot/master/doc/img/vireth.png)](http://vireth.com)
---|---
:chicken: [Simon Menard](keysim.fr) | :monkey: [Vireth Thach sok](vireth.com)

## License

[The MIT License](http://opensource.org/licenses/MIT)

Copyright (c) 2017 Simon & Vireth
