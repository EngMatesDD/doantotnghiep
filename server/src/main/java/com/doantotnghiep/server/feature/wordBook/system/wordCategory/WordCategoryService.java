package com.doantotnghiep.server.feature.wordBook.system.wordCategory;

import com.doantotnghiep.server.exception.ResponseException;
import com.doantotnghiep.server.feature.wordBook.system.wordCategory.response.AllWordCategory;
import com.doantotnghiep.server.feature.wordBook.system.wordCategory.response.AllWordInCategory;
import com.doantotnghiep.server.repository.tbl_word.Word;
import com.doantotnghiep.server.repository.tbl_word.WordRepository;
import com.doantotnghiep.server.repository.tbl_word_category.WordCategory;
import com.doantotnghiep.server.repository.tbl_word_category.WordCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.ArrayList;
import java.util.Date;

@Service
@RequiredArgsConstructor
@ControllerAdvice
public class WordCategoryService {
    private final WordCategoryRepository wordCategoryRepository;
    private final WordRepository wordRepository;

    public AllWordCategory getAllWordCategory(Integer page, Integer size) {
        Pageable paging = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<WordCategory> wordCategories = wordCategoryRepository.findAll(paging);

        return AllWordCategory.builder()
                .wordCategories(wordCategories.getContent())
                .total((int) wordCategories.getTotalElements())
                .totalPage(wordCategories.getTotalPages())
                .build();

    }

    public WordCategory createWordCategory(String name) throws ResponseException {
        WordCategory wordCategory = wordCategoryRepository.findByName(name);
        if (wordCategory != null) {
            throw new ResponseException("Category already exists", HttpStatus.BAD_REQUEST, 400);
        }

        WordCategory newWordCategory = WordCategory.builder()
                .name(name)
                .createdAt(new Date())
                .updatedAt(new Date())
                .wordIds(new ArrayList<>())
                .build();
        return wordCategoryRepository.save(newWordCategory);
    }

    public WordCategory getWordCategoryByName(String name) throws ResponseException {
        WordCategory wordCategory = wordCategoryRepository.findByName(name);
        if (wordCategory == null) {
            throw new ResponseException("Category not found", HttpStatus.BAD_REQUEST, 400);
        }
        return wordCategory;
    }

    public WordCategory getWordCategoryById(String id) throws ResponseException {
        WordCategory wordCategory = wordCategoryRepository.findById(id).orElse(null);
        if (wordCategory == null) {
            throw new ResponseException("Category not found", HttpStatus.BAD_REQUEST, 404);
        }
        return wordCategory;
    }

    public WordCategory updateWordCategory(String id, String name) throws ResponseException {
        WordCategory wordCategory = wordCategoryRepository.findById(id).orElse(null);
        if (wordCategory == null) {
            throw new ResponseException("Category not found", HttpStatus.BAD_REQUEST, 404);
        }

        WordCategory categoryExist = wordCategoryRepository.findByName(name);
        if (categoryExist != null && !categoryExist.getId().equals(id)) {
            throw new ResponseException("Category already exists", HttpStatus.BAD_REQUEST, 400);
        }

        wordCategory.setName(name);
        wordCategory.setUpdatedAt(new Date());
        return wordCategoryRepository.save(wordCategory);
    }

    public WordCategory deleteWordCategory(String id) throws ResponseException {
        WordCategory wordCategory = wordCategoryRepository.findById(id).orElse(null);
        if (wordCategory == null) {
            throw new ResponseException("Category not found", HttpStatus.BAD_REQUEST, 404);
        }
        wordCategoryRepository.delete(wordCategory);
        return wordCategory;
    }

    public AllWordInCategory getAllWordInCategory(String categoryId, Integer page, Integer size) throws ResponseException {
        WordCategory wordCategory = wordCategoryRepository.findById(categoryId).orElse(null);
        if (wordCategory == null) {
            throw new ResponseException("Category not found", HttpStatus.BAD_REQUEST, 404);
        }

        Pageable paging = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Word> words = wordRepository.findAllByIdIn(wordCategory.getWordIds(), paging);

        System.out.println(words);

        return AllWordInCategory.builder()
                .category(wordCategory.getName())
                .words(words.getContent())
                .total((int) words.getTotalElements())
                .totalPage(words.getTotalPages())
                .build();
    }

}
