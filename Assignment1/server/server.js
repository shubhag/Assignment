var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var mongoose = require('mongoose');
var fs = require('fs');


// Create application/x-www-form-urlencoded parser
var urlencodedParser = bodyParser.urlencoded({ extended: false })

app.use(express.static('public'));
mongoose.connect('mongodb://localhost:27017/dbcloud');

//load all files in models dir
fs.readdirSync(__dirname + "/model").forEach(function(filename){
    if(~filename.indexOf('.js')) require(__dirname + "/model/"+ filename)
})

var Instruction = mongoose.model('calculate');

app.get('/', function (req, res) {
   res.sendFile( __dirname + "/" + "index.html" );
})

app.get('/db', function (req, res) {
    Instruction.find(function (err, result){
        res.send(result);
    });
});

app.post('/calculate', urlencodedParser, function (req, res) {

   // Prepare output in JSON format
   var operanda = req.body.operanda
   var operandb = req.body.operandb
   var operator = req.body.operator

    var opArray = ["+", "-", "*", "/", "^", "e", "sin", "cos", "tan"]
    var response = {}
    var result = 0.0
    var error = null
    var inputError = "Input error"
    mongoose.model('calculate').findOne({ operanda : operanda, operandb : operandb, operator : operator} , function(err, ans){
        if(ans != null){
            result = ans["result"]
            error = ans["error"]
            console.log("result from db")
        } else{
            if( !isNaN(operanda) && !isNaN(operandb) && opArray.indexOf(operator) != -1 ){
                if(!operanda.trim()){
                    error = inputError
                } else{
                    if(operator == "e" ){
                        if(!operandb.trim()){
                            result = Math.exp(parseFloat(operanda))
                        } else{
                            error = inputError
                        }
                    } else if(operator == "sin"){
                        console.log("sin enter")
                        if(!operandb.trim()){
                            result = Math.sin(parseFloat(operanda))
                        } else{
                            error = inputError
                        }
                    } else if(operator == "cos"){
                        if(!operandb.trim()){
                            result = Math.cos(parseFloat(operanda))
                        } else{
                            error = inputError
                        }
                    } else if(operator == "tan"){
                        if(!operandb.trim()){
                            result = Math.tan(parseFloat(operanda))
                        } else{
                            error = inputError
                        }
                    } else{
                        if(!operandb.trim()){
                            error = inputError
                        } else{
                            if( operator == '+'){
                                result = parseFloat(operanda) + parseFloat(operandb)
                            } else if(operator == '-'){
                                result = parseFloat(operanda) - parseFloat(operandb)
                            } else if(operator == '*'){
                                result = parseFloat(operanda) * parseFloat(operandb)
                            } else if(operator == '/'){
                                if(parseFloat(operandb) == 0){
                                    error = "Divide by zero error"
                                } else{
                                    result = parseFloat(operanda) / parseFloat(operandb) 
                                }
                            } else if(operator == "^"){
                                result = Math.pow(parseFloat(operanda), parseFloat(operandb))
                            } 
                            else{
                                error = inputError
                            }
                        }
                    }
                } 
            } else{
                error = inputError
            }
            var instruction = new Instruction({
                operanda : operanda, 
                operandb : operandb, 
                operator : operator, 
                result: result.toString(),
                error : error
            });
            instruction.save(function(err, res){
                if(err) return console.error(err);
            });
        }
        response = {
            error : error,
            result : result
        }
        console.log(response);
        res.end(JSON.stringify(response));
    })
    
    
})

app.listen(8080, function () {
console.log('Example app listening on port 8080!');
})
