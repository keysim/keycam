package com.keysim.keycam;

public interface Config {
	 
    
    // CONSTANTS
    static final String YOUR_SERVER_URL = "YOUR_SERVER_URL/gcm_server_files/register.php";
     
    // Google project id
    static final String PROJECT_NUMBER = "433489418161"; 
 
    /**
     * Tag used on log messages.
     */
    static final String LOG_TAG = "keycam";
 
    static final String DISPLAY_MESSAGE_ACTION = "com.keysim.keycam.DISPLAY_MESSAGE";
 
    static final String EXTRA_MESSAGE = "msg";
    static final String URL_POST_CAM = "http://keysim.fr/keycam/post.php";
    static final String URL_POST_REGISTER = "http://keysim.fr/keycam/register.php";
    static final String PROPERTY_REG_ID = "registration_id";
    static final String PROPERTY_APP_VERSION = "appVersion";
}
