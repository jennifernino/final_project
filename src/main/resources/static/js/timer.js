//sets the time for each round to be 30
const TIME_LIM = 30;
//starting times: at start of timer, there are 30 seconds left, no time passed
let timePassed = 0;
let timeLeft = TIME_LIM;
let timerInterval = null;

//builds the timer item, formatting time left in a circle
document.getElementByID("timeradd").innerHTML = '
<div class="base-timer">
  <svg class="base-timer__svg" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
    <g class="base-timer__circle">
      <circle class="base-timer__path-elapsed" cx="50" cy="50" r="45" />
    </g>
  </svg>
<span id="base-timer-label" class="base-timer__label">
  ${formatTimeLeft(timeLeft)}
</span>
</div>
';
//starts the counting down of the timer
countdown();

//adjusts the format in which remaining time is shown in the timer
function formatTimeLeft(time) {
  let seconds = time % 60;

  if (seconds < 10){
    seconds = '0${seconds}';
  }
  return '0:${seconds}'
}

//decreases the count in the timer every second
function countdown(){
  timerInterval = setInterval(() => {
    timePassed = timePassed + 1;
    timeLeft = TIME_LIM - timePassed;
    document.getElementByID("base-timer-label").innerHTML = formatTimeLeft(timeLeft);
  }, 1000)
}
