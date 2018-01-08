# Accident Route - Charlotte
An Android app which alerts you about the route/accident spots where most of the accidents occur in charlotte city.

Used : Android SDK, JAVA, Google Map Api, Place Api, Generated Geo location model.

Approach:- 

Based on historical accident data in Charlotte.
Model generation - Clustered geo location points(Latitude, Longitude) using KNN algorithm considering certain threshold.
Extracted the Route between Source and Destination and modified the parts of route where a lot of accident occurred in past.
