Mobile & Social Computing Android project

 Objective 
The idea is to implement an application which helps the user to organize their workload and activities by associating custom notifications to different working places.
The system tracks the user location with GPS and sends notifications based on their positions and the tasks nearby.
The app will therefore let the user add tasks to the list with a specified location where the task should be done.
For example, if the user wants to go to the copy shop and the supermarket, the app recognizes the proximity to either of the shops.
With this information, it notifies the user when they are close to the location where they have tasks that need to be done.

Contrary to the feature of reminders (for example the basic apple reminder app), the user is able to track their reminders on
the map and therefore get an overview of all the tasks that they want to complete. Also, on one system it is possible to track
different users so that you can separate tasks between different users. With this, the user shall have an organized overview
of all tasks that they want to monitor and see where they want to complete it.

Context-aware functionalities
•	User position:
	The user position will be monitored constantly to track if the user is near to a task. As soon as the user
	enters the task, the system thereby knows when to send out alerts. This will also help the user track tasks
	by using the map function.
	
	In detail, this means that when the user logged into the app, the main activity will get called. With the call
	of the main activity, a background service starts to track the location of the user. Every 10 seconds, the user
	location will be updated. With every update, the background service calls a broadcast receiver that checks if 
	the user is next to a task that has been created before. 
	
	The coordinates of the task locations are stored in a database and are also saved temporarily in a hashmap that
	changes through the different tasks depending on the user that is logged in at that time. With the saved coordinates
	(latitude and longitude), a geofence is added for each task. With this geofence, the aforementioned broadcast receiver
	can monitor for entry and exit next to a task location. 
	
	Based on this, a notification will be sent (by the broadcast receiver that is called by the background service). If
	the user clicks on this notification, they will be redirected to the main screen of the application. The notification
	also allows the user to mark a task as completed from outside the application.
	
	A map activity can be called that shows the location of the user and all their active geofences. With every 4 seconds,
	the location of the user will be updated on the map (with fused location) and the camera always centers on the user's
	location. Next to the user, they will find their task locations with a marker and a circle that shows the geofences
	created. If the user taps on the task marker, they will see the description of the task at that location.
	
•	User identity:
	With user identity, the app can easily track the preferences of the user and it will also be able to track
	different users.
	
	The first activity that is called when the app will be opened is the login screen. A user can register with a username,
	password and email. This data will be stored in the database. The user can also choose to login with their facebook
	account. If they forget their credentials, they can use the "forgot password" functionality that sends the credentials
	to the registered email.
	
	Inside the app, the user may change their password via the settings activity. The user will only be able to see the
	tasks that they created and that have been associated to them in the database. Like this, the application considers
	the identity context of the specific user.
	
	As soon as the user logs out of the app, all existing notifications and notification channels will be deleted to 
	prevent theft of data. When another user logs in, the application considers the context again and shows the appropriate
	data.
	
•	Date, time of day: (possibly)
	If the user wants to specify tasks with time restrictions or special alerts to different times, the application can
	keep track of the date and time of the day.
	
	For example, if a user wants to set an expiry date of a task, the task will destroy itself based on the set time.
	However, this functionality has not been implemented and is an idea for the future.
	
•	Calendar: (possibly)
	Mostly comparable to 'Date, time of day' with the addition of managing tasks inside the calendar function.
	
•	Orientation (possibly)
•	Temperature (possibly)

Features
•	Login system for different users (User Identity)
	- users can be registered
	- users can change their password manually
	- users get an email with their password if they forgot it
	- users can login and logout and can only see their personal tasks

•	Map view that shows the tasks of the current user and the user position (GPS)
	- the user's position is marked on a google map
	- inside the maps activity, the user location will be updated every 4 sec
	- the tasks are displayed with a marker surrounded by a circle that visualizes the geofence of the task
	
•	Add task: new tasks can be added with associated description
	- the user has to set a description to the task
	- the location of the task can be moved with drag and drop inside the map fragment of the add task activity
	- the description of the task and the associated user are saved as primary keys in the database
	
•	Complete task: completed tasks will be erased from the map
	- a list of all tasks is displayed in the end task activity (displayed with a recycler card view)
	- if the user clicks on a task, they will be asked if they want to delete that task. They can approve or cancel.
	- as soon as a task is deleted, it will also be deleted from the database
	- a deleted task will be erased from the map and will not be shown any more
	- all pending notifications of the deleted task will be destroyed
	
•	Proximity feature: notify the user when they are close to a task location
	- this is implemented with geofences
	- as soon as the user moves inside a geofence (radius 200m), they will receive a notification
	- as soon as the user moves out of the geofence, the notification will be erased

•	Database: a database holds all relevant information that is needed to persistently track user preferences
	- a room database has been created that holds information of users, their credentials and tasks
	- there are two entities: users and tasks
	- tasks are always associated to a user and have a unique description (primary keys: user, description)
	
•	Social media connection - Facebook Login
	- a user can decide if they want to login to the app with their Facebook account
	
•	Expired tasks: if a task is expired or close to expiring, the task will be removed from the list (possibly)
	- feature not implemented -> future work
•	Temperature: if it is too hot or too cold to perform a task, it might not be displayed (possibly)
	- feature not implemented -> future work

Implementation
	The team will try to implement all the main features (the ones that don’t end with the “possibly” label).

Future work
Possible extensions to the project are all the features marked with the “possibly” label.
Furthermore, a possible improvement to the project would be a responsive UI that uses the accelerometer sensor.

Members
•	Buonanno, 199771, grasta4@gmail.com
•	Luebstorf, 214089, juliusluebstorf@gmail.com
