package com.finance.manager.service;

import com.finance.manager.dto.DashboardSummaryResponse;
import com.finance.manager.dto.TransactionResponse;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Map<String, Object> getMonthlyReport(User user, int year, int month) {
        validateYear(year);
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        Map<String, Object> report = generateReport(user, yearMonth.atDay(1), yearMonth.atEndOfMonth());
        report.put("month", month);
        report.put("year", year);
        return report;
    }

    public Map<String, Object> getYearlyReport(User user, int year) {
        validateYear(year);
        Map<String, Object> report = generateReport(user, LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
        report.put("year", year);
        return report;
    }

    public DashboardSummaryResponse getDashboardSummary(User user) {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = LocalDate.now();
        List<Transaction> transactions = transactionRepository.findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate);
        BigDecimal income = total(transactions, CategoryType.INCOME);
        BigDecimal expenses = total(transactions, CategoryType.EXPENSE);
        List<TransactionResponse> recent = transactionRepository.findTop5ByUserOrderByDateDesc(user).stream()
                .map(t -> new TransactionResponse(t.getId(), t.getAmount(), t.getDate(), t.getCategory().getName(),
                        t.getDescription(), t.getCategory().getType()))
                .toList();
        return new DashboardSummaryResponse(income, expenses, income.subtract(expenses), recent);
    }

    public String exportMonthlyCsv(User user, int year, int month) {
        validateYear(year);
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        List<Transaction> transactions = transactionRepository.findByUserAndDateBetweenOrderByDateDesc(
                user, yearMonth.atDay(1), yearMonth.atEndOfMonth());
        StringBuilder csv = new StringBuilder("id,date,type,category,amount,description\n");
        for (Transaction transaction : transactions) {
            csv.append(transaction.getId()).append(',')
                    .append(transaction.getDate()).append(',')
                    .append(transaction.getCategory().getType()).append(',')
                    .append(escape(transaction.getCategory().getName())).append(',')
                    .append(transaction.getAmount()).append(',')
                    .append(escape(transaction.getDescription()))
                    .append('\n');
        }
        return csv.toString();
    }

    private Map<String, Object> generateReport(User user, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate);
        Map<String, BigDecimal> totalIncome = groupByCategory(transactions, CategoryType.INCOME);
        Map<String, BigDecimal> totalExpenses = groupByCategory(transactions, CategoryType.EXPENSE);
        BigDecimal income = totalIncome.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal expenses = totalExpenses.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalIncome", totalIncome);
        response.put("totalExpenses", totalExpenses);
        response.put("netSavings", income.subtract(expenses));
        return response;
    }

    private Map<String, BigDecimal> groupByCategory(List<Transaction> transactions, CategoryType type) {
        return transactions.stream()
                .filter(t -> t.getCategory().getType() == type)
                .collect(Collectors.groupingBy(t -> t.getCategory().getName(), LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));
    }

    private BigDecimal total(List<Transaction> transactions, CategoryType type) {
        return transactions.stream()
                .filter(t -> t.getCategory().getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateYear(int year) {
        int currentYear = LocalDate.now().getYear();
        if (year < 2000 || year > currentYear + 1) {
            throw new IllegalArgumentException("Year must be between 2000 and " + (currentYear + 1));
        }
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
