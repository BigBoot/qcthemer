#Quick Circle Themer

Quick Circle Themer is a Xposed Module which allows you to easily change the watchfaces of the LG G3 Quick Circle Case

![Screenshot](http://bigboot.github.io/qcthemer/images/qcthemer.png)

##Download
Download the apk file [here](http://dl-xda.xposed.info/modules/de.bigboot.qcthemer_v2_0e2740.apk)
You can find some themes [here](https://drive.google.com/folderview?id=0B9SPjhBszDMQSHgxWkc2dFBDcG8&usp=sharing) or [here](http://forum.xda-developers.com/lg-g3/themes-apps/quick-circle-watches-t2906614)

##Theme creation
See Getting the original resources to get an Idea about the files to modify.
Once you've got your modified files, you need to package them for the app.

1. You need a clock.xml, here's an example:

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <clock>
      <!--Title of the clock-->
      <title>Awesome clock</title>
      <!--some identifier. Use something unique-->
      <id>com.coolguy.awesome</id>
      <!--Name of the Author-->
      <author>CoolGuy42</author>
      <!--Description of the clock-->
      <description>Some description</description>
      <!--[Optional] Will automatically switch to clock when it's activated.-->
      <!--Value is the index of the clock-->
      <activate>0</activate>
      <!--Device for which the clock is. "G2" or "G3"-->
      <!--If not present will default to G3-->
      <device>G3</device>
      <replaces>
		  <!--List all files, you want to replace-->
          <file>b2_quickcircle_analog_style03_hour.png</file>
          <file>b2_quickcircle_analog_style03_minute.png</file>
          <file>b2_quickcircle_analog_style03_second.png</file>
          <file>b2_quickcircle_analog_style03_bg.png</file>
      </replaces>
  </clock>
  ```
2. You should optionally provide a preview.png which will be shown in the watchface chooser
3. Now zip up all your files, in the example you would zip the following files:
```
clock.xml
preview.png   
b2_quickcircle_analog_style03_hour.png
b2_quickcircle_analog_style03_minute.png
b2_quickcircle_analog_style03_second.png
b2_quickcircle_analog_style03_bg.png
```

###Getting the original resources
Make sure your phone is rooted and your adb is working

####LG G3
1. Get your LGAlarmClock.apk from your phone:
  ```sh
  adb pull /system/priv-app/LGAlarmClock.apk LGAlarmClock.apk
  ```
2. Extract LGAlarmClock.apk
3. The original resource are in ```LGAlarmClock/res/raw-xxxhdpi/```

####LG G2
1. Get your LGClockWidget.apk from your phone:
  ```sh
  adb pull /system/priv-app/LGClockWidget.apk LGClockWidget.apk
  ```
2. Extract LGClockWidget.apk
3. The original resource are in ```LGClockWidget/res/drawable-xxhdpi/```

## Version
0.3

## Acknowledgements 
* Excilys team for [Android annotations](https://github.com/excilys/androidannotations/wiki)
* Jake Wharton for [viewpagerindicator](http://viewpagerindicator.com/).
* rovo89, Tungstwenty for [Xposed Framework](http://repo.xposed.info/)
* Yoavst for [rolexquickcircle](https://github.com/yoavst/rolexquickcircle)
* Kevin Slaton for the example watchface
