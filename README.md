##Bird's Eye
Bird's Eye is a comprehensive tool for identifying and analyzing temporal and geospatial relationships that influence the spread of ideas on the social media platform Twitter.  Simply load your own data, search for a keyword, and hit play.  Then see your data visualized in real-time as Tweets light up on the map.  View statistical summary information such as the keyword's frequency over time, as well as the frequencies of related keywords.
![demo](./resources/demo.gif?raw=true "Demo")

##Authors
- Jeffrey Hamilton  
- Tim Goering

##Features
- The interface consists of multiple coordinated components, all with interactive elements that allow the user to drill down into the data to gather more information.  
- The map view displays geographically tagged Tweets by the minute they appear in the data set.  As you play the animation, Tweets appear the minute they are Tweeted and slowly fade afterward. The size of each data point serves as a measure of the Tweet's influence and corresponds to the number of followers the user has.  The map can be zoomed and scrolled with the mouse wheel and by dragging the right mouse button, respectively.  To view more information about a Tweet, left-click on it.
- The line graph displays the volume of Tweets matching the searched keyword over time.  The line graph also serves as playback controls for the animation. You can left-click on the line graph to skip to a certain point in the animation.  Right-click and drag across the graph to select only a certain time interval for the animation to display.  
- The bar graph plots the frequencies of keywords related to the searched keyword.  You can hover over a bar to view more information about the keyword. Left-click on a bar to search for that particular keyword, and shift-left-click to search for Tweets that contain the current keyword or the new keyword.
- Includes a host of options that allow the user to manipulate the views to see their data in more granularity. You can set the animation's speed or step through individual frames of the animation. You can adjust the size of the map markers and the duration they stay on the map to reduce overplotting.  You can use regular expressions in your search terms to search for multiple keywords or exclude certain keywords, among a wide range of other applications.
<img src="http://i.imgur.com/FIy7CVE.png">

##Attributions
- Parsing streaming data done with GSON. Copyright 2008-2015 Google Inc.  
- Map tiles by CartoDB, under CC BY 3.0. Data by OpenStreetMap, under ODbL.