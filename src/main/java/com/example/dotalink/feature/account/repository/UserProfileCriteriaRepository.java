package com.example.dotalink.feature.account.repository;

import com.example.dotalink.feature.account.model.UserProfile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserProfileCriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Page<UserProfile> search(String nickname, String rank, String region, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<UserProfile> cq = cb.createQuery(UserProfile.class);
        Root<UserProfile> root = cq.from(UserProfile.class);

        List<Predicate> predicates = buildPredicates(cb, root, nickname, rank, region);
        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(root.get("updatedAt")));

        TypedQuery<UserProfile> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<UserProfile> content = query.getResultList();

        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<UserProfile> countRoot = countCq.from(UserProfile.class);
        List<Predicate> countPredicates = buildPredicates(cb, countRoot, nickname, rank, region);
        countCq.select(cb.count(countRoot));
        countCq.where(countPredicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countCq).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb,
                                            Root<UserProfile> root,
                                            String nickname,
                                            String rank,
                                            String region) {
        List<Predicate> predicates = new ArrayList<>();

        if (nickname != null && !nickname.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("nickname")), "%" + nickname.trim().toLowerCase() + "%"));
        }
        if (rank != null && !rank.isBlank()) {
            predicates.add(cb.equal(cb.lower(root.get("rank")), rank.trim().toLowerCase()));
        }
        if (region != null && !region.isBlank()) {
            predicates.add(cb.equal(cb.lower(root.get("region")), region.trim().toLowerCase()));
        }

        return predicates;
    }
}
