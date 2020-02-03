Mobile & Social Computing Android project

Objective 
The idea is to implement an application which helps the user to organize their workload and activities by associating custom notifications to different working places.
The system tracks the user location with GPS and sends notifications based on their positions and the tasks nearby.
The app will therefore let the user add tasks to the list with a specified location where the task should be done.
For example, if the user wants to go to the copy shop and the supermarket, the app recognizes the proximity to either of the shops.
With this information, it notifies the user when they are close to the location where they have tasks that need to be done.

Context-aware functionalities
•	User position:
	The user position will be monitored constantly to track if the user is near to a task. As soon as the user
	enters the task, the system thereby knows when to send out alerts. This will also help the user track tasks
	by using the map function. (GeoFence?)
	
•	User identity:
	With user identity, the app can easily track the preferences of the user and it will also be able to track
	different users.
	
•	Date, time of day:
	If the user wants to specify tasks with time restrictions or special alerts to different times, the application can
	keep track of the date and time of the day.
	
•	Calendar:
	Mostly comparable to 'Date, time of day'.
	
•	Orientation (possibly)
•	Temperature (possibly)

Features
•	Login system for different users (User Identity)
•	Map view that shows the tasks of the current user and the user position (GPS)
•	Add task: new tasks can be added with associated description
•	Complete task: completed tasks will be erased from the map
•	Proximity feature: notify the user when they are close to a task location
•	Social media connection (possibly)
•	Expired tasks: if a task is expired or close to expiring, the task will be removed from the list (possibly)
•	Temperature: if it is too hot or too cold to perform a task, it might not be displayed (possibly)

Implementation
	The team will try to implement all the main features (the ones that don’t end with the “possibly” label).

Future work
Possible extensions to the project are all the features marked with the “possibly” label.
Furthermore, a possible improvement to the project would be a responsive UI that uses the accelerometer sensor.

Members
•	Buonanno, 199771, grasta4@gmail.com
•	Luebstorf, 214089, juliusluebstorf@gmail.com
