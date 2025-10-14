// ========== AUTHENTICATION ==========
function getToken() {
    return localStorage.getItem('jwtToken');
}

// ========== FORMATTING HELPERS ==========
const currencyFormatter = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
});
const currencyFormatterCompact = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    notation: 'compact',
    maximumFractionDigits: 1,
});
function formatCurrency(value) {
    const n = Number(value);
    return currencyFormatter.format(Number.isFinite(n) ? n : 0);
}
function formatCurrencyCompact(value) {
    const n = Number(value);
    return currencyFormatterCompact.format(Number.isFinite(n) ? n : 0);
}
function formatPercent(value, digits = 0) {
    const n = Number(value);
    return `${Number.isFinite(n) ? n.toFixed(digits) : '0'}%`;
}

// ========== MENU TOGGLE ==========
function toggleMenu() {
    const menu = document.getElementById('dropdownMenu');
    menu.classList.toggle('active');
}

// Close menu when clicking outside
document.addEventListener('click', (e) => {
    const menu = document.getElementById('dropdownMenu');
    const hamburger = document.querySelector('.hamburger-btn');
    if (!menu.contains(e.target) && !hamburger.contains(e.target)) {
        menu.classList.remove('active');
    }
});

// ========== MENU FUNCTIONS ==========
function openProfile(event) {
    event.preventDefault();
    alert('Profile page coming soon!');
    document.getElementById('dropdownMenu').classList.remove('active');
}

function openSettings(event) {
    event.preventDefault();
    alert('Settings page coming soon!');
    document.getElementById('dropdownMenu').classList.remove('active');
}

function openReports(event) {
    event.preventDefault();
    alert('Financial Reports page coming soon!');
    document.getElementById('dropdownMenu').classList.remove('active');
}

function changePassword(event) {
    event.preventDefault();
    alert('Change Password page coming soon!');
    document.getElementById('dropdownMenu').classList.remove('active');
}

function logout() {
    localStorage.removeItem('jwtToken');
    window.location.href = '/login';
}

// ========== ACTION BUTTONS ==========
function addTransaction(event) {
    event.preventDefault();
    const modal = document.getElementById('addTransactionModal');
    if (modal) modal.style.display = 'flex';
}

function closeTransactionForm() {
    const modal = document.getElementById('addTransactionModal');
    if (modal) modal.style.display = 'none';
}

function viewAllTransactions(event) {
    event.preventDefault();
    window.location.href = '/transactions';
}

// ========== DATA LOADING ==========
function loadUserInfo() {
    const token = getToken();
    if (!token) {
        window.location.href = '/login';
        return;
    }

    // Get username from JWT or API
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        document.getElementById('username').textContent = payload.sub || 'User';
    } catch (e) {
        console.error('Error parsing token:', e);
    }
}

function loadTransactions() {
    const token = getToken();
    fetch('/api/transaction/fetchAll', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch transactions');
            return response.json();
        })
        .then(transactions => {
            displayTransactions(transactions);
            calculateSummary(transactions);
            updateChart(transactions);
        })
        .catch(error => {
            console.error('Error loading transactions:', error);
            document.getElementById('transactionList').innerHTML =
                '<div class="loading">Error loading transactions</div>';
        });
}

function displayTransactions(transactions) {
    const container = document.getElementById('transactionList');

    if (transactions.length === 0) {
        container.innerHTML = '<div class="loading">No transactions yet</div>';
        return;
    }

    const recent = transactions.slice(0, 5);
    container.innerHTML = recent.map(t => `
            <div class="transaction-item">
                <div class="transaction-info">
                    <div class="transaction-desc">${t.description}</div>
                    <div class="transaction-date">${new Date(t.date).toLocaleDateString()}</div>
                    <span class="transaction-category">${t.category}</span>
                </div>
                <div class="transaction-amount ${t.transactionType === 'INCOME' ? 'income' : 'expense'}">
                    ${t.transactionType === 'INCOME' ? '+' : '-'}${formatCurrency(Math.abs(t.amount))}
                </div>
            </div>
        `).join('');
}

