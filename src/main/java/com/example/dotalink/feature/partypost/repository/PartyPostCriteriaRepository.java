package com.example.dotalink.feature.partypost.repository;

import com.example.dotalink.feature.partypost.model.PartyPost;
import com.example.dotalink.feature.partypost.model.PartyPostStatus;
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
import java.util.Locale;

@Repository
public class PartyPostCriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Page<PartyPost> search(String rank, String role, String region, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<PartyPost> query = cb.createQuery(PartyPost.class);
        Root<PartyPost> root = query.from(PartyPost.class);
        query.where(buildPredicates(cb, root, rank, role, region).toArray(new Predicate[0]));
        query.orderBy(cb.desc(root.get("createdAt")));

        TypedQuery<PartyPost> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<PartyPost> content = typedQuery.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<PartyPost> countRoot = countQuery.from(PartyPost.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(buildPredicates(cb, countRoot, rank, role, region).toArray(new Predicate[0]));
        long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb,
                                            Root<PartyPost> root,
                                            String rank,
                                            String role,
                                            String region) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("status"), PartyPostStatus.OPEN));

        if (rank != null && !rank.isBlank()) {
            predicates.add(cb.equal(cb.lower(root.get("requiredRank")), rank.trim().toLowerCase(Locale.ROOT)));
        }
        if (role != null && !role.isBlank()) {
            predicates.add(cb.equal(cb.lower(root.get("roleNeeded")), role.trim().toLowerCase(Locale.ROOT)));
        }
        if (region != null && !region.isBlank()) {
            predicates.add(cb.equal(cb.lower(root.get("region")), region.trim().toLowerCase(Locale.ROOT)));
        }
        return predicates;
    }
}
