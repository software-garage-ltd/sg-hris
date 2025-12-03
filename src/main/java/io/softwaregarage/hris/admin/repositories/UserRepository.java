package io.softwaregarage.hris.admin.repositories;

import io.softwaregarage.hris.admin.entities.User;
import io.softwaregarage.hris.profile.entities.EmployeeProfile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT u FROM User u WHERE u.username = :param")
    User findByUsername(@Param("param") String username);

    @Query("""
           SELECT u FROM User u
           WHERE u.employeeProfile = :param
           """)
    User findByEmployee(@Param("param") EmployeeProfile employeeProfile);

    @Query("""
           SELECT u FROM User u
           WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(u.employeeProfile.firstName) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(u.employeeProfile.middleName) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(u.employeeProfile.lastName) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(u.employeeProfile.suffix) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(u.employeeProfile.gender) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(u.emailAddress) LIKE LOWER(CONCAT('%', :param, '%'))
           OR LOWER(u.role) LIKE LOWER(CONCAT('%', :param, '%'))
           """)
    List<User> findByStringParameter(@Param("param") String parameter);

    @Query("""
           SELECT u FROM User u
           WHERE u.accountActive = :param
           OR u.passwordChanged = :param
           """)
    List<User> findByBooleanParameter(@Param("param") boolean param);
}
