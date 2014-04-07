android-calculator
==================

This app acts as a learning and testing ground as I learn more about Android app development.

To-do list
==========
- Remove Result activity
- Move all database code into the database helper class.

Completed tasks
===============
- Send the database lookup code in MainActivity's onCreate() to a background thread.
- Replace SaveHistoryTask with a Service or IntentService. Look into Parcel to send data
- Add in a clear history button
- (Canceled)Remove linked lists from code. Linked lists are not needed since onPause() is called once for every calculation made.
	- A linked list can be useful in the future when the result activity is removed (The Result activity is an annoyance now)
- Share locking between the LoadHistoryTask AsyncTask and the SaveHistoryService IntentService.
