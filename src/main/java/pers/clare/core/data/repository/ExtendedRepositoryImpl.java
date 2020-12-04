package pers.clare.core.data.repository;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 將save拆成insert 跟 update
 */
@Transactional(propagation = Propagation.NEVER)
public class ExtendedRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
		implements ExtendedRepository<T, ID> {
	private final EntityManager entityManager;

	public ExtendedRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {

		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	public ExtendedRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
		super(domainClass, entityManager);
		this.entityManager = entityManager;
	}

	@Override
	@Transactional
	public <S extends T> S insert(S entity) {
		entityManager.persist(entity);
		return entity;
	}
	
	@Override
	@Transactional
	public <S extends T> S update(S entity) {
		return entityManager.merge(entity);
	}

	@Override
	@Transactional
	public <S extends T> List<S> insert(Iterable<S> entities) {

		List<S> result = new ArrayList<S>();

		if (entities == null) {
			return result;
		}

		for (S entity : entities) {
			result.add(insert(entity));
		}

		return result;
	}

	@Override
	@Transactional
	public <S extends T> List<S> update(Iterable<S> entities) {

		List<S> result = new ArrayList<S>();

		if (entities == null) {
			return result;
		}

		for (S entity : entities) {
			result.add(update(entity));
		}
		
		return result;
	}
}
