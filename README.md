Example Cordova Plugin
=================

Cordova Plugin for Android example for background work

#####Remove a Key from Preference file

function remove(key){

	sharedpreferences.remove(key, successHandler, errorHandler);
	
}

#####Clear a preference file

function clearAll(){

	sharedpreferences.clear(successHandler, errorHandler);
	
}

