# PhoneGap Camera Crop plugin 

for  Android, by [Sayone Technologies](http://www.sayonetech.com)


## 1. Description

This plugin helps you crop the images coming from  gallery or camera

Works with PhoneGap >= 3.0.

## 2. Usage

In Javascript

          cordova.exec(onSuccess, onFail, "openCropCamera", option, //option should be either "camera" or "gallery"
          [{       // and this array of custom arguments to create our entry
                "targetWidth": 132, //Width As integer
                "targetHeight": 132 //Height As integer
          }]);
          
          function onSuccess(imageURI) {
            //ImageURI as base64 String
          }


          function onFail(message) {
            //Error message
          }

In config.xml

          <feature name="openCropCamera">
              <param name="android-package" value="com.phonegap.plugins.cropPlugin.openCropCamera" />
          </feature>

## 3. Credits

This plugin is an enhancement of existing phonegap camera plugin by [Sayone Technologies](http://www.sayonetech.com)

## 4. Issues Or Contributions

* If you have an idea to improve the package, let me know. 
* Post issues in the github issue tracker.
*  Our Email hello@sayonetech.com
*  Pull requests are welcome.


