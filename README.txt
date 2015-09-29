README.TXT

whiskeyDem is open source!  This means you are free to modify it as needed
according to the General Public License, GPL.  Please feel free to
distribute this software, modify it, play with it and have fun.  Please,
remember to cite the original authors at the bottum of this document  when
expanding on this work.  whiskeyDem, with all it's additions of network
communication code now has the very real possibility of being extended into
a true distributed simulation.  This readme will attempt to point the
reader and the new purveyor of "whiskey" in that direction.  Good luck.
Here are some fixes and additions that could be done to that end:

* Fixing some of the lock code to use real java locks and not java variables
  in ipKtalk.  Take a look at the code and you will understand.
* Fix GraphicsConfiguration message that appears on startup.
* Integrate, more tightly, the Updating of the screen and sending TCP packets.
  In other words coordinate the run() thread in MovementModel.java with the 
  sending of flight packets.
* To add a server for each distributed client to connect to you would need to
  design it by using either ipKchat's server framework or,
  better still, use the channels function of Java's NIO feature.  This
  would be the most exciting highest performing way.
* In addition to a server, the reader would need to extend the simple 
  "botsOn" framework in the fligh simulator to include a dynamic
  aircraft instantiation/destruction routine to play remote user piloted
  aircraft in the sim and remove them when those users disconnect.
* Add all options in the "config.txt" to a user friendly menu withint the
  current simulation options menu.
* Closer integration of ipKtalk and ipKchat with the simulation.
* Weapon and scoring system.
* A flight User Interface with radar and such.
* A basic physics model.
* A basic collision detection algorithm.
* Other obects, such as buildings and boats.
* Fix any deprecated functions.  (Compile with -Xlint:deprecation)
* Fix any unchecked items.  (Compile with -Xlint:unchecked)

If all of the above or even some of the suggestions are accomplished then
the reader would, truely, have a fantastic distributed virtual environment.
=+]

Original authors and contributors:
Sun's Java team
IBM's many talented engineers
Yuichi Motai
Sergei Grichine
Kevin Gorman
Daneyand Singley
