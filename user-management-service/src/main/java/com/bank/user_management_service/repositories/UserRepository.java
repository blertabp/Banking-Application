package com.bank.user_management_service.repositories;

import com.bank.user_management_service.model.Role;
import com.bank.user_management_service.model.Status;
import com.bank.user_management_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByRoleAndStatus(Role role, Status status);


}
