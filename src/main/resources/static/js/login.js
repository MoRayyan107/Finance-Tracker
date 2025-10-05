// wait until the HTML pages are loaded before running the JS file
document.addEventListener("DOMContentLoaded", () => {

  // find the HTML elements 
  const loginForm = document.getElementById("login-form");
  const errorMessage = document.getElementById("error-message");
  const errorBox = document.getElementById("error-box");

  // Listen to the "Submit" Event from the Form
  loginForm.addEventListener('submit', (event) => {
    // prevent the browser from refreshing the page 
    event.preventDefault();
    
    // get the plaintext values from the fields
    const Username = document.getElementById('username').value;
    const Password = document.getElementById('password').value;

    // create a JS object 
    const credentials = {
      username: Username,
      password: Password
    }

    // use the api to send the credentials to the backend
    fetch('/api/auth/login', {
      // method type
      method: 'POST',
      // body data type must match "Content-Type" header
      headers: {
        'Content-Type': 'application/json'
      },

      // convert the JS object to a JSON string
      body: JSON.stringify(credentials)
    })
    .then(Response => {

      // check the response
      if(!Response.ok){
        // is response is not ok, show error message
        errorBox.style.display = 'block';
        errorMessage.textContent = "Invalid Credentials";
        e
      }
      // if login is successful, parse the JSON response
      return Response.json();
    })
    .then(data => {
      // SUCCSESSFUL LOGIN
      console.log('Login successful', data);
      // store the JWT token in local storage
      localStorage.setItem('token', data.token);
      // redirect to dashboard
      window.location.href = '/dashboard';
    })
    .catch(error => {
      // FAILED LOGIN
      console.error('Error:', error);
    
      errorMessage.textContent = 'Login failed. Please check your credentials and try again.';
      registerForm.reset(); // clear the form fields
      if(errorBox){
        errorBox.style.display = 'block';

        setTimeout(() => {
          errorBox.style.display = 'none';
          errorMessage.textContent = ''; // clear message
        }, 5000); // hide after 5 seconds
      }

    });

  });

});
