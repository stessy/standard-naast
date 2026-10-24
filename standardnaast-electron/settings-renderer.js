const form = document.getElementById('settings-form');
const message = document.getElementById('message');
const save = document.getElementById('save');
const cancel = document.getElementById('cancel');

cancel.addEventListener('click', () => window.close());
document.addEventListener('keydown', event => {
  if (event.key === 'Escape' && !cancel.disabled) {
    event.preventDefault();
    window.close();
  }
});

function notify(text, error = false) {
  message.textContent = text;
  message.className = error ? 'error' : '';
}

async function loadSettings() {
  try {
    const result = await window.settings.get();
    if (!result.success) throw new Error(result.error);
    for (const key of ['backupsDir', 'logsDir']) document.getElementById(key).value = result.value[key];
    save.disabled = false;
  } catch (error) {
    notify(error.message, true);
  }
}

for (const button of document.querySelectorAll('[data-folder]')) {
  button.addEventListener('click', async () => {
    button.disabled = true;
    try {
      const result = await window.settings.choose(button.dataset.folder);
      if (!result.success) throw new Error(result.error);
      if (result.value) {
        document.getElementById(button.dataset.folder).value = result.value;
        notify('Modification non enregistrée.');
      }
    } catch (error) {
      notify(error.message, true);
    } finally {
      button.disabled = false;
    }
  });
}

form.addEventListener('submit', async event => {
  event.preventDefault();
  const buttons = Array.from(form.querySelectorAll('button'));
  buttons.forEach(button => { button.disabled = true; });
  try {
    const result = await window.settings.save({
      backupsDir: document.getElementById('backupsDir').value,
      logsDir: document.getElementById('logsDir').value
    });
    if (!result.success) throw new Error(result.error);
    window.close();
  } catch (error) {
    notify(error.message, true);
  } finally {
    buttons.forEach(button => { button.disabled = false; });
  }
});

loadSettings();
