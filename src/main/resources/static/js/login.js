document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('loginForm');
    const errorMessage = document.getElementById('error-message');
    form.addEventListener('submit', function(event) {
        const userId = document.getElementById('userId').value.trim();
        const password = document.getElementById('password').value.trim();
        if (!userId || !password) {
            event.preventDefault();
            errorMessage.textContent = 'User ID and Password are required.';
            errorMessage.style.display = 'block';
        }
    });
}); 