function calculateSummary(transactions) {
    let totalIncome = 0;
    let totalExpenses = 0;

    transactions.forEach(t => {
        if (t.transactionType === 'INCOME') {
            totalIncome += parseFloat(t.amount);
        } else {
            totalExpenses += parseFloat(t.amount);
        }
    });

    const balance = totalIncome - totalExpenses;
    const savingsRate = totalIncome > 0 ? (balance / totalIncome) * 100 : 0;

    document.getElementById('totalIncome').textContent = formatCurrencyCompact(totalIncome);
    document.getElementById('totalExpenses').textContent = formatCurrencyCompact(totalExpenses);
    document.getElementById('netBalance').textContent = formatCurrencyCompact(balance);
    document.getElementById('savingsRate').textContent = formatPercent(savingsRate, 1);

    document.getElementById('incomeChange').textContent = `Total: ${formatCurrency(totalIncome)}`;
    document.getElementById('expensesChange').textContent = `Total: ${formatCurrency(totalExpenses)}`;
    document.getElementById('balanceChange').textContent = balance > 0 ? '✓ Positive balance!' : '⚠ Deficit';
}

function updateChart(transactions) {
    let totalIncome = 0;
    let totalExpenses = 0;

    transactions.forEach(t => {
        if (t.transactionType === 'INCOME') {
            totalIncome += parseFloat(t.amount);
        } else {
            totalExpenses += parseFloat(t.amount);
        }
    });

    const total = totalIncome + totalExpenses;
    const expensePercent = total > 0 ? ((totalExpenses / total) * 100).toFixed(0) : 0;
    const incomePercent = 100 - expensePercent;

    document.getElementById('expensePercent').textContent = expensePercent;
    document.getElementById('incomePercent').textContent = incomePercent;

    const expenseDegrees = (expensePercent / 100) * 360;
    document.getElementById('pieChart').style.background =
        `conic-gradient(#e74c3c 0deg ${expenseDegrees}deg, #2ecc71 ${expenseDegrees}deg 360deg)`;
}

function loadSavings() {
    const token = getToken();
    fetch('/api/savings/my-savings', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => {
            if (!response.ok) throw new Error('Failed to fetch savings');
            return response.json();
        })
        .then(savings => {
            displaySavings(savings);
        })
        .catch(error => {
            console.error('Error loading savings:', error);
            document.getElementById('savingsGrid').innerHTML =
                '<div class="loading">Error loading savings</div>';
        });
}

function addSavings(event) {
    event.preventDefault();
    const modal = document.getElementById('addSavingsModal');
    if (modal) modal.style.display = 'flex';
}

function closeSavingsForm() {
    const modal = document.getElementById('addSavingsModal');
    if (modal) modal.style.display = 'none';
}

function displaySavings(savings) {
    const container = document.getElementById('savingsGrid');

    if (savings.length === 0) {
        container.innerHTML = '<div class="loading">No savings yet. Create your first saving!';
        return;
    }

    container.innerHTML = savings.map(saving => {
        const progressPercent = saving.targetAmount > 0
            ? (saving.currentAmount / saving.targetAmount) * 100
            : 0;

        const isCompleted = saving.status === 'COMPLETED';
        const statusEmoji = isCompleted ? '✅' : '🟡';
        const progressClass = isCompleted ? 'completed' : 'in-progress';
        const savingsItemClass = isCompleted ? 'completed' : 'in-progress';

        return `
                <div class="savings-item ${savingsItemClass}">
                    <div class="savings-header">
                        <span class="savings-name">${saving.savingsName}</span>
                        <span class="savings-status">${statusEmoji}</span>
                    </div>
                    <div class="savings-progress">
                        <div class="progress-bar">
                            <div class="progress-fill ${progressClass}" style="width: ${Math.min(progressPercent, 100)}%"></div>
                        </div>
                        <div class="savings-amount">
                            <span class="amount-current">${formatCurrency(saving.currentAmount)}</span>
                            <span>/ ${formatCurrency(saving.targetAmount)}</span>
                        </div>
                    </div>
                </div>
            `;
    }).join('');
}

