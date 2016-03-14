## "Building this project" for people who know nothing about Java env.

I'm assuming Linux here.

1. Go to [Android's SDK page](http://developer.android.com/sdk/index.html#Other) and grab whatever the curent version of android-studio-ide is.
2. For maximum fun have a [Oracle's Java 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) installed. I'm being told that it won't fly with the newest version. 
3. *Do not* try to install your own version of Gradle or whatever tooling you think you might need. Keep faith in whatever the factory put into the IDE's arcihve.
4. See if your ```JAVA_HOME``` is set. Even if it is set, Android Stuido might fail to find a proper binary distribution. Use menu, File → Project Structore and set JDK to whatever is valid on your OS (for me it's ```/usr/lib/jvm/java-7-oracle```)

