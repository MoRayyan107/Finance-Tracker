// wait until the HTML page is loaded before running the JS file
document.addEventListener("DOMContentLoaded", () => {

  // find the HTML elements 
  const registerForm = document.getElementById("register-form");
  const errorMessage = document.getElementById("error-message");
  const errorBox = document.getElementById("error-box");

  // Listen to the "Submit" Event from the Form
  registerForm.addEventListener('submit', (event) => {
    // prevent the browser from refreshing the page 
    event.preventDefault();
    
    // get the plaintext values from the fields
    const Username = document.getElementById('username').value;
    const Password = document.getElementById('password').value;
    const ReTypedPassword = document.getElementById('confirm-password').value;

    // check if the plaintext password matches the re-typed password
    if (Password !== ReTypedPassword) {
      errorMessage.textContent = 'Passwords do not match. Please try again.';
      errorBox.style.display = 'block';
      setTimeout(() => {
        errorBox.style.display = 'none';
        errorMessage.textContent = '';
      }, 5000);

      registerForm.reset(); // clear the form fields

      return; // stop the registration process
    }

    // create a JS object 
    const credentials = {
      username: Username,
      password: Password
    }

    // use the API to send the credentials to the backend
    fetch('/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials)
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Registration failed');
      }
      return response.json();
    })
    .then(data => {
      // SUCCESSFUL REGISTRATION
      console.log('Successfully Registered', data);

      // store the JWT token in local storage (if you want auto-login)
      localStorage.setItem('token', data.token);

      // redirect to login page
      window.location.href = '/login'; 
    })
    .catch(error => {
      // FAILED REGISTRATION
      console.error('Error:', error);
      errorMessage.textContent = 'Registration failed. Please check your credentials and try again.';

      if (errorBox) {
        errorBox.style.display = 'block';

        // hide error after 5 seconds
        setTimeout(() => {
          errorBox.style.display = 'none';
          errorMessage.textContent = '';
        }, 5000);
        registerForm.reset(); // clear the form fields
      }
    });

  });

});