// ========== MODAL HELPERS ==========
// (Modal helpers are defined above in ACTION BUTTONS to avoid duplicate definitions)

// ========== INITIALIZE ==========
document.addEventListener('DOMContentLoaded', () => {
    loadUserInfo();
    loadTransactions();
    loadSavings();

    // Wire up transaction modal close on overlay click and form submit
    const modal = document.getElementById('addTransactionModal');
    if (modal) {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) closeTransactionForm();
        });
    }
    const form = document.getElementById('transactionForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const fd = new FormData(form);
            const data = Object.fromEntries(fd.entries());

            // Normalize types and shape expected by backend
            const amount = parseFloat(data.amount);
            const dateInput = data.date;
            const localDateTime = dateInput ? `${dateInput}T00:00:00` : null; // Spring LocalDateTime format

            if (!Number.isFinite(amount)) {
                alert('Please enter a valid amount.');
                return;
            }

            const payload = {
                description: data.description?.trim(),
                amount: amount,
                transactionType: data.transactionType,
                category: data.category?.trim(),
                date: localDateTime,
            };

            const token = getToken();
            if (!token) {
                window.location.href = '/login';
                return;
            }

            try {
                const res = await fetch('/api/transaction/create', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                    body: JSON.stringify(payload),
                });

                if (!res.ok) {
                    const msg = await res.text().catch(() => 'Failed to create transaction');
                    throw new Error(msg || `HTTP ${res.status}`);
                }

                // Success: reset form, close modal, refresh
                form.reset();
                closeTransactionForm();
                loadTransactions();
                alert('Transaction created successfully!');
            } catch (err) {
                console.error('Create transaction failed:', err);
                alert(typeof err?.message === 'string' ? err.message : 'Failed to create transaction');
            }
        });
    }

    // Wire up savings modal close on overlay click and form submit
    const savingsModal = document.getElementById('addSavingsModal');
    if (savingsModal) {
        savingsModal.addEventListener('click', (e) => {
            if (e.target === savingsModal) closeSavingsForm();
        });
    }
    const savingsForm = document.getElementById('savingsForm');
    if (savingsForm) {
        savingsForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const fd = new FormData(savingsForm);
            const data = Object.fromEntries(fd.entries());

            // Normalize types and shape expected by backend
            const currentAmount = parseFloat(data.currentAmount) || 0;
            const targetAmount = parseFloat(data.targetAmount);

            if (!Number.isFinite(targetAmount) || targetAmount <= 0) {
                alert('Please enter a valid target amount greater than 0.');
                return;
            }

            if (currentAmount < 0) {
                alert('Current amount cannot be negative.');
                return;
            }

            const payload = {
                savingsName: data.savingsName?.trim(),
                savingsDescription: data.savingsDescription?.trim(),
                currentAmount: currentAmount,
                targetAmount: targetAmount,
            };

            const token = getToken();
            if (!token) {
                window.location.href = '/login';
                return;
            }

            try {
                const res = await fetch('/api/savings/create', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                    body: JSON.stringify(payload),
                });

                if (!res.ok) {
                    const msg = await res.text().catch(() => 'Failed to create savings');
                    throw new Error(msg || `HTTP ${res.status}`);
                }

                // Success: reset form, close modal, refresh
                savingsForm.reset();
                closeSavingsForm();
                loadSavings();
                alert('Savings created successfully!');
            } catch (err) {
                console.error('Create savings failed:', err);
                alert(typeof err?.message === 'string' ? err.message : 'Failed to create savings');
            }
        });
    }

    // Refresh data every 30 seconds
    setInterval(() => {
        loadTransactions();
        loadSavings();
    }, 30000);
});