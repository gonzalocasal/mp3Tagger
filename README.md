# mp3Tagger

Batch application to set mp3 tags based on file name and folder name. 

The way that the tags get updated is very personal and specific for my use case, but you can take this and adapt it based on your needs.

The artist and title gets read from file that have this format: 
```
artist - title.mp3 
```

* The Year tag is read from a Google search Web scraping, when it can't be found set the value that file already has.
WARNING after many requests you may get blocked  for a couple of minutes, so is not recommended process many files at once.


* The Album tag is always the same than the artist. In my collection, i just group the song by Artist but you can adapt this to get the Album tag similarly than the Year tag.



* The Genre tag applied is the folder name where the file is in. The genre folders and root folder must be configured on application properties. The expected format is:
```
Music 
├── Rock
├── Pop
├── Reggae
```

### Example
For the file in the following folder structure:
```
Music 
├── Rock
    ├── The Rolling Stones - Paint it Black.mp3
```
The result tags applied will be:
```
Artist: The Rolling Stones
Tittle: Paint it Black
Genre: Rock
Year: 1966
Album: The Rolling Stones
```
All the other tags are empty, and the Album Artwork remains if the file already had one.

### Prerequisites
* Apache Maven 3.6.3
* Java 8

### Build
```
 mvn clean package
```

### Execute
```
mvn spring-boot:run
```