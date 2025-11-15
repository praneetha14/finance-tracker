<html>
<head>
    <title>Monthly Expense Report</title>
</head>
<body style="font-family: Arial, sans-serif; margin: 20px; color: #2c3e50;">
    <h2 style="color: #1a5276; border-bottom: 2px solid #3498db; padding-bottom: 5px;">
    Monthly Expense Report - ${month}/${year}
    </h2>
    <div style="margin-top: 20px; line-height: 1.8;">
        <p><strong>Name:</strong> ${firstName} ${lastName}</p>
        <p><strong>Email:</strong> ${email}</p>
        <p><strong>Mobile:</strong> ${mobile}</p>
        <p><strong>Salary:</strong> ₹${salary}</p>
    </div>
    <div style="margin-top: 20px; line-height: 1.8;">
        <h3 style="color: #21618c; margin-bottom: 8px;">Summary</h3>
        <p><strong>Total Expected Expenses:</strong> ₹${totalExpectedExpenses}</p>
        <p><strong>Total Default Expenses:</strong> ₹${totalDefaultExpenses}</p>
        <p><strong>Actual Expenses:</strong> ₹${actualExpenses}</p>
        <p><strong>Expense Change from previous month(%):</strong> ${percentageChange}%</p>
        <p><strong>Expected Savings:</strong> ₹${totalExpectedSavings}</p>
        <p><strong>Actual Savings:</strong> ₹${actualSavings}</p>
        <p><strong>Savings Change from previous month (%):</strong> ${percentageChangeSavings}%</p>
        <p><strong>Previous Month Expenses:</strong> ₹${previousMonthExpenses}</p>
        <p><strong>Previous Month Savings:</strong> ₹${previousMonthSavings}</p>
    </div>
    <h3 style="margin-top: 30px; color: #1f618d;">Expense Breakdown</h3>
    <table style="width: 100%; border-collapse: collapse; margin-top: 10px; font-size: 13px;">
        <thead>
        <tr style="background-color: #f4f6f7;">
            <th style="border: 1px solid #ccc; padding: 8px; text-align: left;">Expense Name</th>
            <th style="border: 1px solid #ccc; padding: 8px; text-align: right;">Expected Cost</th>
            <th style="border: 1px solid #ccc; padding: 8px; text-align: center;">Is Default</th>
            <th style="border: 1px solid #ccc; padding: 8px; text-align: right;">Actual Cost</th>
            <th style="border: 1px solid #ccc; padding: 8px; text-align: right;">Change from expected to actual expense(Current Month %)</th>
            <th style="border: 1px solid #ccc; padding: 8px; text-align: right;">Previous Month Cost</th>
            <th style="border: 1px solid #ccc; padding: 8px; text-align: right;">Change From Previous Month (%)</th>
        </tr>
        </thead>
        <tbody>
        <#list expenseItems as e>
            <tr>
                <td style="border: 1px solid #ddd; padding: 8px;">${e.expenseName}</td>
                <td style="border: 1px solid #ddd; padding: 8px; text-align: right;">₹${e.expectedCost}</td>
                <td style="border: 1px solid #ddd; padding: 8px; text-align: center;">${e.isDefault}</td>
                <td style="border: 1px solid #ddd; padding: 8px; text-align: right;">₹${e.actualCost}</td>
                <td style="border: 1px solid #ddd; padding: 8px; text-align: right;">${e.percentageChangeForCurrentMonth}%</td>
                <td style="border: 1px solid #ddd; padding: 8px; text-align: right;">₹${e.previousMonthCost}</td>
                <td style="border: 1px solid #ddd; padding: 8px; text-align: right;">${e.percentageChangeFromPreviousMonth}%</td>
            </tr>
        </#list>
        </tbody>
    </table>
    <p style="margin-top: 25px; font-size: 12px; color: #7f8c8d; text-align: center;">
        Report generated on ${.now?string["dd MMM yyyy, HH:mm"]} by Finance Tracker
    </p>
</body>
</html>
