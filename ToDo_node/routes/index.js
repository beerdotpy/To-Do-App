var express = require('express');
var router = express.Router();

/* GET home page. 
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});*/

/* ToDo Web App Welcome Page 
router.get('/Todoist',function(req,res,next){
   var db = req.db;
   var collection = db.get('task');
   collection.find({},{},function(e,docs){
       res.render('welcome',{ title:'Todoist - Save your ToDo task here','tasks':docs });
   });
});*/

/* Login Page */
router.get('/',function(req,res,next){
   // Check if user is already logged in or not
   if(!req.session.user_id){
      res.render('login',{title:'Todoist - Save your To Do task here'});
   }else{
      res.redirect('/home');
   }
});

/* Login POST request */
router.post('/login',function(req,res,next){
   //Initialize DB 
   var db = req.db;

   // Get input values
   var email = req.body.email;
   var pass = req.body.password;

   // Get all users from db
   var users = db.get('user');

   // If user exist redirect to Homepage
   users.find({'email':email,'password':pass},function(err,user){
   if(err){
     console.log("User does not exist");
   }else{
     req.session.user_id = email;
     res.redirect('/home');
   }
   });
});

/* User Home Page */
router.get('/home',function(req,res,next){
   res.render('homepage',{title:'Todoist - Save your To Do task here'});
});
  

module.exports = router;
