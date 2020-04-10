This is a Readme, feel free to edit this however
<br><br><br><br>

To test viewer<->server<->control panel socket connections

- change settings: run -> edit configurations -> turn on parralel run
- run first: 
    - billboard_server -> TCPServerTest -> main
        - should say its accepting new clients
        - should write to database when control panel sends stuff
- run either: 
    - billboard_control_panel -> TCPConnect -> main
        - should ask server to add TCPClass object to database every 3 secs
    - billboard_viewer -> TCPConnect -> main
        - should ask server to send database to it every 3 secs


**Relevant JDK Packages and Classes:** 

 + Swing:<br>
`javax.swing`<br>
`java.awt`

 + Networking:<br>
`java.net`<br>
`java.net.Socket`<br>
`java.net.ServerSocket`

 + Image file format loading:<br>
`javax.imageio.ImageIO`

 + Properties:<br>
`java.util.Properties`

 + Data Connectivity:<br>
`java.sql`<br>
`java.sql.DriverManager`

 + Base64:<br>
`java.util.Base64`

 + Crypto (for password hashing)<br>
`java.security`<br>
`java.security.MessageDigest`

 + XML parsing:<br>
`java.xml.parsers.DocumentBuilderFactory`

 + Threading:<br>
`java.lang.Thread`<br>
`java.lang.Runnable"`

<br>

**Tutorials:**
+ [Java Swing tutorial](https://docs.oracle.com/javase/tutorial/uiswing)
+ [Client-Server Networking tutorial](https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html)
    

