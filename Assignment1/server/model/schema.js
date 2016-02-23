var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var calculator = new Schema({
	operanda : String,
	operandb : String,
	operator : String,
	result : String,
	error : String
});

mongoose.model('calculate', calculator);