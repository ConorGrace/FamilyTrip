# Family Trip

## Overview
Family Trip is a social media platform designed to help users plan their holidays efficiently by using social networking features. Users can post their holidays, share locations with people, and share their images with fellow travellers!!

## Features
- **User Authentication**: Secure user authentication system to ensure the safety of user data using Firebase Authentication.
- **Trip Planning**: Users can create, edit, and share trip itineraries with other travellers.
- **Filter Posts**: Advanced filter functionality to discover new destinations, activities, and friends.
- **Responsive Design**: A responsive mobile design that ensures seamless user experience across android devices.
- **Cloud Based**: Posts are stored in the cloud, as well as locations and user info.

## Technologies

### Hardware
The Family Trip app will be compatible with any device capable of running Android Nougat (version 7.0) or higher. This broad compatibility ensures accessibility to a wide range of Android users, maximizing the app's reach in the market. As the app primarily relies on cloud storage for data, concerns regarding local device storage are minimized.

### Software
Family Trip will be distributed through the App Store upon completion. A primary focus during development will be optimizing the app's size and performance for efficient operation on various devices. Specific permissions, such as location and gallery access, will be requested from users as needed for certain functionalities. Development will be carried out using Android Studio, the industry-standard IDE for Android app development. Android Studio offers seamless integration with utilities like Firebase and other APIs, alongside features such as emulator support, ensuring a smooth development experience.

### Methodology - Agile
The Agile Methodology has been selected for the development of Family Trip. This methodology is widely adopted in the software industry for its flexibility and iterative approach. By employing Agile, the development process will be structured into sprints, enabling iterative and incremental progress. This approach facilitates setting and meeting deadlines, incorporating feedback, and fostering collaboration among stakeholders. Additionally, Agile emphasizes code reviews and inspections, ensuring high-quality software development practices throughout the project.

### Version Control - GitHub
All version control is done using Github, allowing people to view and use previous versions of the app.

## Running app
*Note - to run this app, a google maps api key is necessary. Please generate if not exisiting.*

Insert your google maps api key into the local.properties file, as well as the strings.xml. Very important to do so, or else the mapsActivity wont work. Once done, app should function as intended.
