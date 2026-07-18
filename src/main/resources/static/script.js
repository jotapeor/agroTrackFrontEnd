function validarCampos() {
    const email = document.getElementById('email');
    const senha = document.getElementById('senha');
    const btn = document.getElementById('btn-logar');
    if (!email || !senha || !btn) return;
    const valido = email.value.trim().length > 0 && senha.value.trim().length > 0;
    btn.disabled = !valido;
    email.classList.toggle('success', email.value.trim().length > 0);
}

function toggleSenha(btnEl) {
    const wrapper = btnEl.closest('.at-input-wrapper');
    if (!wrapper) return;
    const input = wrapper.querySelector('input');
    const icon = btnEl.querySelector('i');
    if (input.type === 'password') {
        input.type = 'text';
        if (icon) icon.classList.replace('bi-eye', 'bi-eye-slash');
    } else {
        input.type = 'password';
        if (icon) icon.classList.replace('bi-eye-slash', 'bi-eye');
    }
}

function initSidebar() {
    const toggleBtn = document.getElementById('sidebarToggle');
    const sidebar = document.getElementById('atSidebar');
    const overlay = document.getElementById('sidebarOverlay');
    if (!toggleBtn || !sidebar) return;
    toggleBtn.addEventListener('click', () => {
        sidebar.classList.toggle('open');
        if (overlay) overlay.classList.toggle('active');
    });
    if (overlay) {
        overlay.addEventListener('click', () => {
            sidebar.classList.remove('open');
            overlay.classList.remove('active');
        });
    }
}

function initActiveNav() {
    const currentPath = window.location.pathname;
    document.querySelectorAll('.at-nav-link').forEach(link => {
        const href = link.getAttribute('href');
        if (href && (href === currentPath || (currentPath.startsWith(href) && href !== '/'))) {
            link.classList.add('active');
        }
    });
}

function initSubmitLoading() {
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', function () {
            const btn = this.querySelector('[type="submit"]:not(:disabled)');
            if (btn) { btn.disabled = true; btn.textContent = 'Aguarde...'; }
        });
    });
}

function validarNome(input) {
    const val = input.value.trim();
    const error = document.getElementById('nome-error');
    const hint = document.getElementById('nome-hint');
    if (val.length > 0 && val.length < 3) {
        input.classList.add('error'); input.classList.remove('success');
        if (error) { error.textContent = 'Mínimo de 3 caracteres.'; error.style.display = 'block'; }
        if (hint) hint.style.display = 'none';
    } else if (val.length >= 3) {
        input.classList.remove('error'); input.classList.add('success');
        if (error) error.style.display = 'none';
        if (hint) hint.style.display = 'none';
    } else {
        input.classList.remove('error', 'success');
        if (error) error.style.display = 'none';
        if (hint) hint.style.display = 'block';
    }
    toggleSubmitBtn();
}

function validarCampoObrigatorio(input) {
    const error = document.getElementById(input.id + '-error');
    if (input.value.trim() === '') {
        input.classList.add('error'); input.classList.remove('success');
        if (error) { error.textContent = 'Este campo é obrigatório.'; error.style.display = 'block'; }
    }
}

function initCadastroValidation() {
    const form = document.getElementById('form-novo-colaborador');
    if (!form) return;
    const nome = document.getElementById('nome');
    const email = document.getElementById('email');
    const senha = document.getElementById('senha');
    const confirmar = document.getElementById('confirmar-senha');
    if (nome) {
        nome.addEventListener('input', function () { validarNome(this); });
        nome.addEventListener('blur', function () { validarNome(this); });
    }
    [nome, email, senha, confirmar].forEach(function(el) {
        if (!el) return;
        el.addEventListener('blur', function() { if (this.value.trim() === '' && this.required) validarCampoObrigatorio(this); });
    });
    if (email) {
        let timeoutId;
        email.addEventListener('input', function () {
            clearTimeout(timeoutId);
            const val = this.value.trim();
            const error = document.getElementById('email-error');
            const success = document.getElementById('email-success');
            const hint = document.getElementById('email-hint');
            if (!val) {
                this.classList.remove('error', 'success');
                if (error) error.style.display = 'none';
                if (success) success.style.display = 'none';
                if (hint) hint.style.display = 'block';
                toggleSubmitBtn();
                return;
            }
            if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val)) {
                this.classList.add('error'); this.classList.remove('success');
                if (error) { error.textContent = 'Formato de e-mail inválido.'; error.style.display = 'block'; }
                if (success) success.style.display = 'none';
                if (hint) hint.style.display = 'none';
                toggleSubmitBtn();
                return;
            }
            timeoutId = setTimeout(() => verificarEmail(val, this, error, success, hint), 500);
        });
    }
    if (senha) {
        senha.addEventListener('input', function () {
            const val = this.value;
            const error = document.getElementById('senha-error');
            const hint = document.getElementById('senha-hint');
            if (val.length > 0 && val.length < 6) {
                this.classList.add('error'); this.classList.remove('success');
                if (error) { error.textContent = 'Mínimo de 6 caracteres.'; error.style.display = 'block'; }
                if (hint) hint.style.display = 'none';
            } else if (val.length >= 6) {
                this.classList.remove('error'); this.classList.add('success');
                if (error) error.style.display = 'none';
                if (hint) hint.style.display = 'none';
            } else {
                this.classList.remove('error', 'success');
                if (error) error.style.display = 'none';
                if (hint) hint.style.display = 'block';
            }
            verificarConfirmacaoSenha();
            toggleSubmitBtn();
        });
    }
    if (confirmar) {
        confirmar.addEventListener('input', function () { verificarConfirmacaoSenha(); toggleSubmitBtn(); });
    }
}

