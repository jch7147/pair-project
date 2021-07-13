/**
 *
 */


let hours = 0;
let minutes = 0;
let seconds = 0;

const appendHours = document.getElementById("hours");
const appendMinutes = document.getElementById("minutes");
const appendSeconds = document.getElementById("seconds");
const appendTime_todo = document.getElementsByClassName("time_todo");

const buttonStart = document.getElementsByClassName("btn_start");
const buttonStop = document.getElementById("btn_stop");
const buttonReset = document.getElementById("btn_reset");
let intervalId;

buttonStart.onclick = function() {
	clearInterval(intervalId);
	intervalId = setInterval(operateTimer, 1000);
};

buttonStop.onclick = function() {
	clearInterval(intervalId);
	var h = appendHours.innerHTML;
	var m = appendHours.innerHTML;
	var s = appendSeconds.innerHTML;

	var i = appendTime_todo.nodeValue;

	appendTime_todo.innerHTML = h + ":" + m + ":" + s;


};

buttonReset.onclick = function() {
	clearInterval(intervalId);

	hours = 0;
	minutes = 0;
	seconds = 0;

	appendHours.textContent = "00";
	appendMinutes.textContent = "00";
	appendSeconds.textContent = "00";
};

function operateTimer() {
	seconds++;
	appendSeconds.textContent = seconds > 9 ? seconds : "0" + seconds;

	if (seconds > 59) {
		minutes++;
		appendMinutes.textContent = minutes > 9 ? minutes : "0" + minutes;
		seconds = 0;
		appendSeconds.textContent = "00";
	}
	if (minutes > 59) {
		hours++;
		appendHours.textContent = hours > 9 ? hours : "0" + hours;
		minutes = 0;
		appendMinutes.textContent = "00";
	}

}