package com.finance.manager.service;

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
        YearMonth yearMonth = YearMonth.of(year, month);
        Map<String, Object> report = generateReport(user, yearMonth.atDay(1), yearMonth.atEndOfMonth());
        report.put("month", month);
        report.put("year", year);
        return report;
    }

    public Map<String, Object> getYearlyReport(User user, int year) {
        Map<String, Object> report = generateReport(user, LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
        report.put("year", year);
        return report;
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
}
