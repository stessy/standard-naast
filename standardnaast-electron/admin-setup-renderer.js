const form = document.getElementById('setup');
const error = document.getElementById('error');
const button = form.querySelector('button');

form.addEventListener('submit', async event => {
  event.preventDefault();
  error.textContent = '';
  const password = document.getElementById('password');
  const confirmation = document.getElementById('confirmation');
  if (password.value !== confirmation.value) {
    error.textContent = 'Les mots de passe ne correspondent pas.';
    return;
  }
  button.disabled = true;
  try {
    const result = await window.adminSetup.create({
      username: document.getElementById('username').value,
      password: password.value
    });
    if (!result.success) error.textContent = result.error;
  } catch {
    error.textContent = 'Impossible de créer le compte. Veuillez réessayer.';
  } finally {
    password.value = '';
    confirmation.value = '';
    button.disabled = false;
  }
});
