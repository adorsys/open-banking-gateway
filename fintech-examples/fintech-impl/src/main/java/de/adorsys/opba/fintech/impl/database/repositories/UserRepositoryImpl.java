package de.adorsys.opba.fintech.impl.database.repositories;

import com.google.common.collect.Iterables;
import de.adorsys.opba.fintech.impl.database.entities.UserEntity;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl
        extends SimpleJpaRepository<UserEntity, String>
        implements UserRepository {

    private EntityManager entityManager;

    public UserRepositoryImpl(EntityManager entityManager) {
        super(UserEntity.class, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public Optional<UserEntity> findByXsrfToken(String xsrfToken) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserEntity> cQuery = builder.createQuery(getDomainClass());
        Root<UserEntity> root = cQuery.from(getDomainClass());
        cQuery
                .select(root)
                .where(builder
                        .like(root.<String>get("xsrfToken"), xsrfToken));
        TypedQuery<UserEntity> query = entityManager.createQuery(cQuery);
        List<UserEntity> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Iterables.getFirst(resultList, null));
    }
}
