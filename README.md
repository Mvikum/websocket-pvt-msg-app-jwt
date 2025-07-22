Frontend app :
1. npm install
2. npm run dev
3. then go to localhost:[port]/msg
     
in frontend this /msg path is the correct one to go for backend connectivity.

Backend
1. Consider creating "messagedb", or else change it to auto creation. Also include your localhost db credentials in application.yaml file.
2. then run the springboot.
3. Consider : jwt token expiration time set to 24hours, it secret(secret_key) and expiration also can be found in the application.yaml file
