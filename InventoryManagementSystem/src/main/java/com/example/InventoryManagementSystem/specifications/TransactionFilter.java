package com.example.InventoryManagementSystem.specifications;

import com.example.InventoryManagementSystem.models.Transaction;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionFilter {

    public static Specification<Transaction> byFilter(String searchValue) {

        return (root, query, criteriaBuilder) -> {
            if (searchValue == null || searchValue.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String searchPattern = "%" + searchValue.toLowerCase() + "%";

            //create a list of search pattern
            List<Predicate> predicateList = new ArrayList<>();

            //search within Transaction fields
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern));
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("note")), searchPattern));
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("transactionType").as(String.class)), searchPattern));
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("transactionStatus").as(String.class)), searchPattern));
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("updatedAt").as(String.class)), searchPattern));
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("createdAt").as(String.class)), searchPattern));

            // if no matches, join safely to check the USER fields
            if (root.getJoins().stream().noneMatch(j -> j.getAttribute().getName().equals("user"))) {
                root.join("user", JoinType.LEFT);
            }

            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("user", JoinType.LEFT).get("name")), searchPattern));
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("user", JoinType.LEFT).get("email")), searchPattern));
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("user", JoinType.LEFT).get("contactNumber")), searchPattern));


            // if no matches, join safely to check the SUPPLIER fields
            if (root.getJoins().stream().noneMatch(j -> j.getAttribute().getName().equals("supplier"))) {
                root.join("supplier", JoinType.LEFT);
            }

            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("supplier", JoinType.LEFT).get("name")), searchPattern));
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("supplier", JoinType.LEFT).get("email")), searchPattern));
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("supplier", JoinType.LEFT).get("contactNumber")), searchPattern));

            // if no matches, join safely to check the PRODUCT fields
            if (root.getJoins().stream().noneMatch(j -> j.getAttribute().getName().equals("product"))) {
                root.join("product", JoinType.LEFT);
            }

            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("product", JoinType.LEFT).get("name")), searchPattern));
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("product", JoinType.LEFT).get("sku")), searchPattern));
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("product", JoinType.LEFT).get("price")), searchPattern));
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("product", JoinType.LEFT).get("stockQuantity")), searchPattern));
            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("product", JoinType.LEFT).get("description")), searchPattern));

            // if no matches, join safely to check the CATEGORY fields
            if (root.getJoins().stream().noneMatch(j -> j.getAttribute().getName().equals("product")) &&
                    root.join("product").getJoins().stream().noneMatch(j -> j.getAttribute().getName().equals("category"))) {
                root.join("product", JoinType.LEFT).join("category", JoinType.LEFT);
            }

            predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.join("product", JoinType.LEFT).join("category", JoinType.LEFT).get("name")), searchPattern));

            //combine all predicts
            return criteriaBuilder.or(predicateList.toArray(new Predicate[0]));
        };
    }

    //search by month and year
    public static Specification<Transaction> byMonthAndYear(int month, int year) {
        return (root, query, criteriaBuilder) -> {
            Expression<Integer> monthExpression = criteriaBuilder.function("month", Integer.class, root.get("createdAt"));
            Expression<Integer> yearExpression = criteriaBuilder.function("year", Integer.class, root.get("createdAt"));

            Predicate monthPredicate = criteriaBuilder.equal(monthExpression, month);
            Predicate yearPredicate = criteriaBuilder.equal(yearExpression, year);

            return criteriaBuilder.and(monthPredicate, yearPredicate);
        };
    }

}
