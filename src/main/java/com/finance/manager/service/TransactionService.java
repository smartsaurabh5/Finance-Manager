package com.finance.manager.service;

import com.finance.manager.dto.PageResponse;
import com.finance.manager.dto.TransactionRequest;
import com.finance.manager.dto.TransactionResponse;
import com.finance.manager.entity.Category;
import com.finance.manager.entity.CategoryType;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.exception.ForbiddenException;
import com.finance.manager.exception.ResourceNotFoundException;
import com.finance.manager.repository.CategoryRepository;
import com.finance.manager.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<TransactionResponse> getTransactions(User user, LocalDate startDate, LocalDate endDate, Long categoryId, CategoryType type) {
        validateFilters(startDate, endDate, categoryId, type);
        return findTransactions(user, startDate, endDate, categoryId, type).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public PageResponse<TransactionResponse> getTransactions(User user, LocalDate startDate, LocalDate endDate,
                                                             Long categoryId, CategoryType type, Pageable pageable) {
        validateFilters(startDate, endDate, categoryId, type);
        Page<Transaction> page;
        if (categoryId != null && startDate != null && endDate != null) {
            page = transactionRepository.findByUserAndCategoryAndDateBetween(user, getCategoryById(categoryId, user), startDate, endDate, pageable);
        } else if (categoryId != null) {
            page = transactionRepository.findByUserAndCategory(user, getCategoryById(categoryId, user), pageable);
        } else if (type != null && startDate != null && endDate != null) {
            page = transactionRepository.findByUserAndCategoryTypeAndDateBetween(user, type, startDate, endDate, pageable);
        } else if (type != null) {
            page = transactionRepository.findByUserAndCategoryType(user, type, pageable);
        } else if (startDate != null && endDate != null) {
            page = transactionRepository.findByUserAndDateBetween(user, startDate, endDate, pageable);
        } else {
            page = transactionRepository.findByUser(user, pageable);
        }
        return new PageResponse<>(page.getContent().stream().map(this::mapToResponse).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    public List<TransactionResponse> getRecentTransactions(User user) {
        return transactionRepository.findTop5ByUserOrderByDateDesc(user).stream().map(this::mapToResponse).toList();
    }

    private List<Transaction> findTransactions(User user, LocalDate startDate, LocalDate endDate, Long categoryId, CategoryType type) {
        if (categoryId != null && type != null) {
            throw new IllegalArgumentException("Filter by either categoryId or type, not both");
        }
        List<Transaction> transactions;
        if (categoryId != null && startDate != null && endDate != null) {
            transactions = transactionRepository.findByUserAndCategoryAndDateBetweenOrderByDateDesc(user, getCategoryById(categoryId, user), startDate, endDate);
        } else if (categoryId != null) {
            transactions = transactionRepository.findByUserAndCategoryOrderByDateDesc(user, getCategoryById(categoryId, user));
        } else if (type != null && startDate != null && endDate != null) {
            transactions = transactionRepository.findByUserAndCategoryTypeAndDateBetweenOrderByDateDesc(user, type, startDate, endDate);
        } else if (type != null) {
            transactions = transactionRepository.findByUserAndCategoryTypeOrderByDateDesc(user, type);
        } else if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate);
        } else {
            transactions = transactionRepository.findByUserOrderByDateDesc(user);
        }
        return transactions;
    }

    private void validateFilters(LocalDate startDate, LocalDate endDate, Long categoryId, CategoryType type) {
        if (categoryId != null && type != null) {
            throw new IllegalArgumentException("Filter by either categoryId or type, not both");
        }
        if ((startDate == null) != (endDate == null)) {
            throw new IllegalArgumentException("Both startDate and endDate are required for date filtering");
        }
        if (startDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before or equal to endDate");
        }
    }

    public TransactionResponse getTransaction(Long id, User user) {
        return mapToResponse(findTransaction(id, user));
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request, User user) {
        if (request.getAmount() == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        if (request.getDate() == null) {
            throw new IllegalArgumentException("Date is required");
        }
        if (request.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the future");
        }
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setDate(request.getDate());
        transaction.setDescription(request.getDescription());
        transaction.setCategory(getCategory(request, user));
        transaction.setUser(user);
        return mapToResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request, User user) {
        Transaction transaction = findTransaction(id, user);
        if (request.getDate() != null) {
            throw new IllegalArgumentException("Transaction date cannot be updated");
        }
        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }
        if (request.getCategoryId() != null || (request.getCategory() != null && !request.getCategory().isBlank())) {
            transaction.setCategory(getCategory(request, user));
        }
        return mapToResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public void deleteTransaction(Long id, User user) {
        transactionRepository.delete(findTransaction(id, user));
    }

    private Transaction findTransaction(Long id, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You don't have permission to access this transaction");
        }
        return transaction;
    }

    private Category getCategory(TransactionRequest request, User user) {
        Category category;
        if (request.getCategoryId() != null) {
            category = getCategoryById(request.getCategoryId(), user);
        } else if (request.getCategory() != null && !request.getCategory().isBlank()) {
            category = categoryRepository.findByNameIgnoreCaseAndUser(request.getCategory(), user)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        } else {
            throw new IllegalArgumentException("Category is required");
        }
        if (request.getType() != null && category.getType() != request.getType()) {
            throw new IllegalArgumentException("Transaction type must match category type");
        }
        return category;
    }

    private Category getCategoryById(Long id, User user) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (!category.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You don't have permission to access this category");
        }
        return category;
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        Category category = transaction.getCategory();
        return new TransactionResponse(transaction.getId(), transaction.getAmount(), transaction.getDate(),
                category.getName(), transaction.getDescription(), category.getType());
    }
}
