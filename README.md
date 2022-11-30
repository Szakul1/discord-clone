# Discord clone 1.0 (Spring boot + Angular)
Application created as discord clone with spring boot and angular. So far implemented features like login and registration, creating, updating and deleting
servers and text channels, live chat
and user settings.

#### Table of Contents
1. [Features](#Features)
    1. [Login and registration](#1-login-and-registration)
    2. [Server](#2-server)
    3. [Text channel](#2-Text-channel)
    4. [Messages](#3-Messages)
    5. [Users](#4-Users)
2. [Functionalities and technologies](#Functionalities-and-technologies)

---

# Features

### 1. Login and registration

User to register requires uniq username, uniq email and password. After registration verification email with link is sent to provided address.
Email verification is required to be able to log in. If user tries to log in with not active account, he will be directed to error page where
he can resend his verification email. Username, email and password are all validated.

![login-register](https://user-images.githubusercontent.com/56487624/204860595-ae7cf154-829a-4799-8f72-46240dabf051.gif)

### 2. Server

To create new server, user can click "plus" button and creation form will be displayed. Where he can upload logo (optional) and name. If server logo is not
provided server will be displayed as first letters of name. Server displays its text channels and members.

https://user-images.githubusercontent.com/56487624/204860989-bb709634-dd53-43b4-b8c4-fca205d7ea0d.mp4

Owner of the server and only him can edit, and delete server by click settings icon next to server name. After changing server name or logo confirmation panel will
show up. From here form can be saved to server or be restored to saved state; From settings menu owner can also see invitation link to server and ban other users
from server.

https://user-images.githubusercontent.com/56487624/204862201-4079e61e-3a30-4236-a1b6-62764564c9d1.mp4

Owner can ban user by clicking "ban" button next to member nickname. After this banned user will be kicked out of server and won't be able to join again.

https://user-images.githubusercontent.com/56487624/204862338-c514e92f-93bb-4249-9d10-326bb90d7f15.mp4

Not server owner is only able to see invitation link and leave in settings menu (first image). Other users can join server by clicking
invitation link sent by other members and clicking accept button (second image).

<p float="left" align="center">
  <img src="https://user-images.githubusercontent.com/56487624/204910181-9c27aeba-0c74-4be9-9f44-139444c8dfc0.png" width="45%">
  <img src="https://user-images.githubusercontent.com/56487624/204909994-11671284-bbc1-4a6d-a912-5a70af42f04c.png" width="45%">
</p>

### 2. Text channel

Server owner can create new channel by clicking "plus" button text to "Text channels" group and typing name.

https://user-images.githubusercontent.com/56487624/204862173-fdbfcc10-2226-41c7-abad-d42036f0592f.mp4

Owner can edit and delete channel by click settings button next to specific channel name.

https://user-images.githubusercontent.com/56487624/204862232-f5ccac33-3383-42fb-b333-96f455607368.mp4

### 3. Messages

Every server member can send messages on text channels. They are updated live by websockets. Message author can edit or delete his message. When joining
text channel only latest messages are being loaded. When scrolling to the top next messages are being loaded.

https://user-images.githubusercontent.com/56487624/204862263-2844e51e-dcfb-4eaa-b89e-eb55eab3f353.mp4

### 4. Users

User can edit his profile by clicking settings button next to his nickname. Here he can change avatar, username, email and password or log out from his account.

https://user-images.githubusercontent.com/56487624/204862314-9673aa87-329b-44ba-a675-924c84343e09.mp4

# Functionalities and technologies

Backend of application is created with spring boot.

Data is stored in mysql database managed by spring boot jpa with default implementation by hibernate.

User authentication is implemented with basic auth with spring boot security. All stored password are being hashed and salted. For object mapping there was used
map-struct. Email verification is sent by google smtp with expiring token. Live chat is created with websocket. Every endpoint and even websockets are authenticated
and authorized if needed.

Mino was used to store files (currently only images). Images are being compressed to lower transmission data. Location to image is stored in mysql database.

-----

Frontend is created with angular framework.

User avatars are cached when downloading server user list for messages. Web chat is created with stompJs where user is subscribing to text channel which
is currently opened. Every path in url is secured with auth guard.
Authorization header is added by auth interceptor. Group of related endpoints are managed by service dedicated to it. Objects used to communicate with api
are mapped to interfaces.
