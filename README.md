# Ghost SMS

## Improvements
* send sms containing encoded message
* generate key pair only once, give the user the possibility to regenerate it
* add a salt to encode the same message differently each time
* append salt to encoded message
* decode message on reception