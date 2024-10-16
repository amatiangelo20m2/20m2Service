package com.ventimetriquadriconsulting.restaurant.restaurant.form.repository;

import com.ventimetriquadriconsulting.restaurant.restaurant.form.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {

    @Query("SELECT form FROM Form form where form.formCode = ?1")
    Optional<Form> findByFormCode(String formCode);

    @Query("SELECT form FROM Form form WHERE form.branchCode = ?1 AND form.formStatus <> 'CANCELLATO' ORDER BY form.formId DESC")
    Optional<List<Form>> findByBranchCodeAndFormStatusNotCancelled(String branchCode);


    boolean existsByFormCode(String string);
}
