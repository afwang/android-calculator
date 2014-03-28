android-calculator
==================

This app acts as a learning and testing ground as I learn more about Android app development.

To-do list
==========
- Add in a clear history button
- Remove linked lists from code. Linked lists are not needed since onPause() is called once for every calculation made.
- Share locking between the LoadHistoryTask AsyncTask and the SaveHistoryService IntentService.

Completed tasks
===============
- Send the database lookup code in MainActivity's onCreate() to a background thread.
- Replace SaveHistoryTask with a Service or IntentService. Look into Parcel to send data
