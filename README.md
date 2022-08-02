# MAD Assignment Transport Me

AY22 P03 Group L Play Store:https://play.google.com/store/apps/details?id=sg.edu.np.mad.transportme

**Group Members**
| Name                 | Student ID  |
|----------------------|-------------|
| Cyrus Tan Rui Xuan   | S10219142C  |
| Dalbert Chea Zhi Jun | S10222553C  |
| Koo Wen Qi           | S10222922   |
| Kyler Lee            | S10222789H  |
| Tristan Tan Jun Xian | S10223003B  |

## App Description
Designed for all commuters, TransportMe provides fast and easy access to bus timings and ensures that you are never late for work. It lets you know the next bus's arrival time anywhere and anytime in Singapore, with up-to-date data directly sourced from the Land Transport Authority. 

## Features
- 3 live bus timings are shown for each bus
- Favoriting bus stops for quick and easy access 
- Bus Timing Search based on StreetName/ Bus Stop name / Service Number
- Fast, real-time bus timing with a refresh rate of 1 minute 
- Shows bus stops which are near you 
- Adjustable 3 Dimensional map
- Login Features for saved bus stops
- User Routing with notifications on when to alight when using Directions API
- Scan Bus Stop Sign to get bus Stop with Vision API and
- Show Bus Service Route
- Notify Where to alight
- Route Fare Tracker
- Carkpark Availability and Search

## Roles and Contributions

### Cyrus Tan Rui Xuan
1. API Calling with LTA DataMall for Bus Stops and Bus Services https://datamall.lta.gov.sg/content/datamall/en/dynamic-data.html
2. Created A backend for the API Call, Deplyed using heroku https://github.com/Mordaax/transportme-backend.git
3. Main Activity Nested RecyclerView with Android Cards for Nearby Bus Stop Timings 
4. Search Feature with autocorrect for all bus Stops
5. Persistent Storage (SQL) For all bus stops so that users dont have to download all the bus stops everytime the app loads
6. Bottom Nav Bar and initializing of fragments
7. Shared preferences so that users wouldnt have to login everytime they load the app
8. Loading screen
9. Fixed errors throughout the app
10. Merge Maps Fragment to Recycler View for main activity
11. Initalized Project and Classes
12. Added confirm password
13. Exception handling / Toast Messages
14. Added Bus Stop refresh
#### Cyrus Tan Assignment 2
15. Routing from location to location with Google Directions API feature
16. Bus Stop Text Recognizer with Google Vision API
17. Side Navigation Menu with activities, fragments and Animation
18. Bus Number Search Feature
19. Extended Backened API for Bus Service Route
20. App Rating Feature
21. App Sharing Feature
22. Privacy Policy Feature
23. Fixed Issues accross the App

### Dalbert Chea Zhi Jun
1. Main Page Nested Recycler View Implementation & UI
2. Responsive MainActivity (and fragment) layout
3. Favouriting Bus Stop Mechanism
4. Favourites Fragment (Displays all favourited bus stops)
5. General App Debugging
6. Firebase Implementation
7. App Style and Theme Files
8. Nav Bar Design
#### Assignment 2
9. Remind to Alight Bus Stop Feature (In App)
10. Notification Channels
11. Foreground Services
12. Background Location Services (Remind to Alight in Background)
13. MainActivity Organisation

### Koo Wen Qi
1. Map implementation
2. Google Maps API implementation
3. Login Page
4. Registration Page
5. Database Handling
6. Firebase Implementation
#### Assignment 2
7. Polyline of bus routes
8. Showed markers on the map to signify each busstops used by the bus
9. Snackbar shown to indicate bus route
10. Recyclerview replaced to see the busstops of the bus when the option is pressed

### Kyler Lee
1. Creation of Logo
2. App colour scheme design
3. Loading Screen
4. Profiles Page Implementation
5. Profiles Page UI
6. Nav Bar Design
7. Login UI
8. Registration UI
#### Assignment 2
9. Implemented weekly calendar view 
10. Function to add transportation fares and display daily total
11. Integration of transportation fares with calendar
12. Insights page with monthly total and pie chart using AnyChart
13. Sliding animations for transport fares page

### Tristan Tan Jun Xian
1. Login Page
2. Registration Page
3. Mrt Map
4. Image Zoom
5. Firebase Authentication
#### Assignment 2
6. API calling from LTA Datamall for Carpark Availability http://datamall2.mytransport.sg/ltaodataservice/CarParkAvailabilityv2
7. Carpark Availability Page
8. Carpark search feature
9. Design page layout

## App Screenshots
![App Navigation](https://github.com/Mordaax/MAD-Assignment-TransportMe/blob/main/images/App%20Navigation.png)

## ER Data Diagram
![ER Diagram](https://github.com/Mordaax/MAD-Assignment-TransportMe/blob/main/images/ER%20Diagram.png)

## SQL database for bus stops
![SQL](https://github.com/Mordaax/MAD-Assignment-TransportMe/blob/main/images/SQL%20Database.PNG)

## Emulator vs phones
Due to location provider issues, both emulator and android phone cannot work together. 
For Emulator Change Location.Manager.NETWORK_PROVIDER to LocationManager.GPS_PROVIDER
![image](https://user-images.githubusercontent.com/53942938/182011967-27fe3757-2386-47b0-8b7f-3cf09d8aa1dd.png)

