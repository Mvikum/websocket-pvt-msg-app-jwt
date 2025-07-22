Used **vue app** with **typescript** and **vuetify** using **options API** for the fronend.

Frontend app :
1. npm install
2. npm run dev
3. then go to localhost:[port]/msg
     
in frontend this /msg path is the correct one to go for backend connectivity.

Backend
1. Consider creating "messagedb", or else change it to auto creation. Also include your localhost db credentials in application.yaml file.
2. I didn't includes the register flow in the vue app. so you have to use postman for register a user.
   POST : http://localhost:8080/api/auth/register
     choose **body** then **raw** 
request body :
{
    "email":"sunil@gmail.com",
    "userName":"sunil",
    "password":"sunil@123"
}
   Into the fronend login include one of register email and password. Passwords are saved in form of hashed.
4. then run the springboot.
5. Consider : jwt token expiration time set to 24hours, it secret(secret_key) and expiration also can be found in the application.yaml file
