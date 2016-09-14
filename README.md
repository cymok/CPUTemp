# CPUTemp
CPUTemp is a Xposed module which adds a label to the status bar showing the current CPU temperature of your device.

[![Download](https://img.shields.io/badge/Download-Xposed Repo-green.svg)](http://repo.xposed.info/module/org.narcotek.cputemp)

## About
Linux-based systems allow applications and processes to read sensor values by providing in-memory files containing the sensor's values. Among other sensor values the CPU temperature can be retrieved this way.

The CPUTemp application uses this technique to read a thermometer file containing the CPU temperature periodically and update a label to display the temperature. This label is inserted at boot into the Android device's status bar using Xposed.

## Requirements
* Xposed Version 82+
* Android 5.0+

For older versions you may want to use [this app](http://forum.xda-developers.com/xposed/modules/mod-cputemp-statusbar-t2494170) by XDA user m11kkaa.

## Installation & setup
Download the application from the link above and install it. Don't forget to enable it in the Xposed Installer. **Important**: After installing you need to set the temperature file path manually.

The temperature file for the CPU thermometer should be in a subdirectory of "/sys/". However the temperature file name and path is device dependent. This means you need to search this directory for the correct file and subdirectory. Moreover not every device has a specific CPU thermometer but several other thermometers distributed on the devices board. The one nearest to the CPU has the "most correct" measured temperature and their subdirectory usually has a trailing zero in the name (e. g. "thermal_zone0") or is named something like "core_temp". The default file path ("/sys/class/thermal/thermal_zone0/temp") therefore won't work on every device. To further check you can read the content of the "type" file which is placed in every subdirectory of "/sys/class/thermal/". Usually the directory with the type file containing "tsens_tz_sensor0" is the one you are looking for.

Furthermore the free input of the file path allows you to put in another temperature file instead of the CPU's (e. g. the battery temperature file) if you so desire.

If the temperature is not shown in the status bar and/or a Toast message is shown the app probably has problems reading the temperature file. Reasons are the format of the temperature file or missing permissions. For the latter you can try to enable the "use root" setting in the CPUTemp app, which causes the SystemUI app (which effectively is reading the temperature file) to ask for root permissions. This way it should be garantueed that missing permissions are not the reason for the module not to work.

## Permissions
The SystemUI asks for root priviliges **only** if the "use root" option is checked. This is a fix for some ROMs/devices where permission to read the temperature file gets denied.

## Planned features
* Adding support for multiple temperature views (having the ability to set up battery and CPU temperature labels in the status bar concurrently)
* A dashboard in the app showing the current temperatures of the device's thermal sensors

## Bug reporting
If you found a bug in this software feel free to open an issue. Please watch out for duplicates. Please provide Xposed logs and mention your ROM, Kernel, Android version and device in the post.

## Want to contribute?
Issue reporting is one thing, trying to help with coding is another. I gladly welcome merge requests as I do not always have time to implement features or fix bugs.

## Libraries
CPUTemp uses the [VintageChroma](https://github.com/MrBIMC/VintageChroma/) library by MrBIMC and the [XposedBridge](https://github.com/rovo89/XposedBridge/) library by rovo89. VintageChroma and XposedBridge are licensed under the **Apache License 2.0**. A copy of this license can be found on this repository ([THIRDPARTY.TXT](THIRDPARTY.TXT)).

## License
CPUTemp is licensed under the **GNU General Public License Version 3 (GNU-GPLv3)**. A copy of the license can be found on this repository ([LICENSE.TXT](LICENSE.TXT)).

    Copyright (C) 2016 narcotek

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
