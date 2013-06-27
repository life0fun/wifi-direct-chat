# A chat app that uses WiFi Direct for Peer-Peer communication

This app uses WiFi Direct P2P channel to exchange chat message.
It also support pub/sub style that group owner publish messages to all group member.

# Java NIO selector and channel.

Java NIO is widely used today by many projects, including Netty.
It provides non blocking IO facilities for writing scalable servers.

Java NIO provides buffer mechanism for buffering incoming data. Channels are pipes through which
byte buffer are transferred between two entities. Normally, channels are in non-blocking mode. 
For example, SocketChannels are operate on non-blocking mode and are selectable. In other word,
it is event-driven so you do not need a dedicate thread for each socket/channel to handle the request.


# Google Android Analytic Event tracking

To use google analytics, all you need is stick libGoogleAnalytics.jar into your project's libs/ folder
and use the tracking Id and  Account you registered with google analytics.

http://android-developers.blogspot.com/2010/12/analytics-for-android-apps.html
