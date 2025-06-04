package com.sistema.educativo.repository;

import com.sistema.educativo.entity.ProfesorGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfesorGrupoRepository extends JpaRepository<ProfesorGrupo, Long> {
}