function verificarEmail(emailVal, input, errorEl, successEl, hintEl) {
    fetch('/api/autenticar/verificar-email?email=' + encodeURIComponent(emailVal))
        .then(r => r.json())
        .then(data => {
            if (data.disponivel) {
                input.classList.remove('error'); input.classList.add('success');
                if (errorEl) errorEl.style.display = 'none';
                if (successEl) { successEl.textContent = 'E-mail disponível.'; successEl.style.display = 'block'; }
                if (hintEl) hintEl.style.display = 'none';
            } else {
                input.classList.add('error'); input.classList.remove('success');
                if (errorEl) { errorEl.textContent = 'Este e-mail já está cadastrado.'; errorEl.style.display = 'block'; }
                if (successEl) successEl.style.display = 'none';
                if (hintEl) hintEl.style.display = 'none';
            }
            toggleSubmitBtn();
        })
        .catch(() => {
            input.classList.remove('error', 'success');
            if (errorEl) errorEl.style.display = 'none';
            if (successEl) successEl.style.display = 'none';
            if (hintEl) hintEl.style.display = 'block';
            toggleSubmitBtn();
        });
}

function verificarConfirmacaoSenha() {
    const senha = document.getElementById('senha');
    const confirmar = document.getElementById('confirmar-senha');
    const error = document.getElementById('confirmar-error');
    const success = document.getElementById('confirmar-success');
    const hint = document.getElementById('confirmar-hint');
    if (!confirmar || confirmar.value.trim() === '') {
        if (error) error.style.display = 'none';
        if (success) success.style.display = 'none';
        if (hint) hint.style.display = 'block';
        return;
    }
    if (senha.value === confirmar.value) {
        confirmar.classList.remove('error'); confirmar.classList.add('success');
        if (error) error.style.display = 'none';
        if (success) { success.textContent = 'Senhas conferem.'; success.style.display = 'block'; }
        if (hint) hint.style.display = 'none';
    } else {
        confirmar.classList.add('error'); confirmar.classList.remove('success');
        if (error) { error.textContent = 'Senhas não conferem.'; error.style.display = 'block'; }
        if (success) success.style.display = 'none';
        if (hint) hint.style.display = 'none';
    }
}

function toggleSubmitBtn() {
    const btn = document.getElementById('btn-cadastrar');
    if (!btn) return;
    const form = document.getElementById('form-novo-colaborador');
    if (!form) return;
    const inputs = form.querySelectorAll('input[required], select[required]');
    let allValid = true;
    inputs.forEach(input => { if (input.value.trim() === '' || input.classList.contains('error')) allValid = false; });
    const confirmar = document.getElementById('confirmar-senha');
    if (confirmar && (confirmar.classList.contains('error') || confirmar.value.trim() === '')) allValid = false;
    btn.disabled = !allValid;
}

function initFooterScroll() {

}

