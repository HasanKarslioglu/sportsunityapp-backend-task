    package com.sportsunity.backend.repository;

    import com.sportsunity.backend.model.Company;
    import com.sportsunity.backend.model.Task;
    import com.sportsunity.backend.model.User;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;

    import java.util.List;

    public interface TaskRepository extends JpaRepository<Task, Long> {
        List<Task> findByUser(User user);
        @Query("SELECT t FROM Task t WHERE t.user.company = :company")
        List<Task> findByCompany(@Param("company") Company company);
    }
