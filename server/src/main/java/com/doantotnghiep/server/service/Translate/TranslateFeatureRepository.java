package com.doantotnghiep.server.service.Translate;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TranslateFeatureRepository extends MongoRepository<TranslateFeature, String> {
    TranslateFeature findByName(String name);
}
