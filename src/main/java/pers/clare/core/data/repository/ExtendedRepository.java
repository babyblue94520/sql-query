package pers.clare.core.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * 將save拆成insert 跟 update
 */
@NoRepositoryBean
public interface ExtendedRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
	/**
	 *
	 * update
	 * 修改
	 * @param entity
	 * @return
	 */
	public <S extends T> S update(S entity);
	/**
	 *
	 * insert 
	 * 新增
	 * @param entity
	 * @return
	 */
	public <S extends T> S insert(S entity);
	/**
	 * 
	 * insert 
	 * 大量新增
	 * @param entites
	 * @return
	 */
	public <S extends T> List<S> insert(Iterable<S> entites);
	/**
	 * 
	 * update 
	 * 大量修改
	 * @param entites
	 * @return
	 */
	public <S extends T> List<S> update(Iterable<S> entites);
}
