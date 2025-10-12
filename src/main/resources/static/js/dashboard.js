// Dashboard JavaScript

document.addEventListener("DOMContentLoaded", () => {
    loadUserInfo();
    loadTransactions();
    loadSavingsGoals();

    // Goal form submission
    const goalForm = document.getElementById('goal-form');
    if (goalForm) {
        goalForm.addEventListener('submit', handleGoalSubmit);
    }
});

function getToken() {
    return localStorage.getItem('jwtToken');
}

function logout() {
    localStorage.removeItem('jwtToken');
    window.location.href = '/login';
}

function loadUserInfo() {
    const token = getToken();
    if (!token) {
        window.location.href = '/login';
        return;
    }

    fetch('/api/auth/me', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => response.json())
    .then(user => {
        document.getElementById('username-display').textContent = user.username;
        document.getElementById('email-display').textContent = user.email;
    })
    .catch(error => {
        console.error('Error loading user info:', error);
        logout();
    });
}

function loadTransactions() {
    const token = getToken();
    fetch('/api/transaction/fetchAll', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => response.json())
    .then(transactions => {
        displayTransactions(transactions);
        calculateSummary(transactions);
    })
    .catch(error => {
        console.error('Error loading transactions:', error);
    });
}

function displayTransactions(transactions) {
    const loadingMessage = document.getElementById('loading-message');
    const table = document.getElementById('transactions-table');
    const tbody = document.getElementById('transactions-body');
    const noTransactions = document.getElementById('no-transactions');

    loadingMessage.style.display = 'none';

    if (transactions.length === 0) {
        noTransactions.style.display = 'block';
        return;
    }

    table.style.display = 'table';
    tbody.innerHTML = '';

    transactions.forEach(transaction => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${new Date(transaction.date).toLocaleDateString()}</td>
            <td>${transaction.description}</td>
            <td>${transaction.amount}</td>
            <td>${transaction.type}</td>
            <td>${transaction.category}</td>
        `;
        tbody.appendChild(row);
    });
}

function calculateSummary(transactions) {
    let totalIncome = 0;
    let totalExpenses = 0;

    transactions.forEach(transaction => {
        if (transaction.type === 'INCOME') {
            totalIncome += transaction.amount;
        } else if (transaction.type === 'EXPENSE') {
            totalExpenses += transaction.amount;
        }
    });

    const balance = totalIncome - totalExpenses;

    document.getElementById('total-income').textContent = `$${totalIncome.toFixed(2)}`;
    document.getElementById('total-expenses').textContent = `$${totalExpenses.toFixed(2)}`;
    document.getElementById('balance').textContent = `$${balance.toFixed(2)}`;
}

function addTransaction() {
    // Placeholder for add transaction functionality
    alert('Add transaction functionality not implemented yet');
}

function loadSavingsGoals() {
    const token = getToken();
    fetch('/api/savings', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => response.json())
    .then(goals => {
        displaySavingsGoals(goals);
    })
    .catch(error => {
        console.error('Error loading savings goals:', error);
    });
}

function displaySavingsGoals(goals) {
    const container = document.getElementById('goals-container');
    container.innerHTML = '';

    if (goals.length === 0) {
        container.innerHTML = '<p>No savings goals found. Create your first goal!</p>';
        return;
    }

    goals.forEach(goal => {
        const goalDiv = document.createElement('div');
        goalDiv.className = 'goal-item';
        goalDiv.innerHTML = `
            <h4>${goal.goalName}</h4>
            <p>${goal.goalDescription}</p>
            <p>Current: $${goal.currentAmount} / Target: $${goal.targetAmount}</p>
            <p>Status: ${goal.status}</p>
            <div class="progress-bar">
                <div class="progress-fill" style="width: ${goal.completion}%"></div>
            </div>
            <button onclick="addToGoal(${goal.id})">Add Funds</button>
            <button onclick="withdrawFromGoal(${goal.id})">Withdraw</button>
            <button onclick="deleteGoal(${goal.id})">Delete</button>
        `;
        container.appendChild(goalDiv);
    });
}

function showAddGoalForm() {
    document.getElementById('add-goal-form').style.display = 'block';
}

function hideAddGoalForm() {
    document.getElementById('add-goal-form').style.display = 'none';
    document.getElementById('goal-form').reset();
}

function handleGoalSubmit(event) {
    event.preventDefault();

    const goalName = document.getElementById('goal-name').value;
    const goalDescription = document.getElementById('goal-description').value;
    const targetAmount = parseFloat(document.getElementById('target-amount').value);

    const goal = {
        goalName,
        goalDescription,
        targetAmount
    };

    const token = getToken();
    fetch('/api/savings', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(goal)
    })
    .then(response => response.json())
    .then(() => {
        hideAddGoalForm();
        loadSavingsGoals();
    })
    .catch(error => {
        console.error('Error creating goal:', error);
    });
}

function addToGoal(id) {
    const amount = prompt('Enter amount to add:');
    if (amount) {
        const token = getToken();
        fetch(`/api/savings/${id}/add`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ amount: parseFloat(amount) })
        })
        .then(() => loadSavingsGoals())
        .catch(error => console.error('Error adding to goal:', error));
    }
}

function withdrawFromGoal(id) {
    const amount = prompt('Enter amount to withdraw:');
    if (amount) {
        const token = getToken();
        fetch(`/api/savings/${id}/withdraw`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ amount: parseFloat(amount) })
        })
        .then(() => loadSavingsGoals())
        .catch(error => console.error('Error withdrawing from goal:', error));
    }
}

function deleteGoal(id) {
    if (confirm('Are you sure you want to delete this goal?')) {
        const token = getToken();
        fetch(`/api/savings/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
        .then(() => loadSavingsGoals())
        .catch(error => console.error('Error deleting goal:', error));
    }
}
