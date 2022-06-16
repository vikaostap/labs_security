let form = document.querySelector('form');

async function sleep(ms) {
    await new Promise(resolve => setTimeout(resolve, ms));
}

form.addEventListener('submit', (e) => {
  e.preventDefault();
  return false;
});

function register() {
    var username = document.getElementById('input-username').value;
    var password = document.getElementById('input-password').value;

    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "register", true);
    xhttp.setRequestHeader("Content-type", "text/plain");

    xhttp.onreadystatechange = function() {
        if (this.readyState == 4) {
            console.log("Received register response");
            console.log("Status code: " + this.status);
            console.log("Response text: " + this.responseText);
            if (this.status == 200) {
                showStatusBar(0, this.responseText);
            } else {
                showStatusBar(1, this.responseText);
            }
        }
    };
    username = encodeURIComponent(username);
    password = encodeURIComponent(password);
    xhttp.send(`username=${username}&password=${password}`);
    document.getElementById('input-username').value = "";
    document.getElementById('input-password').value = "";
}

function login() {
    var username = document.getElementById('input-username').value;
    var password = document.getElementById('input-password').value;

    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "login", true);
    xhttp.setRequestHeader("Content-type", "text/plain");

    xhttp.onreadystatechange = function() {
        if (this.readyState == 4) {
            console.log("Received login response");
            console.log("Status code: " + this.status);
            console.log("Response text: " + this.responseText);
            if (this.status == 200) {
                showStatusBar(0, this.responseText);
                var token = xhttp.getResponseHeader("Auth-Token");
                setTimeout(function() {
                    window.location = "/personalInfo.html?username=" + username + "&token=" + token;
                    },
                    2000
                )
            } else {
                showStatusBar(1, this.responseText);
            }
        }
    };
    username = encodeURIComponent(username);
    password = encodeURIComponent(password);
    xhttp.send(`username=${username}&password=${password}`);
    document.getElementById('input-username').value = "";
    document.getElementById('input-password').value = "";
}

function updatePersonalInfo() {
    var username = document.getElementById('input-username').value;
    var address = document.getElementById('input-address').value;
    var phoneNumber = document.getElementById('input-phoneNumber').value;
    var token = document.getElementById('input-token').value;

    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "updatePersonalInfo", true);
    xhttp.setRequestHeader("Content-type", "text/plain");

    xhttp.onreadystatechange = function() {
        if (this.readyState == 4) {
            console.log("Received login response");
            console.log("Status code: " + this.status);
            console.log("Response text: " + this.responseText);
            if (this.status == 200) {
                showStatusBar(0, this.responseText);
            } else {
                showStatusBar(1, this.responseText);
                setTimeout(function() {
                    window.location.href = '/';
                    },
                    2000
                )
            }
        }
    };
    username = encodeURIComponent(username);
    address = encodeURIComponent(address);
    phoneNumber = encodeURIComponent(phoneNumber);
    token = encodeURIComponent(token);
    xhttp.send(`username=${username}&token=${token}&address=${address}&phoneNumber=${phoneNumber}`);
}

function logout() {
    window.location = "/";
}

function showStatusBar(error, msg) {
    var color = error === 0 ? 'Lime' : 'DarkOrange';
    document.getElementById("input-status-bar").style.backgroundColor = color;
    document.getElementById("input-status-bar-text").innerHTML = msg;
    document.getElementById("input-status-bar").style.opacity= "1";
}

function resetStatusBar() {
    document.getElementById("input-status-bar").style.backgroundColor = 'transparent';
    document.getElementById("input-status-bar-text").innerHTML = 'empty';
    document.getElementById("input-status-bar").style.opacity= "0";
}

function setOnClick(elementId, onclickFunc) {
    var element =  document.getElementById(elementId);
    if (typeof(element) != 'undefined' && element != null) {
        element.onclick = onclickFunc;
    }
}

/*************************************************************/
/*    Main   */

resetStatusBar();

setOnClick("input-username", resetStatusBar);
setOnClick("input-password", resetStatusBar);
setOnClick("input-address", resetStatusBar);
setOnClick("input-phoneNumber", resetStatusBar);