function initPrimeiroAcesso() {
    const precisaTrocar = document.getElementById('primeiroAcessoFlag');
    if (!precisaTrocar || precisaTrocar.value !== 'true') return;
    const overlay = document.createElement('div');
    overlay.className = 'at-modal-overlay';
    overlay.innerHTML = `<div class="at-modal">
        <div class="at-alert at-alert-warning" style="margin-bottom:16px;">
            <i class="bi bi-shield-exclamation"></i>
            <div><strong>Atenção!</strong><br><span>Por segurança, você precisa alterar sua senha antes de continuar.</span></div>
        </div>
        <form id="form-trocar-senha" novalidate>
            <div class="at-form-group">
                <label class="at-form-label" for="nova-senha">Nova senha <span class="required">*</span></label>
                <div class="at-input-wrapper">
                    <i class="bi bi-lock at-input-icon"></i>
                    <input type="password" id="nova-senha" class="at-form-control" placeholder="Mínimo 6 caracteres" minlength="6" required />
                    <button type="button" class="at-input-action" onclick="toggleSenha(this)" aria-label="Mostrar ou ocultar senha"><i class="bi bi-eye"></i></button>
                </div>
                <span class="at-form-hint" id="nova-senha-hint">Mínimo 6 caracteres.</span>
                <span class="at-form-error" id="nova-senha-error" style="display:none;"></span>
            </div>
            <div class="at-form-group">
                <label class="at-form-label" for="confirmar-nova-senha">Confirmar nova senha <span class="required">*</span></label>
                <div class="at-input-wrapper">
                    <i class="bi bi-lock at-input-icon"></i>
                    <input type="password" id="confirmar-nova-senha" class="at-form-control" placeholder="Repita a senha" required />
                </div>
                <span class="at-form-hint" id="confirmar-nova-hint">Repita a senha digitada.</span>
                <span class="at-form-error" id="confirmar-nova-error" style="display:none;"></span>
                <span class="at-form-success" id="confirmar-nova-success" style="display:none;"></span>
            </div>
            <div class="at-form-error" id="troca-senha-global-error" style="display:none; margin-bottom:12px;"></div>
            <button type="submit" class="btn btn-primary btn-block" id="btn-alterar-senha" disabled><i class="bi bi-check-lg"></i> Alterar Senha</button>
        </form>
    </div>`;
    document.body.appendChild(overlay);
    document.body.style.overflow = 'hidden';
    const novaSenha = document.getElementById('nova-senha');
    const confirmarNova = document.getElementById('confirmar-nova-senha');
    const btnAlterar = document.getElementById('btn-alterar-senha');
    function validar() {
        const sv = novaSenha.value;
        const cv = confirmarNova.value;
        const se = document.getElementById('nova-senha-error');
        const ce = document.getElementById('confirmar-nova-error');
        const cs = document.getElementById('confirmar-nova-success');
        let valido = true;
        if (sv.length > 0 && sv.length < 6) {
            novaSenha.classList.add('error'); novaSenha.classList.remove('success');
            se.textContent = 'Mínimo de 6 caracteres.'; se.style.display = 'block'; valido = false;
        } else if (sv.length >= 6) {
            novaSenha.classList.remove('error'); novaSenha.classList.add('success'); se.style.display = 'none';
        } else {
            novaSenha.classList.remove('error', 'success'); se.style.display = 'none'; valido = false;
        }
        if (cv.length > 0) {
            if (sv === cv && sv.length >= 6) {
                confirmarNova.classList.remove('error'); confirmarNova.classList.add('success'); ce.style.display = 'none'; cs.textContent = 'Senhas conferem.'; cs.style.display = 'block';
            } else {
                confirmarNova.classList.add('error'); confirmarNova.classList.remove('success'); ce.textContent = 'Senhas não conferem.'; ce.style.display = 'block'; cs.style.display = 'none'; valido = false;
            }
        } else {
            confirmarNova.classList.remove('error', 'success'); ce.style.display = 'none'; cs.style.display = 'none'; valido = false;
        }
        btnAlterar.disabled = !valido;
    }
    novaSenha.addEventListener('input', validar);
    confirmarNova.addEventListener('input', validar);
    document.getElementById('form-trocar-senha').addEventListener('submit', function (e) {
        e.preventDefault();
        const ge = document.getElementById('troca-senha-global-error');
        if (novaSenha.value !== confirmarNova.value) { ge.textContent = 'As senhas não conferem.'; ge.style.display = 'block'; return; }
        btnAlterar.disabled = true; btnAlterar.textContent = 'Alterando...'; ge.style.display = 'none';
        fetch('/api/alterar-senha', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ senha: novaSenha.value })
        })
        .then(r => { if (!r.ok) return r.text().then(t => { throw new Error(t); }); return r.text(); })
        .then(() => {
            overlay.remove();
            document.body.style.overflow = '';
            if (typeof showToast === 'function') showToast('Senha alterada com sucesso!', 'success');
        })
        .catch(err => { ge.textContent = err.message; ge.style.display = 'block'; btnAlterar.disabled = false; btnAlterar.innerHTML = '<i class=\"bi bi-check-lg\"></i> Alterar Senha'; });
    });
}

function showToast(message, type = 'success') {
    let container = document.getElementById('at-toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'at-toast-container';
        container.className = 'at-toast-container';
        document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    toast.className = `at-toast at-toast-${type}`;
    toast.innerHTML = `<i class="bi bi-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i> <span>${message}</span>`;
    container.appendChild(toast);
    setTimeout(() => {
        toast.classList.add('fade-out');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function initToasts() {
    const successMsg = document.getElementById('toast-success-message');
    if (successMsg && successMsg.value) {
        showToast(successMsg.value, 'success');
    }
    const errorMsg = document.getElementById('toast-error-message');
    if (errorMsg && errorMsg.value) {
        showToast(errorMsg.value, 'danger');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    initSidebar();
    initActiveNav();
    initSubmitLoading();
    initCadastroValidation();
    initFooterScroll();
    initPrimeiroAcesso();
    validarCampos();
    const emailInput = document.getElementById('email');
    const senhaInput = document.getElementById('senha');
    if (emailInput) emailInput.addEventListener('input', validarCampos);
    if (senhaInput) senhaInput.addEventListener('input', validarCampos);
    initToasts();
});
