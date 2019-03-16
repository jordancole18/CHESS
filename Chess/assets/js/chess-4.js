window.onload = function () {
var dps = []; // dataPoints
var chart = new CanvasJS.Chart("chartContainer5", {
	title :{
		text: "Node 5"
	},
	axisY: {
		includeZero: false
	},      
	data: [{
		type: "line",
		dataPoints: dps
	}]
});

var xVal = 0;
var yVal = 0; 
var updateInterval = 1000;
var dataLength = 20; // number of dataPoints visible at any point

var updateChart = function (count) {

	count = count || 1;

    $.get("http://10.24.69.172/api/?node=node5", function(request){
        yVal = Number(request);
	});
    
	for (var j = 0; j < count; j++) {
        dps.push({
            x:xVal,
            y:yVal 
        });
        xVal++;
    }
    
	if (dps.length > dataLength) {
		dps.shift();
	}

	chart.render();
};

updateChart(dataLength);
setInterval(function(){updateChart()}, updateInterval);

}