package com.doantotnghiep.server.service.CrawWord;

import com.doantotnghiep.server.service.Translate.TranslateTextService;
import com.doantotnghiep.server.common.ErrorEnum.WordErrorEnum;
import com.doantotnghiep.server.exception.ResponseException;
import com.doantotnghiep.server.repository.tbl_word.Example;
import com.doantotnghiep.server.repository.tbl_word.Mean;
import com.doantotnghiep.server.repository.tbl_word.Type;
import com.doantotnghiep.server.repository.tbl_word.Word;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@ControllerAdvice
public class CrawWordService {
    private final static String url = "https://dictionary.cambridge.org";
    private final static String endpointWord = "/dictionary/english/";

    private final static String urlThirdPartyApi = "https://api.dictionaryapi.dev/api/v2/entries/en/";
    private final TranslateTextService translateTextService;


    public Word crawWord(String word) throws IOException, ResponseException {
        String urlWord = url + endpointWord + word;
        Document doc = Jsoup.connect(urlWord).get();
        Word wordCraw = new Word();
        wordCraw.setName(word);
        wordCraw.setPronunciationUKAudio(crawPronunciationUKAudio(doc));
        wordCraw.setPronunciationUSAudio(crawPronunciationUSAudio(doc));
        wordCraw.setPronunciationUS(crawPronunciationUS(doc));
        wordCraw.setPronunciationUK(crawPronunciationUK(doc));
        wordCraw.setTypes(crawTypes(doc));
        if (wordCraw.getTypes().isEmpty()) {
            throw new ResponseException(WordErrorEnum.WORD_NOT_FOUND, HttpStatus.NOT_FOUND, 404);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> synonyms = new ArrayList<>();
        List<String> antonyms = new ArrayList<>();
        try {
            JsonNode jsonNode = objectMapper.readTree(new URL(urlThirdPartyApi + word));
            for (JsonNode wordNode : jsonNode) {
                JsonNode meanings = wordNode.get("meanings");
                for (JsonNode meaning : meanings) {
                    synonyms = addSynonymOrAntonym(meaning, synonyms, "synonyms");
                    antonyms = addSynonymOrAntonym(meaning, antonyms, "antonyms");

                    JsonNode definitions = meaning.get("definitions");
                    for (JsonNode definition : definitions) {
                        synonyms = addSynonymOrAntonym(definition, synonyms, "synonyms");
                        antonyms = addSynonymOrAntonym(definition, antonyms, "antonyms");
                    }

                }
            }
        } catch (Exception e) {
        }


        synonyms = deleteDuplicate(synonyms);
        antonyms = deleteDuplicate(antonyms);
        wordCraw.setSynonyms(synonyms);
        wordCraw.setAntonyms(antonyms);
        return wordCraw;
    }

    public String crawPronunciationUKAudio(Document doc) {
        Element AudioPronunciationUK = doc.getElementById("audio1");
        if (AudioPronunciationUK == null) {
            return "";
        }
        Element sourcePronunciationUK = AudioPronunciationUK.getElementsByTag("source").first();
        if (sourcePronunciationUK == null) {
            return "";
        }
        String pronunciationUK = sourcePronunciationUK.attr("src");
        String urlPronunciationUK = url + pronunciationUK;
        return urlPronunciationUK;
    }

    public String crawPronunciationUSAudio(Document doc) {
        Element AudioPronunciationUS = doc.getElementById("audio2");
        if (AudioPronunciationUS == null) {
            return "";
        }
        Element sourcePronunciationUS = AudioPronunciationUS.getElementsByTag("source").first();
        if (sourcePronunciationUS == null) {
            return "";
        }
        String pronunciationUS = sourcePronunciationUS.attr("src");
        String urlPronunciationUS = url + pronunciationUS;
        return urlPronunciationUS;
    }

    public String crawPronunciationUK(Document doc) {
        Element pronunciationUKElement = doc.select(".uk.dpron-i").first();
        if (pronunciationUKElement == null) {
            return "";
        }
        Element pronunciationUKText = pronunciationUKElement.select(".pron.dpron").first();
        if (pronunciationUKText == null) {
            return "";
        }
        return pronunciationUKText.text();
    }

    public String crawPronunciationUS(Document doc) {
        Element pronunciationUSElement = doc.select(".us.dpron-i").first();
        if (pronunciationUSElement == null) {
            return "";
        }
        Element pronunciationUSText = pronunciationUSElement.select(".pron.dpron").first();
        if (pronunciationUSText == null) {
            return "";
        }
        return pronunciationUSText.text();
    }

    public List<Type> crawTypes(Document doc) throws IOException {
        List<Type> typesList = new ArrayList<>();
        Element typesElement = doc.select(".entry-body").first();
        if (typesElement == null) {
            return typesList;
        }
        List<Element> allTypeElement = typesElement.select(".pr.entry-body__el");

        for (Element typeElement : allTypeElement) {
            Element header = typeElement.select(".pos-header.dpos-h").first();
            String type = "";
            if (header != null) {
                type = header.select(".pos.dpos").first().text();
            }
            Type typeCraw = new Type(type);
            Element body = typeElement.select(".pos-body").first();
            if (body == null) {
                continue;
            }
            List<Mean> means = crawMeans(body);
            typeCraw.means = means;
            typesList.add(typeCraw);
        }
        return typesList;
    }

    public List<Mean> crawMeans(Element body) throws IOException {
        List<Mean> meansList = new ArrayList<>();
        List<Element> meansElement = body.select(".pr.dsense ");
        for (Element meanElement : meansElement) {
            Element meansBody = meanElement.select(".sense-body.dsense_b").first();
            if (meansBody == null) {
                continue;
            }
            List<Element> means = meansBody.select(".def-block.ddef_block ");
            for (Element mean : means) {
                Mean meanCraw = new Mean();
                Element levelAndConceptEnglishElement = mean.select(".ddef_h").first();
                if (levelAndConceptEnglishElement == null) {
                    continue;
                }
                Element levelElement = levelAndConceptEnglishElement.select("span.epp-xref.dxref").first();
                String level = "";
                if (levelElement != null) {
                    level = levelElement.text();
                }
                String conceptEnglish = "";
                Element conceptEnglishElement = levelAndConceptEnglishElement.select(".def.ddef_d.db").first();

                if (conceptEnglishElement != null) {
                    conceptEnglish = conceptEnglishElement.text();
                }

                meanCraw.conceptEnglish = conceptEnglish;
                meanCraw.conceptVietnamese = translateTextService.translateText(conceptEnglish, "vi");
                meanCraw.level = level;
                meanCraw.examples = crawExamples(mean);
                meansList.add(meanCraw);
            }
        }
        return meansList;
    }

    public List<Example> crawExamples(Element meanBody) throws IOException {
        List<Example> examplesList = new ArrayList<>();
        List<Element> examplesElement = meanBody.select(".eg.deg");
        for (Element exampleElement : examplesElement) {
            Example example = new Example();
            example.example = exampleElement.text();
            example.meanOfExample = translateTextService.translateText(example.example, "vi");
            examplesList.add(example);
        }
        return examplesList;
    }

    public List<String> deleteDuplicate(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String item : list) {
            if (!newList.contains(item)) {
                newList.add(item);
            }
        }
        return newList;
    }

    public List<String> addSynonymOrAntonym(JsonNode node, List<String> list, String type) {
        JsonNode nodeSynonymOrAntonym = node.get(type);
        if (nodeSynonymOrAntonym != null) {
            for (JsonNode item : nodeSynonymOrAntonym) {
                list.add(item.asText());
            }
        }
        return list;
    }
}
