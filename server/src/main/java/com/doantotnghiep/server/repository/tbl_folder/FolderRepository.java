package com.doantotnghiep.server.repository.tbl_folder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends MongoRepository<Folder, String> {
    Folder findByNameAndUserId(String id, String userId);

    Page<Folder> getAllByUserId(String userId, Pageable pageable);

    void deleteByIdAndUserId(String id, String userId);

    Folder findByIdAndUserId(String id, String userId);

    Integer countAllByUserId(String userId);

    List<Folder> findAllByUserId(String userId);

    void deleteAllByUserId(String userId);

}