The primary architecture, I implement with the MVVM, since the requirement is straightforward, one search bar and view on the activity display the weather data.  In order to reuse the instance of components, I configure the dagger hilt library to generate the instance of components and access it across the different layers, all instances of classes configured on the AppModule and DatabaseModule
## Before you start
This project requires the following

1. Android Studio Flamingo
2. Android Gradle Plugin version 7.3.0
3. Gradle version 8.0

## Library dependency
* AndroidX RecyclerView
* Glide
* LiveData + ViewModel
* Coroutines
* Room
* Dagger Hilt
* Retrofit
* junit
* Mockito

## Dependency Injection
* AppModule
* DataBaseModule
* TestModule

## Folder Hierarchy
* database - Room database interface and operators
* di - inject the instance of classes
* listener - callback listener when the adapter is clicked
* model - define the weather table schema and Dto
* network - define the API interface with coroutines suspend functions
* repositories - WeatherReposity is an interface used to delegate between the API and database
* ui - display the search cities view on the recycle view, and the UX on the WeatherActiviy
* util - define the extension functions and constant variables, the State used to keep track the view model state
* viewmodel - WeatherViewModel is where we define the business logic that stores the last search city on the SharedPreference and determines when we query the city weather from the network call or search from the weather database.

## Screenshots
all demo recording video and screenshot

## APK file
The file under apk folder

## Use Case Behavior
* Search city on the search bar and display the weather temperature, city
* Ask the User for location access, If the User gives permission to access the location, then retrieve weather data by default
* After the user search more than one city, we retrieve the last city searched upon app launch.
* Use can scroll the list of card view below the search bar to see the city weather histories if the user ever searched the city
* When the user taps the weather card view, the bottom of the view displays the selected city weather detail
* If the user denied the location permission request, the user still able to search the city weather on the search bar
* If the user turns off the network, the user can still see the list of city weathers on the screen, and interact with the weather card views