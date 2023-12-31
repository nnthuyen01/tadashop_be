package com.tadashop.nnt.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tadashop.nnt.model.Order;
import com.tadashop.nnt.model.User;
import com.tadashop.nnt.utils.constant.StateOrderConstant;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

	@Query("SELECT o FROM Order o WHERE UPPER(o.state) = UPPER(:state)")
	Page<Order> findByStateIgnoreCase(String state, Pageable page);
	
	@Query("SELECT o FROM Order o WHERE CAST(o.state AS text) LIKE %:state%")
	Page<Order> findByStateContaining(@Param("state") String state, Pageable page);

//	@Query(value = "SELECT * FROM orders where id_user = :id_user", nativeQuery = true)
//	List<Order> findByIdUser(@Param("id_user") Long idUser);

	@Query("SELECT o FROM Order o WHERE o.orderUser.id = :userId")
	List<Order> findByUserId(@Param("userId") Long userId);


	Page<Order> findByOrderUser(User orderUser, Pageable page);
	
	@Query("select count(o) from Order o where o.state = 'Paid' or o.state = 'Complete' ")
	Long countAllOrderPayment();
	
	@Query("select count(o) from Order o where o.state = 'Paid' and o.createTime>=:day")
	Long countAllTimeGreaterThanEqual(LocalDateTime day);
	
	@Query("select count(o) from Order o where (o.state = 'Paid' or o.state = 'Complete') and o.createTime >= :startOfDay and o.createTime <= :endOfDay")
	Long countAllTimeEqual(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

	@Query("select coalesce(SUM(o.totalPrice), 0) from Order o where (o.state = 'Paid' or o.state = 'Complete') and o.createTime >= :startOfDay and o.createTime <= :endOfDay")
	Double totalRevenue(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

	@Query("select coalesce(SUM(o.totalPrice), 0) from Order o where o.state = 'Paid' or o.state = 'Complete'")
	Double totalAllRevenue();

	List<Order> findByState(StateOrderConstant state);
	
	@Query("SELECT o FROM Order o WHERE lower(o.orderUser.username) like lower(concat('%', :username, '%'))")
	Page<Order> findByUsername(@Param("username") String username, Pageable pageable);
}
